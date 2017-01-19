package terminal;

import com.sixtyfour.plugins.impl.NullMemoryListener;

/**
 * Created by Administrator on 1/8/2017.
 */
public class PeekPokeHandler extends NullMemoryListener
{
    public static final int SID_FIRST = 0xd400;
    public static final int SID_LAST = 0xd41c;
    private C64Screen shell;

    public PeekPokeHandler (C64Screen f)
    {
        shell = f;
    }

    @Override
    public void poke (int addr, int value)
    {
//        if (addr == 53281)
//        {
//            shell.setBkColor(C64Colors.get(value));
//        }
//        else if (addr >= SID_FIRST && addr <= SID_LAST)   // SID
//        {
//            //System.out.println("write "+addr+"/"+value);
//            SidRunner.write(addr-SID_FIRST, value);
//        }
    }

    @Override
    public Integer peek (int addr)
    {
//        if (addr >= SID_FIRST && addr <= SID_LAST)   // SID
//        {
//            //System.out.println("read "+addr);
//            return SidRunner.read(addr-0xd400);
//        }
        return 0;
    }
}
