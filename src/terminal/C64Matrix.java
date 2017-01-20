package terminal;

import java.awt.*;
import java.util.ArrayList;

import static java.awt.event.KeyEvent.VK_ENTER;

/**
 * Created
 */
class C64Matrix extends ArrayList<C64Character[]>
{
    public static final int LINES_ON_SCREEN = 25;
    public static final int CHARS_PER_LINE = 40;
    private final Point currentCursorPos = new Point(0,0);
    /**
     * Last typed character before return was hit
     */
    private char lastChar = '\uFFFF';
    /**
     * Color used for new chars on screen
     */
    private byte defaultColorIndex = 3;

    public C64Matrix()
    {
        clearScreen();
    }

    /**
     * Fill screen with blanks
     */
    synchronized public void clearScreen ()
    {
        this.clear();
        for (int s = 0; s< LINES_ON_SCREEN; s++)
        {
            add(createEmptyLine());
        }
        currentCursorPos.x = 0;
        currentCursorPos.y = 0;
    }

    /**
     * Make a line of blanks
     * @return The line
     */
    private C64Character[] createEmptyLine ()
    {
        C64Character[] c = new C64Character[CHARS_PER_LINE];
        for (int s = 0; s< CHARS_PER_LINE; s++)
        {
            c[s] = new C64Character(' ', defaultColorIndex);
        }
        return c;
    }

    /**
     * Move cursor to the line below
     * Shifts lines up and creates a new one at the end
     * if cursor moves behind last line.
     */
    private synchronized void nextLine ()
    {
        currentCursorPos.x = 0;
        currentCursorPos.y++;
        if (currentCursorPos.y == LINES_ON_SCREEN)
        {
            currentCursorPos.y--;
            for (int s = 0; s< LINES_ON_SCREEN -1; s++)
            {
                set (s, get(s+1));
            }
            set (LINES_ON_SCREEN -1, createEmptyLine());
        }
    }

    /**
     * Injects new Char
     * @param c the character
     * @param keyCode keyCode from KeyEvent
     * @param action  action from KeyEvent
     */
    synchronized public void putChar (char c, int keyCode, boolean action)
    {
        if (action || c == '\uFFFF')
        {
            return;
        }
        if (keyCode == VK_ENTER)
        {
            nextLine();
        }
        else
        {
            lastChar = c;
            if (currentCursorPos.x == CHARS_PER_LINE)
            {
                nextLine();
            }
            C64Character[] line = get(currentCursorPos.y);
            line[currentCursorPos.x].face = c;
            line[currentCursorPos.x].colorIndex = defaultColorIndex;
            currentCursorPos.x++;
        }
    }

    public void setDefaultColorIndex (int b)
    {
        defaultColorIndex = (byte)(b & 0x0f);
    }

    public byte getDefaultColorIndex ()
    {
        return defaultColorIndex;
    }

    /**
     * Get current cursor position
     * @return the cursorPos (cloned)
     */
    synchronized public Point getCursor()
    {
        return (Point) currentCursorPos.clone();
    }

    /**
     * Get last line
     * @return a char array clone of the line
     */
    synchronized public C64Character[] getLastLine ()
    {
        return get(currentCursorPos.y);
    }

// --Commented out by Inspection START (1/20/2017 5:31 AM):
//    /**
//     * Get last chararacter typed by the user
//     * @return the char or 0xffff if there is none
//     */
//    synchronized public char getLastChar()
//    {
//        char c = lastChar;
//        lastChar = '\uffff';
//        return c;
//    }
// --Commented out by Inspection STOP (1/20/2017 5:31 AM)

    /**
     * Move cursor up
     */
    synchronized public void up()
    {
        if (currentCursorPos.y > 0)
            currentCursorPos.y--;
    }

    /**
     * Move cursor down
     */
    synchronized public void down()
    {
        if (currentCursorPos.y < LINES_ON_SCREEN-1)
            currentCursorPos.y++;
    }

    /**
     * Move cursor left
     */
    synchronized public void left()
    {
        if (currentCursorPos.x > 0)
            currentCursorPos.x--;
    }

    /**
     * Move cursor right
     */
    synchronized public void right()
    {
        if (currentCursorPos.x < CHARS_PER_LINE-1)
            currentCursorPos.x++;
    }

    /**
     * Insert a backspace
     */
    synchronized public void backspace()
    {
        left();
        putChar(' ', 0, false);
        left();
    }

    /**
     * Inserts entire string
     * @param str
     */
    synchronized void putString (String str)
    {
        str = str.toLowerCase();
        for (int s=0; s<str.length(); s++)
        {
            char c = str.charAt(s);
            int keycode = c == '\n' ? VK_ENTER : 0;
            c = CharacterWriter.getInstance().mapPCtoCBM(c);
            putChar(c, keycode, false);
        }
    }

// --Commented out by Inspection START (1/20/2017 5:31 AM):
//    /**
//     * Move cursor to new position
//     * @param x new X pos.
//     * @param y new y pos.
//     * @throws Exception on bogus input arguments
//     */
//    synchronized public void gotoXY (int x, int y) throws Exception
//    {
//        if (x<0 || y<0)
//            throw new Exception ("Negative values not allowed");
//        if (x >= CHARS_PER_LINE || y >= LINES_ON_SCREEN)
//            throw new Exception("Out of Range");
//        currentCursorPos.x = x;
//        currentCursorPos.y = y;
//    }
// --Commented out by Inspection STOP (1/20/2017 5:31 AM)

    //    @Override
//    public String toString ()
//    {
//        StringBuilder sb = new StringBuilder();
//        for (char[] arr : this)
//        {
//            sb.append(Arrays.toString(arr)).append('\n');
//        }
//        return sb.toString();
//    }

    /**
     * Get element at specified position
     * @param x x pos.
     * @param y y pos.
     * @return the element
     */
    synchronized public C64Character getVal (int x, int y)
    {
        try
        {
            return get(y)[x];
        }
        catch (Exception ex)
        {
            return new C64Character('?', 2);   // red '?'
        }
    }

// --Commented out by Inspection START (1/20/2017 5:31 AM):
//    /**
//     * Set element at specified position
//     * @param x x pos.
//     * @param y y pos.
//     * @param val new element
//     */
//    synchronized public void setVal (int x, int y, char val)
//    {
//        get(y)[x].face = (byte)val;
//    }
// --Commented out by Inspection STOP (1/20/2017 5:31 AM)

    /**
     * Convert screen memory address to coordinates
     * @param addr memory address, must be >= 1024 and <= 1024+25*40
     * @return a point givin the x/y coordinates
     */
    private Point fromAddress (int addr, int offset)
    {
        addr -= offset;
        return new Point (addr % CHARS_PER_LINE, addr / CHARS_PER_LINE);
    }

    /**
     * Get value at specified address
     * @param offset screen memory address
     * @return the value
     */
    synchronized public int peekFace (int offset)
    {
        Point p = fromAddress (offset, 1024);
        return get(p.y)[p.x].face;
    }

    synchronized public int peekColor (int offset)
    {
        Point p = fromAddress (offset, 0xd800);
        return get(p.y)[p.x].colorIndex;
    }

    /**
     * Set value at specified address
     * @param offset screen memory address
     * @param val the new value
     */
    synchronized public void pokeFace (int offset, int val)
    {
        Point p = fromAddress (offset, 1024);
        get(p.y)[p.x].face = val;
    }

    synchronized public void pokeColor (int offset, int val)
    {
        Point p = fromAddress (offset, 0xd800);
        get(p.y)[p.x].colorIndex = val;
    }
}
