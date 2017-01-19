package terminal;

import com.sixtyfour.plugins.impl.NullMemoryListener;

/**
 * Created by Administrator on 1/8/2017.
 */
public class PeekPokeHandler extends NullMemoryListener
{
    public static final int SID_FIRST = 0xd400;
    public static final int SID_LAST = SID_FIRST + 0x1c;
    public static final int SCREEN_FIRST = 1024;
    public static final int SCREEN_LAST = SCREEN_FIRST+25*40;
    private C64Screen shell;

    public PeekPokeHandler (C64Screen f)
    {
        shell = f;
    }

    @Override
    public void poke (int addr, int value)
    {
        //System.out.println("poke "+addr);
        if (addr == 53281)
        {
            CharacterWriter.getInstance().setBackgroundColor(value);
            shell.panel.repaint();
        }
        else if (addr >= SCREEN_FIRST && addr <= SCREEN_LAST)
        {
            shell.matrix.poke(addr, (char)value);
            shell.panel.repaint();
        }
        else if (addr >= SID_FIRST && addr <= SID_LAST)   // SID
        {
            SidRunner.write(addr-SID_FIRST, value);
        }
    }

    @Override
    public Integer peek (int addr)
    {
        if (addr >= SID_FIRST && addr <= SID_LAST)   // SID
        {
            return SidRunner.read(addr-0xd400);
        }
        else if (addr >= SCREEN_FIRST && addr <= SCREEN_LAST)
        {
            return (int)shell.matrix.peek(addr);
        }
        return 0;
    }
}
