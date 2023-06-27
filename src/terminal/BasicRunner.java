package terminal;

import com.sixtyfour.Basic;
import com.sixtyfour.DelayTracer;

/**
 * Proxy class to instantiate an run the BASIC system
 */
class BasicRunner implements Runnable {
    private static volatile boolean running = false;
    private Basic olsenBasic;
    private final C64Screen screen;
    private Thread thread;

    public void pause(boolean b) {
        olsenBasic.setPause(b);
    }

    public BasicRunner(String[] program, int speed, C64Screen shellFrame) {
        screen = shellFrame;
        if (running) {
            return;
        }
        olsenBasic = new Basic(program);
        if (speed > 0) {
            DelayTracer t = new DelayTracer(speed);
            olsenBasic.setTracer(t);
        }
        olsenBasic.getMachine().setMemoryListener(new PeekPokeHandler(shellFrame));
        olsenBasic.setOutputChannel(new ShellOutputChannel(shellFrame));
        olsenBasic.setInputProvider(new ShellInputProvider(shellFrame));
    }

    /**
     * Compile an run a single line
     *
     * @param in the BASIC line
     * @param sf reference to shell main window
     * @return textual representation of success/error
     */
    public static String runSingleLine(String in, C64Screen sf) {
        try {
            Basic b = new Basic("0 " + in.toUpperCase());
            b.getMachine().setMemoryListener(new PeekPokeHandler(sf));
            b.compile();
            b.setOutputChannel(new ShellOutputChannel(sf));
            b.setInputProvider(new ShellInputProvider(sf));
            b.start();
            return "";
        } catch (Exception ex) {
            return ex.getMessage().toUpperCase() + "\n";
        }
    }

    /**
     * Start BASIC task
     *
     * @param synchronous if true the caller is blocked
     */
    public void start(boolean synchronous) {
        if (running) {
            System.out.println("already running ...");
            return;
        }
        thread = new Thread(this);
        thread.start();
        screen.matrix.setCursorOnOff(false);
        if (!synchronous) {
            return;
        }
        try {
            thread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        screen.matrix.setCursorOnOff(true);
    }


    public Basic getOlsenBasic() {
        return olsenBasic;
    }

    @Override
    public void run() {
        running = true;
        try {
            olsenBasic.run();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            running = false;
        }
    }
}
