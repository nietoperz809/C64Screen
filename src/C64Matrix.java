import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

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

    public void NextLine()
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

    public void putChar (char c)
    {
        if (c == '\n')
        {
            NextLine();
        }
        else
        {
            if (cursorPos.x == CHARS_PER_LINE)
            {
                NextLine();
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
        putChar(' ');
        left();
    }

    void putString (CharSequence str)
    {
        for (int s=0; s<str.length(); s++)
        {
            putChar(str.charAt(s));
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



    public static void main (String[] args) throws Exception
    {
        C64Matrix m = new C64Matrix();
//        m.putString("\n\n\n\nHallo");
//        for (int s=0; s<24; s++)
//            m.putString("0123456789012345678901234567890123456789");
//        m.putString("\n\nCharsequence");
//        m.putString("\n\n          Charsequence");
        m.gotoXY(0,0);
        m.putChar('*');

        m.gotoXY(39,24);
        m.putChar('*');
        m.putString("hallo");
        m.poke (1024, 'X');
        m.poke (2023, 'X');
        System.out.println(m);
    }
}
