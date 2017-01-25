package terminal;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.WritableRaster;

/**
 * Created by Administrator on 1/25/2017.
 */
public class C64HiresMatrix
{
    BufferedImage bwImage = new BufferedImage(
            320, 200,
            BufferedImage.TYPE_BYTE_BINARY);
    WritableRaster wr = bwImage.getRaster();
    DataBuffer db = wr.getDataBuffer();
    byte[] rawbytes = new byte[25 * 40 * 8 * 8];
    int offset = 8192;

    public byte peek (int addr)
    {
        return rawbytes[addr-offset];
    }

    public void poke (int addr, byte val)
    {
        rawbytes[addr-offset] = val;
    }

    public void render (Graphics g)
    {
        int counter = 0;
        for (int row = 0; row < 200; row+=8)
        {
            for (int cc = 0; cc < 40; cc++)
            {
                for (int s = 0; s < 320; s+=40)
                {
                    db.setElem(s + 40*row + cc, rawbytes[counter++]);
                }
            }
        }
        g.drawImage(bwImage, 0, 0, 2 * 320, 2 * 200, null);
    }
}
