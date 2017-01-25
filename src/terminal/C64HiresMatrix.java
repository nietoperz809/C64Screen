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
    private final int[] rawints = new int[26 * 40 * 8 * 8];
    private final int offset = 8192;

    public C64HiresMatrix()
    {
        bwImage = new BufferedImage (320, 200, BufferedImage.TYPE_INT_ARGB);
        db = bwImage.getRaster().getDataBuffer();
    }

    public int peek (int addr)
    {
        addr = (addr-offset)*8;
        return 0; //rawints[addr-offset];
        // TODO: fill this with usable code
    }

    public void poke (int addr, int val)
    {
        addr = (addr-offset)*8;
        int b = 128;
        while(b != 0)
        {
            if ((val & b) == b)
                rawints[addr] = Color.RED.getRGB();
            else
                rawints[addr] = 0;
            addr++;
            b>>>=1;
        }
    }

    public void render (Graphics g)
    {
        int counter = 0;
        for (int r = 0; r < 64000; r+=2560)
        {
            for (int c = 0; c < 320; c+=8)
            {
                for (int s = 0; s < 2560; s+=320)
                {
                    for (int in = 0; in < 8; in++)
                    {
                        db.setElem(c + in + r + s, rawints[counter++]);
                    }
                }
            }
        }
        g.drawImage(bwImage, 0, 0, 2 * 320, 2 * 200, null);
    }
}
