package terminal;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Comparator;
import java.util.TreeSet;

/**
 * Created by Administrator on 1/4/2017.
 */
class ProgramStore {
    public static final String ERROR = "ERROR.\n";
    public static final String OK = "READY.\n";
    private final TreeSet<String> store = new TreeSet<>(new LineComparator());

    public String[] toArray() {
        //noinspection unchecked
        TreeSet<String> clone = (TreeSet<String>) store.clone();  // avoid java.util.ConcurrentModificationException
        String[] arr = new String[clone.size()];
        int n = 0;
        for (String s : clone) {
            arr[n++] = s;
        }
        return arr;
    }


    public void insert(String codeLine) throws NumberFormatException {
        if (codeLine.trim().isEmpty())
            return;
        codeLine = codeLine.toUpperCase();
        int num = getLineNumber(codeLine);
        try {
            int unused = Integer.parseInt(codeLine); // Number only?
            removeLine(num);
        } catch (NumberFormatException ex) {
            addLine(codeLine);
        }
    }

    /**
     * Get line number from the beginning
     *
     * @param in input string
     * @return line number
     */
    private int getLineNumber(String in) {
        if (in.isEmpty())
            return 0;  // no line number
        String[] split = in.split(" ");
        return Integer.parseInt(split[0]);
    }

    private void removeLine(int num) {
        //noinspection unchecked
        TreeSet<String> clone = (TreeSet<String>) store.clone();  // avoid java.util.ConcurrentModificationException
        for (String s : clone) {
            if (getLineNumber(s) == num) {
                store.remove(s);
            }
        }
    }

    private void addLine(String s) {
        removeLine(getLineNumber(s));
        store.add(s);
    }

    public void clear() {
        store.clear();
    }

    public String load(String path) {
        path = path.replaceAll("\"", "");
        store.clear();
        File file = new File(path);
        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(Files.newInputStream(file.toPath()), StandardCharsets.UTF_8))) {
            String str;
            while ((str = in.readLine()) != null) {
                store.add(str);
            }
        } catch (Exception e) {
            System.out.println(e);
            return ERROR;
        }
        return OK;
    }

    public String save(String path) {
        path = path.replaceAll("\"", "");
        boolean ok = true;
        String txt = this.toString();
        FileWriter outFile = null;
        try {
            outFile = new FileWriter(path);
        } catch (IOException e) {
            e.printStackTrace();
            ok = false;
        }
        try {
            assert outFile != null;
            try (PrintWriter out1 = new PrintWriter(outFile)) {
                out1.append(txt);
            }
        } finally {
            try {
                outFile.close();
            } catch (Exception e) {
                e.printStackTrace();
                ok = false;
            }
        }
        return ok ? OK : ERROR;
    }

    public String list(int min, int max) {
        //noinspection unchecked
        TreeSet<String> clone = (TreeSet<String>) store.clone();  // avoid java.util.ConcurrentModificationException
        StringBuilder sb = new StringBuilder();
        for (String s : clone) {
            int num = getLineNumber(s);
            if (num >= min && num <= max) {
                sb.append(s).append('\n');
            }
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        //noinspection unchecked
        TreeSet<String> clone = (TreeSet<String>) store.clone();  // avoid java.util.ConcurrentModificationException
        StringBuilder sb = new StringBuilder();
        for (String s : clone) {
            sb.append(s).append('\n');
        }
        return sb.toString();
    }

    class LineComparator implements Comparator<String> {
        @Override
        public int compare(String s1, String s2) {
            //System.out.println(s1+"-"+s2+"="+ret);
            return getLineNumber(s1) - getLineNumber(s2);
        }
    }
}
