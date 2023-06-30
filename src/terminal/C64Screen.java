package terminal;


import misc.RingBuffer;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.sun.glass.events.KeyEvent.VK_BACKSPACE;
import static java.awt.event.KeyEvent.VK_ENTER;

/**
 *
 */
public class C64Screen {
    public static JFrame frame;
    final C64VideoMatrix matrix = C64VideoMatrix.bufferFromAddress(1024);
    final C64HiresMatrix hires = new C64HiresMatrix();
    final ArrayBlockingQueue<char[]> fromTextArea = new ArrayBlockingQueue<>(20);
    final RingBuffer<Character> ringBuff = new RingBuffer<>(40);
    final MyPanel panel = new MyPanel();
    private final CharacterWriter writer = CharacterWriter.getInstance();
    private final CommandLineDispatcher dispatcher = new CommandLineDispatcher(this);
    private boolean isHires = false;
    private boolean isEnabled = true;

    private C64Screen() {
        JFrame f = new JFrame();
        JMenuBar menubar = new JMenuBar();
        JMenu menu = new JMenu("menu ...");
        JMenuItem helpmenu = new JMenuItem("help ,,,");
        JCheckBoxMenuItem stopmenu = new JCheckBoxMenuItem("pause");
        JMenuItem copymenu = new JMenuItem("toClipboard");
        copymenu.addActionListener(e -> {
            matrix.copyToClipboard();
        });
        stopmenu.addActionListener(e -> {
            stopmenu.setState(stopmenu.getState());
            dispatcher.basicRunner.pause(stopmenu.getState());
        });
        helpmenu.addActionListener(e -> {
            String msg = "<html>" + "<u>Additional commands</u><br>shift/unshift - change font<br" +
                    "prettify - reformat code<br>" +
                    "renumber - adjust line numbers<br>" +
                    "dir [filter] - show current directory<br>" +
                    "cls - clear screen<br>" +
                    "speed n - set program execution speed<br>" +
                    "-----------------------<br>" +
                    "* right mouse button inserts string from clipboard<br>"+
                    "* drag/drop basic code into window then type run (try music.bas).<br>"
                    + "</html>";
            JOptionPane.showMessageDialog(null, msg, "InfoBox", JOptionPane.INFORMATION_MESSAGE);
        });
        menu.add(helpmenu);
        menu.add(stopmenu);
        menu.add(copymenu);
        menubar.add(menu);
        f.setJMenuBar(menubar);
        f.setLayout(new FlowLayout());
        f.add(panel);
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        f.setSize(43 * 8 * 2, 30 * 8 * 2);
        f.setVisible(true);
        frame = f;
    }

    public static void main(String[] args) {
        new C64Screen();
        SidRunner.start();
    }

    public void setHires(boolean b) {
        isHires = b;
    }

    public void setDisplayEnabled(boolean b) {
        isEnabled = b;
    }

    class MyPanel extends JPanel {
        final static int SCALE = 16;

        public MyPanel() {
            setDoubleBuffered(true);
            setFocusable(true);
            requestFocusInWindow();
            setPreferredSize(new Dimension(
                    C64VideoMatrix.CHARS_PER_LINE * SCALE,
                    C64VideoMatrix.LINES_ON_SCREEN * SCALE));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {  // insert string from clipboard
                    Transferable t = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
                    try {
                        if (t != null && t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                            String text = (String) t.getTransferData(DataFlavor.stringFlavor);
                            matrix.putString(text.trim());
                        }
                    } catch (Exception ex) {
                        System.out.println(ex);
                    }
                }
            });

            addKeyListener(new KeyAdapter() {
                void handleKey(KeyEvent e) {
                    char c = e.getKeyChar();
                    if (c == '\uFFFF')
                        return;
                    if (c == VK_ENTER) {
                        Character[] arr = matrix.readLine();
                        if (arr == null)
                            return;
                        try {
                            fromTextArea.put(writer.mapCBMtoPC(arr));
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                    } else if (c == VK_BACKSPACE) {
                        return;
                    }
                    if (e.isShiftDown())
                        ringBuff.add((char)(c + 128));
                    else
                        ringBuff.add(c);
                    matrix.putChar(writer.mapPCtoCBM(c),
                            e.getKeyCode(), e.isActionKey(), -1);
                }

                void handleSpecialKeys(KeyEvent e) {
                    switch (e.getKeyCode()) {
                        case KeyEvent.VK_C:
                            if (e.isControlDown()) {
                                //noinspection EmptyCatchBlock
                                try {
                                    dispatcher.basicRunner.getOlsenBasic().runStop();
                                } catch (Exception ex) {

                                }
                            }
                            break;

                        case KeyEvent.VK_BACK_SPACE:
                            matrix.backspace();
                            break;

                        case KeyEvent.VK_LEFT:
                            matrix.left();
                            break;

                        case KeyEvent.VK_RIGHT:
                            matrix.right();
                            break;

                        case KeyEvent.VK_UP:
                            matrix.up();
                            break;

                        case KeyEvent.VK_DOWN:
                            matrix.down();
                            break;
                    }
                }

                @Override
                public void keyPressed(KeyEvent e) {
                    handleKey(e);
                    handleSpecialKeys(e);
                    repaint();
                }
            });

            ScheduledExecutorService scheduler =
                    Executors.newScheduledThreadPool(1);
            scheduler.scheduleAtFixedRate(this::repaint,
                    100,
                    500,
                    TimeUnit.MILLISECONDS);

            new DropTarget(this, new DropTargetAdapter() {
                @Override
                public void drop(DropTargetDropEvent event) {
                    event.acceptDrop(DnDConstants.ACTION_COPY);
                    Transferable transferable = event.getTransferable();
                    DataFlavor[] flavors = transferable.getTransferDataFlavors();
                    for (DataFlavor flavor : flavors) {
                        try {
                            if (flavor.isFlavorJavaFileListType()) {
                                java.util.List<File> files = (java.util.List<File>) transferable.getTransferData(flavor);
                                File f = files.get(0);
                                dispatcher.progStore.load(f.getPath());
                                matrix.putString("Loaded: " + f.getName() + "\n" + ProgramStore.OK);
                                return; // only one file
                            }
                        } catch (Exception e) {
                            matrix.putString(ProgramStore.ERROR);
                        }
                    }
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            if (isEnabled) {
                if (isHires)
                    hires.render(g);
                else
                    matrix.render(g);
            }
        }

        @Override
        public void update(Graphics g) {
            //super.update(g);
        }
    }
}
