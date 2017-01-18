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
    private Point cursorPos = new Point(0,0);

    public C64Matrix()
    {
        ClearScreen();
    }

    public void ClearScreen()
    {
        this.clear();
        for (int s = 0; s< LINES_ON_SCREEN; s++)
        {
            add(createEmptyLine());
        }
    }

    private char[] createEmptyLine ()
    {
        char[] c = new char[CHARS_PER_LINE];
        for (int s = 0; s< CHARS_PER_LINE; s++)
        {
            c[s] = ' ';
        }
        return c;
    }

    public void nextLine ()
    {
        cursorPos.x = 0;
        cursorPos.y++;
        if (cursorPos.y == LINES_ON_SCREEN)
        {
            cursorPos.y--;
            for (int s = 0; s< LINES_ON_SCREEN -1; s++)
            {
                set (s, get(s+1));
            }
            set (LINES_ON_SCREEN -1, createEmptyLine());
        }
    }

    public void putChar (char c, int keyCode, boolean action)
    {
        if (action)
        {
            return;
        }
        if (keyCode == VK_ENTER)
        {
            nextLine();
        }
        else
        {
            if (cursorPos.x == CHARS_PER_LINE)
            {
                nextLine();
            }
            char[] line = get(cursorPos.y);
            line[cursorPos.x] = c;
            cursorPos.x++;
        }
    }

    public Point getCursor()
    {
        return (Point) cursorPos.clone();
    }

    public char[] getCurrentLine()
    {
        return get(cursorPos.y).clone();
    }

    public void up()
    {
        if (cursorPos.y > 0)
            cursorPos.y--;
    }

    public void down()
    {
        if (cursorPos.y < LINES_ON_SCREEN-1)
            cursorPos.y++;
    }

    public void left()
    {
        if (cursorPos.x > 0)
            cursorPos.x--;
    }

    public void right()
    {
        if (cursorPos.x < CHARS_PER_LINE-1)
            cursorPos.x++;
    }

    public void backspace()
    {
        left();
        putChar(' ', 0, false);
        left();
    }

    void putString (CharSequence str)
    {
        for (int s=0; s<str.length(); s++)
        {
            putChar(str.charAt(s), 0, false);
        }
    }

    public void gotoXY (int x, int y) throws Exception
    {
        if (x<0 || y<0)
            throw new Exception ("Negative values not allowed");
        if (x >= CHARS_PER_LINE || y >= LINES_ON_SCREEN)
            throw new Exception("Ou of Range");
        cursorPos.x = x;
        cursorPos.y = y;
    }

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

    char getVal (int x, int y)
    {
        return get(y)[x];
    }

    void setVal (int x, int y, char val)
    {
        get(y)[x] = val;
    }

    private Point fromAddress (int addr)
    {
        addr -= 1024;
        return new Point (addr % CHARS_PER_LINE, addr / CHARS_PER_LINE);
    }

    char peek (int offset)
    {
        Point p = fromAddress (offset);
        return get(p.y)[p.x];
    }

    void poke (int offset, char val)
    {
        Point p = fromAddress (offset);
        get(p.y)[p.x] = val;
    }
}
