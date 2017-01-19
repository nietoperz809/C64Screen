package terminal;


import misc.RingBuffer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.sun.glass.events.KeyEvent.VK_BACKSPACE;
import static java.awt.event.KeyEvent.VK_ENTER;

/**
 *
 */
public class C64Screen
{
//    BufferedImage canvas =
//            new BufferedImage(
//                    8*C64Matrix.CHARS_PER_LINE,
//                    8*C64Matrix.LINES_ON_SCREEN,
//                            TYPE_INT_ARGB);
    final C64Matrix matrix = new C64Matrix();
    final CharacterWriter writer = CharacterWriter.getInstance();
    final InputDispatcher dispatcher = new InputDispatcher(this);
    final ArrayBlockingQueue<char[]> fromTextArea = new ArrayBlockingQueue<>(20);
    final RingBuffer<Character> ringBuff = new RingBuffer<>(40);
    final MyPanel panel = new MyPanel();
//    void matrixToCanvas()
//    {
//        int ypos = 0;
//        for (int y=0; y<terminal.C64Matrix.LINES_ON_SCREEN; y++)
//        {
//            char[] row = matrix.get(y);
//            int xpos = 0;
//            for (int x=0; x<terminal.C64Matrix.CHARS_PER_LINE; x++)
//            {
//                writer.printImg (canvas, matrix.getVal(x,y), xpos, ypos);
//                xpos += 8;
//            }
//            ypos += 8;
//        }
//    }

    class MyPanel extends JPanel
    {
        boolean blinkflag;
        final static int SCALE=16;

        public MyPanel ()
        {
            setFocusable(true);
            requestFocusInWindow();
            setPreferredSize(new Dimension(
                    C64Matrix.CHARS_PER_LINE*SCALE,
                    C64Matrix.LINES_ON_SCREEN*SCALE));
            addKeyListener(new KeyAdapter()
            {
                void handleKey (KeyEvent e)
                {
                    char c = e.getKeyChar();
                    if (c == VK_ENTER)
                    {
                        char[] arr = matrix.getLastLine();
                        if (arr == null)
                            return;
                        try
                        {
                            fromTextArea.put(writer.mapCBMtoPC(arr));
                        }
                        catch (InterruptedException e1)
                        {
                            e1.printStackTrace();
                        }
                        //dispatcher.handleInput(writer.mapCBMtoPC(arr));
                        //System.out.println(new String(writer.mapCBMtoPC(arr)));
                        //System.out.println("LastChar: "+ (int)matrix.getLastChar());
                    }
                    else if (c == VK_BACKSPACE)
                    {
                        return;
                    }
                    ringBuff.add(c);
                    matrix.putChar(writer.mapPCtoCBM(c),
                            e.getKeyCode(), e.isActionKey());
                }

                void handleSpecialKeys (KeyEvent e)
                {
                    switch (e.getKeyCode())
                    {
                        case KeyEvent.VK_C:
                            if (e.isControlDown())
                            {
                                try
                                {
                                    dispatcher.basicRunner.getOlsenBasic().runStop();
                                }
                                catch (Exception ex)
                                {
                                    
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
                public void keyPressed (KeyEvent e)
                {
                    handleKey(e);
                    handleSpecialKeys(e);
                    repaint();
                }
            });

            ScheduledExecutorService scheduler =
                    Executors.newScheduledThreadPool(1);
            scheduler.scheduleAtFixedRate(() -> repaint(),
                    100,
                    500,
                    TimeUnit.MILLISECONDS);
        }

        @Override
        protected void paintComponent (Graphics g)
        {
            int ypos = 0;
            for (int y = 0; y<C64Matrix.LINES_ON_SCREEN; y++)
            {
                int xpos = 0;
                for (int x = 0; x<C64Matrix.CHARS_PER_LINE; x++)
                {
                    g.drawImage(writer.imageMap.get(matrix.getVal(x,y)),
                            xpos, ypos, SCALE, SCALE, this);
                    xpos += SCALE;
                }
                ypos += SCALE;
            }
            if (blinkflag)
            {
                Point p = matrix.getCursor();
                g.setColor(Color.GREEN);
                g.fillRect(p.x * SCALE, p.y * SCALE, SCALE, SCALE);
            }
            blinkflag = !blinkflag;
        }

        @Override
        public void update (Graphics g)
        {
            //super.update(g);
        }
    }

    public C64Screen ()
    {
        //matrix.putString("hallo");
        //matrixToCanvas();
        JFrame f = new JFrame();
        f.setLayout(new FlowLayout());
        f.add (panel);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(42*8*2, 28*8*2);
        f.setVisible(true);
    }

    public static void main (String[] args)
    {
        new C64Screen();
        SidRunner.start();
    }
}
