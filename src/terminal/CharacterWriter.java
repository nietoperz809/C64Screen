package terminal;/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import misc.BitmapTools;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import static java.awt.image.BufferedImage.TYPE_INT_ARGB;

/**
 * @author Administrator
 */
public class CharacterWriter implements CharacterROM {
    private static final Color TRANSPARENT = new Color(1, 2, 3);
    private static CharacterWriter instance = null;
    final HashMap<Character, Image> imageMap = new HashMap<>();
    private final HashMap<Character, Character> keyMap = new HashMap<>();
    private final HashMap<Character, Character> reverseKeyMap = new HashMap<>();
    private int backgroundColor = C64Colors.BLUE.getRGB();
    private boolean shifted = true;

    /**
     * Constructor, fills the char imageMap
     */
    private CharacterWriter() {
        fillImageMap();
        for (char s = 'a'; s <= 'z'; s++) {
            char t = (char) (s - 'a' + 1);
            setMaps(s, t);
        }
        setMaps('@', (char) 0);
        setMaps('^', (char) 30);
    }

    public static CharacterWriter getInstance() {
        if (instance == null) {
            instance = new CharacterWriter();
        }
        return instance;
    }

//    public static void main(String[] args) {
//        System.out.println(CharacterROM.characterData.length / 8);
//    }

    private void setMaps(char a, char b) {
        keyMap.put(a, b);
        reverseKeyMap.put(b, a);
    }

    private void fillImageMap() {
        imageMap.clear();
        for (int s = 0; s < 256; s++) {
            int idx = shifted ? s * 8 : (s + 256) * 8;
            imageMap.put((char) s, getImage(idx));
        }
    }

    private Image getImage(int idx) {
        BufferedImage img = new BufferedImage(8, 8, TYPE_INT_ARGB);
        for (int rows = 0; rows < 8; rows++) {
            int c = characterData[idx++];
            int i = 128;
            for (int lines = 0; lines < 8; lines++) {
                if ((c & i) == i) {
                    img.setRGB(lines, rows, TRANSPARENT.getRGB());
                } else {
                    img.setRGB(lines, rows, backgroundColor);
                }
                i >>>= 1;
            }
        }
        return BitmapTools.makeColorTransparent(img, TRANSPARENT);
    }

    void setBackgroundColor(int idx) {
        backgroundColor = C64Colors.values()[idx].getRGB();
        fillImageMap();
    }

    void switchCharset (boolean shift) {
        shifted = shift;
        fillImageMap();
    }

    public char mapCBMtoPC(Character in) {
        Character c = reverseKeyMap.get(in);
        c = c==null ? in : c;
        if (c == 'Ġ')  // no char?
            return ' ';
        return c;
    }

    public char[] mapCBMtoPC(Character[] in) {
        char[] out = new char[in.length];
        for (int s = 0; s < in.length; s++) {
            Character c1 = reverseKeyMap.get(in[s]);
            out[s] = c1 == null ? in[s] : c1;
        }
        return out;
    }

    public char mapPCtoCBM(char in) {
        Character c1 = keyMap.get(in);
        return c1 == null ? in : c1;
    }

}
