package terminal;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;

/**
 * Created by Administrator on 1/25/2017.
 */
public class C64HiresMatrix
{
    private BufferedImage bwImage;
    private final DataBuffer db;
    private final byte[] rawbytes = new byte[26 * 40 * 8];
    private int offset = 0;

    public C64HiresMatrix()
    {
        bwImage = new BufferedImage (320, 200, BufferedImage.TYPE_INT_ARGB);
        db = bwImage.getRaster().getDataBuffer();
        for (int s=0; s<rawbytes.length; s++)
        {
            rawbytes[s] = 0;
        }
    }

    public byte peek (int addr)
    {
        return rawbytes[addr-offset];
    }

    public void poke (int addr, byte val)
    {
        rawbytes[addr-offset] = val;
    }

    C64VideoMatrix screenMatrix = C64VideoMatrix.bufferFromAddress(1024);

    public void render (Graphics g)
    {
        int counter = 0;
        for (int r = 0; r < 64000; r+=2560)
        {
            for (int c = 0; c < 320; c+=8)
            {
                for (int s = 0; s < 2560; s+=320)
                {
                    int rgb;
                    try
                    {
                        rgb = screenMatrix.peekFace(1024+counter/8) & 0xff;
                    }
                    catch (Exception e)
                    {
                        rgb = 0;
                        e.printStackTrace();
                    }
                    int rgb1 = C64Colors.values()[rgb%15].getRGB();
                    int rgb2 = C64Colors.values()[(rgb>>>4)%15].getRGB();
                    int b = 128;
                    int bb = rawbytes[counter++];
                    for (int in = 0; in < 8; in++)
                    {
                        if ((bb & b) == b)
                            db.setElem(c + in + r + s, rgb1);
                        else
                            db.setElem(c + in + r + s, rgb2);
                        b>>>=1;
                    }
                }
            }
        }
        g.drawImage(bwImage, 0, 0, 2 * 320, 2 * 200, null);
    }

    public void setAddress_8192 (boolean b)
    {
        if (b)
            offset = 8192;
        else
            offset = 0;
    }
}
