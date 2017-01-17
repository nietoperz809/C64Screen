import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
            addKeyListener(new KeyAdapter()
            {
                @Override
                public void keyTyped (KeyEvent e)
                {
                    matrix.putChar(e.getKeyChar());
                }

                @Override
                public void keyPressed (KeyEvent e)
                {
                    //System.out.println(e.getKeyCode());
                    switch (e.getKeyCode())
                    {
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
                    g.drawImage(writer.map.get(matrix.getVal(x,y)),
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
        f.add (new MyPanel());
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(600, 600);
        f.setVisible(true);
    }

    public static void main (String[] args)
    {
        new C64Screen();
    }
}
