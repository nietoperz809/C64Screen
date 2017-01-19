package terminal;

import com.sixtyfour.plugins.impl.ConsoleOutputChannel;

import java.awt.*;


/**
 * Created by Administrator on 1/4/2017.
 */
public class ShellOutputChannel extends ConsoleOutputChannel
{
    private C64Screen shellFrame;

    public ShellOutputChannel (C64Screen sf)
    {
        this.shellFrame = sf;
    }

    @Override
    public void print (int id, String txt)
    {
        StringBuilder sb = new StringBuilder();
        for (int s=0; s<txt.length(); s++)
        {
            char c = txt.charAt(s);
            switch (c)  // handle control chars (colors only)
            {
                case 0x05:
                    shellFrame.matrix.setDefaultColorIndex((byte)1);
                    break;
                case 0x1c:
                    shellFrame.matrix.setDefaultColorIndex((byte)2);
                    break;
                case 0x1e:
                    shellFrame.matrix.setDefaultColorIndex((byte)5);
                    break;
                case 0x1f:
                    shellFrame.matrix.setDefaultColorIndex((byte)6);
                    break;
                case 0x81:
                    shellFrame.matrix.setDefaultColorIndex((byte)8);
                    break;
                case 0x90:
                    shellFrame.matrix.setDefaultColorIndex((byte)0);
                    break;
                case 0x95:
                    shellFrame.matrix.setDefaultColorIndex((byte)8);
                    break;
                case 0x96:
                    shellFrame.matrix.setDefaultColorIndex((byte)10);
                    break;
                case 0x97:
                    shellFrame.matrix.setDefaultColorIndex((byte)11);
                    break;
                case 0x98:
                    shellFrame.matrix.setDefaultColorIndex((byte)12);
                    break;
                case 0x99:
                    shellFrame.matrix.setDefaultColorIndex((byte)13);
                    break;
                case 0x9a:
                    shellFrame.matrix.setDefaultColorIndex((byte)14);
                    break;
                case 0x9b:
                    shellFrame.matrix.setDefaultColorIndex((byte)15);
                    break;
                case 0x9c:
                    shellFrame.matrix.setDefaultColorIndex((byte)4);
                    break;
                case 0x9e:
                    shellFrame.matrix.setDefaultColorIndex((byte)7);
                    break;
                case 0x9f:
                    shellFrame.matrix.setDefaultColorIndex((byte)3);
                    break;
                default:
                    sb.append(c);
            }
        }
        shellFrame.matrix.putString(sb.toString());
        shellFrame.panel.repaint();
    }

    @Override
    public void println (int id, String txt)
    {
        print (id, txt+'\n');
    }

    @Override
    public int getCursor ()
    {
        Point p = shellFrame.matrix.getCursor();
        return p.x;
    }

}
