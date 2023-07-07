package terminal;


import resid.AudioDriverSE;
import resid.ISIDDefs;
import resid.SID;

/**
 * Run the sound chip emulation
 */
@SuppressWarnings("InfiniteLoopStatement")
class SidRunner {
    private static AudioDriverSE audioDriver = null;
    private static SID sid = null;
    private static final int CPUFrq = 985248;
    private static final int SAMPLE_RATE = 22000;
    private static final int BUFFER_SIZE = 256;
    private static final byte[] buffer = new byte[BUFFER_SIZE * 2];
    private static int pos = 0;
    private static volatile boolean reset = false;

// --Commented out by Inspection START (1/20/2017 5:31 AM):
//    public static void reset()
//    {
//        reset = true;
//    }
// --Commented out by Inspection STOP (1/20/2017 5:31 AM)

    private static synchronized void setupSID() {
        sid = new SID();
        sid.set_sampling_parameters(CPUFrq,
                ISIDDefs.sampling_method.SAMPLE_RESAMPLE_INTERPOLATE, //.SAMPLE_INTERPOLATE, //SAMPLE_FAST,
                SAMPLE_RATE,
                -1,
                0.97);
        sid.set_chip_model(ISIDDefs.chip_model.MOS8580);
        for (int s = 0; s <= 0x1c; s++)
            sid.write(s, 0);
    }

    /**
     * Main function that inits and starts the SID
     */
    static void start() {
        audioDriver = new AudioDriverSE();
        audioDriver.init(SAMPLE_RATE, 22000);
        setupSID();

        new Thread(new Runnable() {
            final int clocksPerSample = CPUFrq / SAMPLE_RATE;
            final int temp = (int) ((CPUFrq * 1000L) / SAMPLE_RATE);
            final int clocksPerSampleRest = temp - clocksPerSample * 1000;
            long nextSample = 0;
            long lastCycles = 0;
            int nextRest = 0;

            public void execute(long cycles) {
                nextSample += clocksPerSample;
                nextRest += clocksPerSampleRest;
                if (nextRest > 1000) {
                    nextRest -= 1000;
                    nextSample++;
                }
                // Clock resid!
                while (lastCycles < cycles) {
                    SidRunner.clock();
                    lastCycles++;
                }
                int sample = sid.output();
                buffer[pos++] = (byte) (sample & 0xff);
                buffer[pos++] = (byte) ((sample >> 8));
                if (pos == buffer.length) {
                    audioDriver.write(buffer);
                    pos = 0;
                }
            }

            @Override
            public void run() {
                Thread.currentThread().setName("SIDRunner");
                long cycles = 1;
                while (!Thread.interrupted()) {
                    execute(cycles);
                    cycles += 33;
                    if (reset) {
                        setupSID();
                        reset = false;
                        System.out.println("SID ready");
                    }
                }
                System.out.println("SID died");
            }
        }).start();
    }

    static synchronized int read(int reg) {
        return sid.read(reg);
    }

    static synchronized void write(int reg, int val) {
        sid.write(reg, val);
    }

    private static synchronized void clock() {
        sid.clock();
    }
}
