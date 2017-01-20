package terminal;

/**
 * Created by Administrator on 1/14/2017.
 */
class Keywords
{
    private static final String[] kw = {"ABS", "AND", "ASC", "ATN",
            "CHR$", "CLOSE", "CLR", "CMD",
            "CONT", "COS", "DATA", "DEF", "DIM",
            "END", "EXP", "FN", "FOR", "FRE", "GET",
            "GET#", "GOSUB", "GOTO", "IF", "INPUT",
            "INPUT#", "INT", "LEFT$", "LEN", "LET",
            "LIST", "LOAD", "LOG", "MID$", "NEW",
            "NEXT", "NOT", "ON", "OPEN", "OR", "PEEK",
            "POKE", "POS", "PRINT", "PRINT#", "READ",
            "REM", "RESTORE", "RETURN", "RIGHT$", "RND",
            "RUN", "SAVE", "SGN", "SIN", "SPC", "SQR", "STATUS",
            "ST", "STEP", "STOP", "STR$", "SYS", "TAB", "TAN",
            "THEN", "TIME", "TI", "TIME$", "TI$", "TO", "USR",
            "VAL", "VERIFY", "WAIT"};

    public static boolean isKeyWord (String in)
    {
        for (String s: kw)
        {
            if (in.equals(s))
                return true;
        }
        return false;
    }
}
