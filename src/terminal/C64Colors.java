package terminal;

import java.awt.*;

/**
 The actual values to POKE into a color memory location to change a
 character's color are:

 0  BLACK   4  PURPLE     8  ORANGE     12  GRAY 2
 1  WHITE   5  GREEN      9  BROWN      13  Light GREEN
 2  RED     6  BLUE      10  Light RED  14  Light BLUE
 3  CYAN    7  YELLOW    11  GRAY 1     15  GRAY 3

 For example, to change the color of a character located at the upper
 left-hand corner of the screen to red, type: POKE 55296,2.
 */
public class C64Colors
{

    private static final Color[] COLORS = {
            new Color(0),
            new Color(0xffffff),
            new Color(0x880000),
            new Color(0xaaffee),
            new Color(0xcc44cc),
            new Color(0x00cc55),
            new Color(0x0000aa),
            new Color(0xeeee77),
            new Color(0xdd8855),
            new Color(0x664400),
            new Color(0xff7777),
            new Color(0x333333),
            new Color(0x777777),
            new Color(0xaaff66),
            new Color(0x0088ff),
            new Color(0xbbbbbb),
    };

    /**
     * Get Color from index
     * @param idx only lower 4 bits are used
     * @return the color
     */
    public static Color getC64Color (int idx)
    {
        try
        {
            return COLORS[idx % COLORS.length];
        }
        catch (Exception unused)
        {
            return Color.BLACK;
        }
    }
}
