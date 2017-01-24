package terminal;

import com.sixtyfour.plugins.impl.NullMemoryListener;

/**
 * Created by Administrator on 1/8/2017.
 */
class PeekPokeHandler extends NullMemoryListener
{
    private static final int SID_FIRST = 0xd400;
    private static final int SID_LAST = SID_FIRST + 0x1c;
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
        else if (addr == 53272)
        {
            //shell.matrix.setBaseAddress(value);
        }
        else if (addr == 646)
        {
            shell.matrix.setDefaultColorIndex((byte) value);
        }
        else if (addr >= 0xd800 && addr <= 0xdbe7)
        {
            try
            {
                shell.matrix.pokeColor(addr, (char) value);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            shell.panel.repaint();
        }
        else if (addr >= SID_FIRST && addr <= SID_LAST)   // SID
        {
            SidRunner.write(addr - SID_FIRST, value);
        }
        else if (addr < 0x4000) // possible screen RAM
        {
            C64VideoMatrix matrix = C64VideoMatrix.bufferFromAddress(addr);
            try
            {
                matrix.pokeFace(addr, (char) value);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            shell.panel.repaint();
        }
        else
        {
            System.out.println("Unknown Poke " +
                    String.format("$%04x",addr) +
                    " - " +
                    String.format("$%02x",value));
        }
    }

    @Override
    public Integer peek (int addr)
    {
        if (addr >= SID_FIRST && addr <= SID_LAST)   // SID
        {
            return SidRunner.read(addr - 0xd400);
        }
        else if (addr == 646)
        {
            return (int) shell.matrix.getDefaultColorIndex();
        }
        else if (addr >= 0xd800 && addr <= 0xdbe7)
        {
            try
            {
                return shell.matrix.peekColor(addr);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        else if (addr < 0x4000)
        {
            C64VideoMatrix matrix = C64VideoMatrix.bufferFromAddress(addr);
            try
            {
                return matrix.peekFace(addr);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        System.out.println("Unknown Peek @ "+String.format("$%04x",addr));
        return 0;
    }
}
