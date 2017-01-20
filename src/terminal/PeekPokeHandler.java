package terminal;

import com.sixtyfour.plugins.impl.NullMemoryListener;

/**
 * Created by Administrator on 1/8/2017.
 */
class PeekPokeHandler extends NullMemoryListener
{
    private static final int SID_FIRST = 0xd400;
    private static final int SID_LAST = SID_FIRST + 0x1c;
    private static final int SCREEN_FIRST = 0x400;
    private static final int SCREEN_LAST = SCREEN_FIRST+0x3ff;
    private static final int COLRAM_FIRST = 0xd800;
    private static final int COLRAM_LAST = COLRAM_FIRST+0x3ff;
    private final C64Screen shell;

    public PeekPokeHandler (C64Screen f)
    {
        shell = f;
    }

    @Override
    public void poke (int addr, int value)
    {
        if (addr == 53281)
        {
            CharacterWriter.getInstance().setBackgroundColor(value);
            shell.panel.repaint();
        }
        else if (addr == 646)
        {
            shell.matrix.setDefaultColorIndex((byte) value);
        }
        else if (addr >= COLRAM_FIRST && addr <= COLRAM_LAST)
        {
            shell.matrix.pokeColor(addr, (char)value);
            shell.panel.repaint();
        }
        else if (addr >= SCREEN_FIRST && addr <= SCREEN_LAST)
        {
            shell.matrix.pokeFace(addr, (char)value);
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
        else if (addr == 646)
        {
            return (int)shell.matrix.getDefaultColorIndex();
        }
        else if (addr >= COLRAM_FIRST && addr <= COLRAM_LAST)
        {
            return shell.matrix.peekColor(addr);
        }
        else if (addr >= SCREEN_FIRST && addr <= SCREEN_LAST)
        {
            return shell.matrix.peekFace(addr);
        }
        return 0;
    }
}
