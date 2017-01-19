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
        shellFrame.matrix.putString(txt);
    }

    @Override
    public void println (int id, String txt)
    {
        shellFrame.matrix.putString(txt+'\n');
    }

    @Override
    public int getCursor ()
    {
        Point p = shellFrame.matrix.getCursor();
        return p.x;
    }

}
