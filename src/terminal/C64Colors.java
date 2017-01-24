package terminal;

import java.awt.*;

/**
 * The actual values to POKE into a color memory location to change a
 * character's color are:
 * <p>
 * 0  BLACK   4  PURPLE     8  ORANGE     12  GRAY 2
 * 1  WHITE   5  GREEN      9  BROWN      13  Light GREEN
 * 2  RED     6  BLUE      10  Light RED  14  Light BLUE
 * 3  CYAN    7  YELLOW    11  GRAY 1     15  GRAY 3
 * <p>
 * For example, to change the color of a character located at the upper
 * left-hand corner of the screen to red, type: POKE 55296,2.
 */

enum C64Colors
{
    BLACK(new Color(0)),
    WHITE(new Color(0xffffff)),
    RED(new Color(0x880000)),
    CYAN(new Color(0xaaffee)),
    PURPLE(new Color(0xcc44cc)),
    GREEN(new Color(0x00cc55)),
    BLUE(new Color(0x0000aa)),
    YELLOW(new Color(0xeeee77)),
    ORANGE(new Color(0xdd8855)),
    BROWN(new Color(0x664400)),
    LIGHT_RED(new Color(0xff7777)),
    DARK_GRAY(new Color(0x333333)),
    MEDIUM_GRAY(new Color(0x777777)),
    LIGHT_GREEN(new Color(0xaaff66)),
    LIGHT_BLUE(new Color(0x0088ff)),
    LIGHT_GRAY(new Color(0xbbbbbb));

    private final Color c;

    C64Colors (Color val)
    {
        c = val;
    }

    Color getColor()
    {
        return c;
    }

    int getRGB()
    {
        return c.getRGB();
    }
}
