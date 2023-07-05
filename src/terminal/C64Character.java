package terminal;

/**
 * Created by Administrator on 1/19/2017.
 */
public class C64Character {
    public int face;
    public int colorIndex;

    public C64Character(int face, int col) {
        this.face = face;
        this.colorIndex = col;
    }

    public void getFrom(C64Character src) {
        this.face = src.face;
        this.colorIndex = src.colorIndex;
    }
}
