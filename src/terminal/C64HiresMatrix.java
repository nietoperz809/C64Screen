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
    private final byte[] rawbytes = new byte[25 * 40 * 8 * 8];
    private final int offset = 8192;

    public C64HiresMatrix()
    {
        bwImage = new BufferedImage (320, 200, BufferedImage.TYPE_BYTE_BINARY);
        //Image img = BitmapTools.makeColorTransparent(bwImage, new Color(0,0,0));
        //bwImage = BitmapTools.imageToBufferedImage(img, BufferedImage.TYPE_BYTE_BINARY);
        db = bwImage.getRaster().getDataBuffer();
    }

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
        for (int row = 0; row < 8000; row+=320)
        {
            for (int cc = 0; cc < 40; cc++)
            {
                for (int s = 0; s < 320; s+=40)
                {
                    db.setElem(s + row + cc, rawbytes[counter++]);
                }
            }
        }
//        g.setPaintMode();
//        g.setXORMode(Color.WHITE);
//        g.setColor(Color.RED);
//        g.fillRect(10,10,300,300);
        g.drawImage(bwImage, 0, 0, 2 * 320, 2 * 200, null);
    }
}
