package terminal;

import java.io.File;

/**
 * Created by Administrator on 1/18/2017.
 */
class InputDispatcher
{
    private final C64Screen m_screen;
    private final ProgramStore store = new ProgramStore();
    BasicRunner basicRunner;

    public InputDispatcher (C64Screen screen)
    {
        m_screen = screen;
        new Thread(() ->
        {
            try
            {
                Thread.sleep (100);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            //noinspection InfiniteLoopStatement
            while(true)
            {
                try
                {
                    char[] in = m_screen.fromTextArea.take();
                    handleInput(in);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void dir ()
    {
        File[] filesInFolder = new File(".").listFiles();
        for (final File fileEntry : filesInFolder)
        {
            if (fileEntry.isFile())
            {
                m_screen.matrix.putString("\n" + fileEntry.getName() +
                        " -- " + fileEntry.length());
            }
        }
    }

    private void run (boolean sync)
    {
        basicRunner = new BasicRunner(store.toArray(), true, m_screen);
        basicRunner.start(sync);
    }

    /**
     * Main function. Runs in a separate thread
     * @param in
     */
    private void handleInput (char[] in)
    {
        System.gc();
        System.runFinalization();

        String s = new String(in).trim();
        String[] split = s.split(" ");
        s = s.toLowerCase();
        if (s.equals("list"))
        {
            m_screen.matrix.putString(store.toString());
            m_screen.matrix.putString(ProgramStore.OK);
        }
        else if (s.equals("new"))
        {
            store.clear();
            m_screen.matrix.putString(ProgramStore.OK);
        }
        else if (s.equals ("prettify"))
        {
            new Prettifier(store).doPrettify();
            m_screen.matrix.putString(ProgramStore.OK);
        }
        else if (s.equals ("renumber"))
        {
            new Prettifier(store).doRenumber();
            m_screen.matrix.putString(ProgramStore.OK);
        }
        else if (s.equals("cls"))
        {
            m_screen.matrix.clearScreen();
        }
        else if (s.equals("run"))
        {
            run(true);
        }
        else if (s.equals("dir"))
        {
            dir();
            m_screen.matrix.putString("\n"+ProgramStore.OK);
        }
        else if (split[0].toLowerCase().equals("save"))
        {
            String msg = store.save(split[1]);
            m_screen.matrix.putString(msg);
        }
        else if (split[0].toLowerCase().equals("load"))
        {
            String msg = store.load(split[1]);
            m_screen.matrix.putString(msg);
        }
        else
        {
            try
            {
                store.insert(s);
            }
            catch (Exception unused)
            {
                m_screen.matrix.putString(BasicRunner.runSingleLine(s, m_screen));
            }
        }
    }
}
