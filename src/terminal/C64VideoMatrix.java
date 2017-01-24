package terminal;

import java.awt.*;
import java.util.ArrayList;

import static java.awt.event.KeyEvent.VK_ENTER;

/**
 * Created
 */
class C64VideoMatrix extends ArrayList<C64Character[]>
{
    static final int LINES_ON_SCREEN = 25;
    static final int CHARS_PER_LINE = 40;
    private static final int NO_CHARACTER = 0x100 + ' ';

    private int charBaseAddress = 0x400;
    static final int COLOR_BASE_ADDRESS = 0xd800;

    static C64VideoMatrix[] buffers = new C64VideoMatrix[16];

    private final Point currentCursorPos = new Point(0,0);
    /**
     * Color used for new chars on screen
     */
    private byte defaultColorIndex = 3;
    /**
     * counts up if line length exceeds CHARS_PER_LINE
     */
    private int overLength = 0;

    static
    {
        for (int s=0; s<buffers.length; s++)
        {
            buffers[s] = new C64VideoMatrix(s*1024);
        }
    }

    /**
     * Private Constructor
     * @param baseAddress Address of virtual memory segment
     */
    private C64VideoMatrix (int baseAddress)
    {
        charBaseAddress = baseAddress;
        clearScreen();
    }

    public static C64VideoMatrix bufferFromAddress (int addr)
    {
        return buffers[addr/1024];
    }

//    synchronized public void setBaseAddress (int a)
//    {
//        charBaseAddress = (a >>> 4)*1024;
//    }

    /**
     * Fill screen with blanks
     */
    synchronized public void clearScreen ()
    {
        this.clear();
        for (int s = 0; s< LINES_ON_SCREEN+1; s++)
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
            c[s] = new C64Character(NO_CHARACTER, defaultColorIndex);
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
            overLength = 0;
        }
        else
        {
            if (currentCursorPos.x == CHARS_PER_LINE)
            {
                nextLine();
                overLength++;
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
     * Gets input line as char array only
     * input can be concatenation of multiple lines
     * 
     * @return a char array
     */
    synchronized public Character[] readLine ()
    {
        ArrayList<Character> arr = new ArrayList<>();
        int ypos = currentCursorPos.y - overLength;
        for(;;)
        {
            C64Character c64[] = get(ypos);
            for (C64Character aC64 : c64)
            {
                if (aC64.face == NO_CHARACTER)
                {
                    Character[] ret = new Character[arr.size()];
                    arr.toArray(ret);
                    return ret; //get(currentCursorPos.y);
                }
                arr.add((char) aC64.face);
            }
            ypos++;
        }
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

// --Commented out by Inspection START (1/24/2017 7:50 AM):
//    /**
//     * Get element at specified position
//     * @param x x pos.
//     * @param y y pos.
//     * @return the element
//     */
//    synchronized public C64Character getVal (int x, int y)
//    {
//        try
//        {
//            return get(y)[x];
//        }
//        catch (Exception ex)
//        {
//            return new C64Character('?', 2);   // red '?'
//        }
//    }
// --Commented out by Inspection STOP (1/24/2017 7:50 AM)

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
    private Point elementfromAddress (int addr) throws Exception
    {
        Point p = new Point (addr % CHARS_PER_LINE, addr / CHARS_PER_LINE);
        if (p.y >= LINES_ON_SCREEN+1)
            throw new Exception("Wrong screen address");
        return p;
    }

    /**
     * Get value at specified address
     * @param offset screen memory address
     * @return the value
     */
    synchronized public int peekFace (int offset) throws Exception
    {
        Point p = elementfromAddress(offset-charBaseAddress);
        return get(p.y)[p.x].face;
    }

    synchronized public int peekColor (int offset) throws Exception
    {
        Point p = elementfromAddress(offset- COLOR_BASE_ADDRESS);
        return get(p.y)[p.x].colorIndex;
    }

    /**
     * Set value at specified address
     * @param offset screen memory address
     * @param val the new value
     */
    synchronized public void pokeFace (int offset, int val) throws Exception
    {
        Point p = elementfromAddress(offset-charBaseAddress);
        get(p.y)[p.x].face = val;
    }

    synchronized public void pokeColor (int offset, int val) throws Exception
    {
        Point p = elementfromAddress(offset- COLOR_BASE_ADDRESS);
        get(p.y)[p.x].colorIndex = val;
    }

    private final static int SCALE=16;
    private boolean blinkflag = false;
    public void render (Graphics g)
    {
        CharacterWriter writer = CharacterWriter.getInstance();
        int ypos = 0;
        for (int y = 0; y<LINES_ON_SCREEN; y++)
        {
            int xpos = 0;
            for (int x = 0; x<CHARS_PER_LINE; x++)
            {
                C64Character c64c = get(y)[x];
                int face = c64c.face & 0x00ff;
                g.setColor (C64Colors.values()[c64c.colorIndex].getColor());
                g.fillRect(xpos, ypos, SCALE, SCALE);
                g.drawImage(writer.imageMap.get((char)face),
                        xpos, ypos, SCALE, SCALE, null);
                xpos += SCALE;
            }
            ypos += SCALE;
        }
        if (blinkflag)
        {
            g.setColor(Color.GREEN);
            g.fillRect(currentCursorPos.x * SCALE,
                    currentCursorPos.y * SCALE, SCALE, SCALE);
        }
        blinkflag = !blinkflag;
    }
}
