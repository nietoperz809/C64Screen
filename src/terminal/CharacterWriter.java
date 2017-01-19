package terminal;/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.awt.*;
import java.awt.image.*;
import java.util.HashMap;

import static java.awt.image.BufferedImage.TYPE_INT_ARGB;

/**
 * @author Administrator
 */
public class CharacterWriter implements CharacterROM
{
    private static final Color TRANSPARENT = new Color (1,2,3);
    private static CharacterWriter instance = null;
    public final HashMap<Character, Image> imageMap = new HashMap<>();
    public final HashMap<Character, Character> keyMap = new HashMap<>();
    public final HashMap<Character, Character> reverseKeyMap = new HashMap<>();
    private int backgroundColor = C64Colors.get(6).getRGB();

    /**
     * Constructor, fills the char imageMap
     */
    private CharacterWriter ()
    {
        fillImageMap();

        for (char s = 'a'; s <= 'z'; s++)
        {
            char t = (char) (s - 'a' + 1);
            keyMap.put(s, t);
            reverseKeyMap.put(t, s);
        }
        keyMap.put('@', (char) 0);
        reverseKeyMap.put((char) 0, '@');
    }

    private void fillImageMap ()
    {
        imageMap.clear();
        for (int s = 0; s < 256; s++)
        {
            imageMap.put((char) s, getImage(s * 8));
        }
    }

    public Image getImage (int idx)
    {
        BufferedImage img = new BufferedImage(8, 8, TYPE_INT_ARGB);
        for (int rows = 0; rows < 8; rows++)
        {
            int c = characterData[idx++];
            int i = 128;
            for (int lines = 0; lines < 8; lines++)
            {
                if ((c & i) == i)
                {
                    img.setRGB(lines, rows, TRANSPARENT.getRGB());
                }
                else
                {
                    img.setRGB(lines, rows, backgroundColor);
                }
                i >>>= 1;
            }
        }
        return makeColorTransparent(img, TRANSPARENT);
    }

    public static CharacterWriter getInstance ()
    {
        if (instance == null)
        {
            instance = new CharacterWriter();
        }
        return instance;
    }

    //Just copy-paste this method
    public static Image makeColorTransparent (BufferedImage im, final Color color)
    {
        ImageFilter filter = new RGBImageFilter()
        {
            // the color we are looking for... Alpha bits are set to opaque
            public int markerRGB = color.getRGB() | 0xFF000000;

            public final int filterRGB (int x, int y, int rgb)
            {
                if ((rgb | 0xFF000000) == markerRGB)
                {
                    // Mark the alpha bits as zero - transparent
                    return 0x00FFFFFF & rgb;
                }
                else
                {
                    // nothing to do
                    return rgb;
                }
            }
        };

        ImageProducer ip = new FilteredImageSource(im.getSource(), filter);
        return Toolkit.getDefaultToolkit().createImage(ip);
    }

    public static void main (String[] args)
    {
        System.out.println(CharacterROM.characterData.length / 8);
    }

    void setBackgroundColor (int idx)
    {
        backgroundColor = C64Colors.get(idx).getRGB();
        fillImageMap();
    }

    public char[] mapCBMtoPC (char[] in)
    {
        char[] out = new char[in.length];
        for (int s = 0; s < in.length; s++)
        {
            Character c1 = reverseKeyMap.get(in[s]);
            out[s] = c1 == null ? in[s] : c1;
        }
        return out;
    }

    public String mapPCtoCBM (String in)
    {
        StringBuilder sb = new StringBuilder();
        for (int s = 0; s < in.length(); s++)
        {
            sb.append(mapPCtoCBM(in.charAt(s)));
        }
        return sb.toString();
    }

    public char mapPCtoCBM (char in)
    {
        Character c1 = keyMap.get(in);
        return c1 == null ? in : c1;
    }

    /**
     * Prints string array to bitmap
     *
     * @param img
     * @param arr
     * @param x
     * @param y
     */
    public void printImg (BufferedImage img, String[] arr, int x, int y)
    {
        for (String str : arr)
        {
            printImg(img, str, x, y);
            y += 8;
        }
    }

    /**
     * Prints String into bitmap
     *
     * @param img Destination bitmap
     * @param s   String to print
     * @param x   start position x
     * @param y   start position y
     */
    public void printImg (BufferedImage img, CharSequence s, int x, int y)
    {
        int xstart = x;
        for (int i = 0; i < s.length(); i++)
        {
            char c = s.charAt(i);
            if (c == '\n')
            {
                y += 8;
                x = xstart;
            }
            else
            {
                printImg(img, c, x, y);
                x += 8;
            }
        }
    }

    public void printImg (BufferedImage img, char c, int x, int y)
    {
        Image i = imageMap.get(c);
        if (i == null)
        {
            i = imageMap.get((char) 256);
        }
        Graphics g = img.getGraphics();
        g.drawImage(i, x, y, null);
    }
}
