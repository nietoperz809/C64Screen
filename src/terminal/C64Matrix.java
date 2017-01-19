package terminal;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

import static java.awt.event.KeyEvent.VK_ENTER;

/**
 * Created
 */
public class C64Matrix extends ArrayList<char[]>
{
    public static final int LINES_ON_SCREEN = 25;
    public static final int CHARS_PER_LINE = 40;
    private Point currentCursorPos = new Point(0,0);
    /**
     * Last typed character before return was hit
     */
    private char lastChar = '\uFFFF';

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
    private char[] createEmptyLine ()
    {
        char[] c = new char[CHARS_PER_LINE];
        for (int s = 0; s< CHARS_PER_LINE; s++)
        {
            c[s] = ' ';
        }
        return c;
    }

    /**
     * Move cursor to the line below
     * Shifts lines up and creates a new one at the end
     * if cursor moves behind last line.
     */
    synchronized public void nextLine ()
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
            char[] line = get(currentCursorPos.y);
            line[currentCursorPos.x] = c;
            currentCursorPos.x++;
        }
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
    synchronized public char[] getLastLine ()
    {
        return get(currentCursorPos.y);
    }

    /**
     * Get last chararacter typed by the user
     * @return the char or 0xffff if there is none
     */
    synchronized public char getLastChar()
    {
        char c = lastChar;
        lastChar = '\uffff';
        return c;
    }

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

    /**
     * Move cursor to new position
     * @param x new X pos.
     * @param y new y pos.
     * @throws Exception on bogus input arguments
     */
    synchronized public void gotoXY (int x, int y) throws Exception
    {
        if (x<0 || y<0)
            throw new Exception ("Negative values not allowed");
        if (x >= CHARS_PER_LINE || y >= LINES_ON_SCREEN)
            throw new Exception("Out of Range");
        currentCursorPos.x = x;
        currentCursorPos.y = y;
    }

    /**
     * The matrix as string representation
     * @return see line above :)
     */
    @Override
    public String toString ()
    {
        StringBuilder sb = new StringBuilder();
        for (char[] arr : this)
        {
            sb.append(Arrays.toString(arr)).append('\n');
        }
        return sb.toString();
    }

    /**
     * Get element at specified position
     * @param x x pos.
     * @param y y pos.
     * @return the element
     */
    synchronized public char getVal (int x, int y)
    {
        try
        {
            return get(y)[x];
        }
        catch (Exception ex)
        {
            return ' ';
        }
    }

    /**
     * Set element at specified position
     * @param x x pos.
     * @param y y pos.
     * @param val new element
     */
    synchronized public void setVal (int x, int y, char val)
    {
        get(y)[x] = val;
    }

    /**
     * Convert screen memory address to coordinates
     * @param addr memory address, must be >= 1024 and <= 1024+25*40
     * @return a point givin the x/y coordinates
     */
    private Point fromAddress (int addr)
    {
        addr -= 1024;
        return new Point (addr % CHARS_PER_LINE, addr / CHARS_PER_LINE);
    }

    /**
     * Get value at specified address
     * @param offset screen memory address
     * @return the value
     */
    synchronized public char peek (int offset)
    {
        Point p = fromAddress (offset);
        return get(p.y)[p.x];
    }

    /**
     * Set value at specified address
     * @param offset screen memory address
     * @param val the new value
     */
    synchronized public void poke (int offset, char val)
    {
        Point p = fromAddress (offset);
        get(p.y)[p.x] = val;
    }
}
