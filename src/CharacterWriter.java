/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import static java.awt.Color.BLACK;
import static java.awt.Color.WHITE;
import static java.awt.image.BufferedImage.TYPE_INT_ARGB;

/**
 *
 * @author Administrator
 */
public class CharacterWriter implements CharacterROM
{
    public final HashMap<Character, Image> imageMap = new HashMap<>();
    public final HashMap<Character, Character> keyMap = new HashMap<>();
    public final HashMap<Character, Character> reverseKeyMap = new HashMap<>();
    private static final int setbit = WHITE.getRGB();
    private static final int clrbit = BLACK.getRGB();

    private static CharacterWriter instance = null;

    public static CharacterWriter getInstance()
    {
        if (instance == null)
            instance = new CharacterWriter();
        return instance;
    }

    /**
     * Constructor, fills the char imageMap
     */
    private CharacterWriter()
    {
        for (int s=0; s<256; s++)
        {
            imageMap.put((char)s, getImage(s*8));
        }

        for (char s = 'a'; s<='z'; s++)
        {
            char t = (char) (s-'a'+1);
            keyMap.put (s, t);
            reverseKeyMap.put (t,s);
        }
        keyMap.put ('@', (char)0);
        reverseKeyMap.put ((char)0, '@');
    }

    /**
     * Prints string array to bitmap
     *
     * @param img
     * @param arr
     * @param x
     * @param y
     */
    public void printImg(BufferedImage img, String[] arr, int x, int y)
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
     * @param s String to print
     * @param x start position x
     * @param y start position y
     */
    public void printImg(BufferedImage img, CharSequence s, int x, int y)
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
            i = imageMap.get((char)256);
        Graphics g = img.getGraphics();
        g.drawImage(i, x, y, null);
    }

    private Image getImage (int idx)
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
                    img.setRGB(lines, rows, setbit);
                }
                else
                {
                    img.setRGB(lines, rows, clrbit);
                }
                i >>>= 1;
            }
        }
        return img;
    }

    public static void main (String[] args)
    {
        System.out.println(CharacterROM.characterData.length/8);
    }
}
