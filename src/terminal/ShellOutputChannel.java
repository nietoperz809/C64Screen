package terminal;

import com.sixtyfour.plugins.impl.ConsoleOutputChannel;

import java.awt.*;
import java.util.HashMap;


/**
 * Created by Administrator on 1/4/2017.
 */
class ShellOutputChannel extends ConsoleOutputChannel {
    private final C64Screen shellFrame;
    /**
     * control char to color mapping
     */
    private final HashMap<Character, Integer> colorMap = new HashMap<>();

    public ShellOutputChannel(C64Screen sf) {
        this.shellFrame = sf;
        char[] codes = {144, 5, 28, 159, 156, 30, 31, 158, 129, 149,
                150, 151, 152, 153, 154, 155};
        for (int s = 0; s < 16; s++) {
            colorMap.put(codes[s], s);
        }
    }

    @Override
    public void print(int id, String txt) {
        Integer col;
        StringBuilder sb = new StringBuilder();
        for (int s = 0; s < txt.length(); s++) {
            char c = txt.charAt(s);
            if (c == 147) {
                shellFrame.matrix.clearScreen();
                continue;
            }
            if (c == 14) {
                CharacterWriter.getInstance().switchCharset(false);
                continue;
            }
            if (c == 142) {
                CharacterWriter.getInstance().switchCharset(true);
                continue;
            }
            col = colorMap.get(c);
            if (col != null) {
                shellFrame.matrix.setCurrentColorIndex(col);
            }
            else if (c == '\t') {
                sb.append ("          ");
            } else {
                sb.append(c);
            }
            shellFrame.matrix.putString(sb.toString());
            shellFrame.panel.repaint();
            sb.setLength(0);
        }
    }

    @Override
    public void println(int id, String txt) {
        print(id, txt + '\n');
    }

    @Override
    public int getCursor() {
        Point p = shellFrame.matrix.getCursor();
        return p.x;
    }

}
