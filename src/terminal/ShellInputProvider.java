package terminal;

import com.sixtyfour.plugins.InputProvider;

/**
 * Created by Administrator on 1/4/2017.
 */
class ShellInputProvider implements InputProvider
{
    private final C64Screen shellFrame;

    public ShellInputProvider (C64Screen shellFrame)
    {
        this.shellFrame = shellFrame;
    }

    @Override
    public Character readKey ()
    {
        return shellFrame.ringBuff.remove();
    }

    private String ringBuffToString()
    {
        StringBuilder sb = new StringBuilder();
        Character c;
        while ((c = shellFrame.ringBuff.remove()) != null)
        {
            sb.append(c);
        }
        return sb.toString();
    }

    @Override
    public String readString ()
    {
        shellFrame.ringBuff.clear();
        try
        {
            shellFrame.fromTextArea.take();
            return ringBuffToString();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
            return null;
        }
    }
}
