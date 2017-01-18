import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.sun.glass.events.KeyEvent.VK_BACKSPACE;
import static java.awt.event.KeyEvent.VK_ENTER;
import static java.awt.image.BufferedImage.TYPE_INT_ARGB;

/**
 *
 */
public class C64Screen
{
    BufferedImage canvas =
            new BufferedImage(
                    8*C64Matrix.CHARS_PER_LINE,
                    8*C64Matrix.LINES_ON_SCREEN,
                            TYPE_INT_ARGB);
    C64Matrix matrix = new C64Matrix();
    CharacterWriter writer = CharacterWriter.getInstance();

//    void matrixToCanvas()
//    {
//        int ypos = 0;
//        for (int y=0; y<C64Matrix.LINES_ON_SCREEN; y++)
//        {
//            char[] row = matrix.get(y);
//            int xpos = 0;
//            for (int x=0; x<C64Matrix.CHARS_PER_LINE; x++)
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
                        char[] arr = matrix.getCurrentLine();
                        for (int s=0; s<arr.length; s++)
                        {
                            Character c1 = writer.reverseKeyMap.get(arr[s]);
                            arr[s] = c1 == null ? arr[s] : c1;
                        }
                        System.out.println(new String(arr));
                    }
                    else if (c == VK_BACKSPACE)
                    {
                        return;
                    }
                    Character c1 = writer.keyMap.get(c);
                    c = c1 == null ? c : c1;
                    matrix.putChar(c, e.getKeyCode(), e.isActionKey());
                }

                void handleSpecialKeys (KeyEvent e)
                {
                    switch (e.getKeyCode())
                    {
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

            ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
            scheduler.scheduleAtFixedRate(new Runnable()
            {
                @Override
                public void run ()
                {
                    repaint();
                }
            }, 100, 500, TimeUnit.MILLISECONDS);
        }

//        @Override
//        protected void paintComponent (Graphics g)
//        {
//            g.drawImage(canvas, 0, 0, getWidth(), getHeight(), this);
//        }

        @Override
        protected void paintComponent (Graphics g)
        {
            int ypos = 0;
            for (int y = 0; y<C64Matrix.LINES_ON_SCREEN; y++)
            {
                char[] row = matrix.get(y);
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
        f.add (new MyPanel());
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(42*8*2, 28*8*2);
        f.setVisible(true);
    }

    public static void main (String[] args)
    {
        new C64Screen();
    }
}
