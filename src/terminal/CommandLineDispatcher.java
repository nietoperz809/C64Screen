package terminal;

import java.io.File;

/**
 * Created by Administrator on 1/18/2017.
 */
class CommandLineDispatcher {
    final ProgramStore store = new ProgramStore();
    private final C64Screen m_screen;
    BasicRunner basicRunner;

    private int speed = 990;

    public CommandLineDispatcher(C64Screen screen) {
        m_screen = screen;
        new Thread(() ->
        {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            while (!Thread.interrupted()) {
                try {
                    char[] in = m_screen.fromTextArea.take();
                    handleInput(in);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void dir() {
        File[] filesInFolder = new File(".").listFiles();
        for (final File fileEntry : filesInFolder) {
            if (fileEntry.isFile()) {
                String formatted = String.format("\n%-15s = %d",
                        fileEntry.getName(), fileEntry.length());
                m_screen.matrix.putString(formatted);
            }
        }
    }

    private void run(boolean sync) {
        basicRunner = new BasicRunner(store.toArray(), speed, m_screen);
        basicRunner.start(sync);
    }

    private void renumber(String[] split) {
        try {
            Prettifier pf = new Prettifier(store);
            switch (split.length) {
                case 1:
                    pf.doRenumber();
                    break;
                case 2:
                    int v1 = Integer.parseInt(split[1]);
                    pf.doRenumber(v1, v1);
                    break;
                case 3:
                    int va = Integer.parseInt(split[1]);
                    int vb = Integer.parseInt(split[2]);
                    pf.doRenumber(va, vb);
                    break;
            }
            m_screen.matrix.putString(ProgramStore.OK);
        } catch (Exception ex) {
            m_screen.matrix.putString(ProgramStore.ERROR);
        }
    }

    private void list(String[] split) {
        if (split.length == 2) {
            try {
                int i1 = Integer.parseInt(split[1]);  // single number
                if (i1 >= 0) // positive
                {
                    m_screen.matrix.putString(store.list(i1, i1));
                } else // negative
                {
                    m_screen.matrix.putString(store.list(0, -i1));
                }
            } catch (NumberFormatException ex) {
                String[] args = split[1].split("-");
                int i1 = Integer.parseInt(args[0]);
                int i2;
                try {
                    i2 = Integer.parseInt(args[1]);
                } catch (NumberFormatException ex2) {
                    i2 = Integer.MAX_VALUE;
                }
                m_screen.matrix.putString(store.list(i1, i2));
            }
        } else  // no args
        {
            m_screen.matrix.putString(store.toString());
        }
        m_screen.matrix.putString(ProgramStore.OK);
    }

    /**
     * Main function. Runs in a separate thread
     */
    private void handleInput(char[] in) {
        System.gc();
        System.runFinalization();

        String s = new String(in).trim();
        String[] split = s.split(" ");
        s = s.toLowerCase();
        if (split[0].equalsIgnoreCase("list")) {
            list(split);
        } else if (s.equals("shift")) {
            CharacterWriter.getInstance().switchCharset(true);
        } else if (s.equals("unshift")) {
            CharacterWriter.getInstance().switchCharset(false);
        } else if (s.equals("new")) {
            store.clear();
            m_screen.matrix.putString(ProgramStore.OK);
        } else if (s.equals("prettify")) {
            new Prettifier(store).doPrettify();
            m_screen.matrix.putString(ProgramStore.OK);
        } else if (split[0].equalsIgnoreCase("renumber")) {
            renumber(split);
        } else if (s.equals("cls")) {
            m_screen.matrix.clearScreen();
        } else if (s.equals("run")) {
            run(true);
        } else if (s.equals("dir")) {
            dir();
            m_screen.matrix.putString("\n" + ProgramStore.OK);
        } else if (split[0].equalsIgnoreCase("speed")) {
            try {
                speed = Integer.parseInt(split[1]);
                m_screen.matrix.putString("\n" + ProgramStore.OK);
            } catch (NumberFormatException ex) {
                m_screen.matrix.putString("\n" + ProgramStore.ERROR);
            }
        } else if (split[0].equalsIgnoreCase("save")) {
            String msg = store.save(split[1]);
            m_screen.matrix.putString(msg);
        } else if (split[0].equalsIgnoreCase("load")) {
            String msg = store.load(split[1]);
            m_screen.matrix.putString(msg);
        } else {
            try {
                store.insert(s);
            } catch (Exception unused) {
                m_screen.matrix.putString(BasicRunner.runSingleLine(s, m_screen));
            }
        }
    }
}
