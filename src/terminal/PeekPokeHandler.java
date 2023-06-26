package terminal;

import com.sixtyfour.plugins.impl.NullMemoryListener;

/**
 * Created by Administrator on 1/8/2017.
 */
class PeekPokeHandler extends NullMemoryListener {
    private static final int SID_FIRST = 0xd400;
    private static final int SID_LAST = SID_FIRST + 0x1c;
    private final C64Screen shell;

    public PeekPokeHandler(C64Screen f) {
        shell = f;
    }

    @Override
    public void poke(int addr, int value) {
        if (addr == 199) {
            shell.matrix.setInverted(value != 0);
        }
        else if (addr == 53281) {
            CharacterWriter.getInstance().setBackgroundColor(value);
            shell.panel.repaint();
        } else if (addr == 53280) {
            C64Colors col = C64Colors.values()[value & 0x0f];
            C64Screen.frame.getContentPane().setBackground(col.getColor());
            C64Screen.frame.repaint();
        } else if (addr == 53265) {
            shell.setHires((value & 32) == 32);
            shell.setDisplayEnabled((value & 16) == 16);
        } else if (addr == 53272) {
            shell.hires.setAddress_8192((value & 8) == 8);
        } else if (addr == 646) {
            shell.matrix.setCurrentColorIndex((byte) value);
        } else if (addr >= 0xd800 && addr <= 0xdbe7) {
            try {
                shell.matrix.pokeColor(addr, (char) value);
            } catch (Exception e) {
                e.printStackTrace();
            }
            shell.panel.repaint();
        } else if (addr >= SID_FIRST && addr <= SID_LAST)   // SID
        {
            SidRunner.write(addr - SID_FIRST, value);
        } else if (addr < 0x4000) // possible screen RAM
        {
            if (addr >= 8192 && addr <= 16383) {
                shell.hires.poke(addr, (byte) value);
            }
            C64VideoMatrix matrix = C64VideoMatrix.bufferFromAddress(addr);
            try {
                matrix.pokeFace(addr, (char) value);
            } catch (Exception e) {
                e.printStackTrace();
            }
            shell.panel.repaint();
        } else {
            System.out.println("Unknown Poke " +
                    String.format("$%04x", addr) +
                    " - " +
                    String.format("$%02x", value));
        }
    }

    @Override
    public Integer peek(int addr) {
        if (addr >= SID_FIRST && addr <= SID_LAST)   // SID
        {
            return SidRunner.read(addr - 0xd400);
        } else if (addr == 646) {
            return (int) shell.matrix.getDefaultColorIndex();
        } else if (addr >= 0xd800 && addr <= 0xdbe7) {
            try {
                return shell.matrix.peekColor(addr);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (addr < 0x4000) {
            C64VideoMatrix matrix = C64VideoMatrix.bufferFromAddress(addr);
            try {
                return matrix.peekFace(addr);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println("Unknown Peek @ " + String.format("$%04x", addr));
        return 0;
    }
}
