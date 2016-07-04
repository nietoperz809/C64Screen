package sixtyfour.elements.commands;

import sixtyfour.system.Machine;
import sixtyfour.system.ProgramCounter;

/**
 * The Class Rem.
 */
public class Rem extends AbstractCommand {

	/** The Constant REM_MARKER. */
	public final static String REM_MARKER = "###";

	/**
	 * Instantiates a new rem.
	 */
	public Rem() {
		super("REM");
	}

	/* (non-Javadoc)
	 * @see sixtyfour.elements.commands.AbstractCommand#parse(java.lang.String, int, int, int, boolean, sixtyfour.system.Machine)
	 */
	@Override
	public String parse(String linePart, int lineCnt, int lineNumber, int linePos, boolean lastPos, Machine machine) {
		super.parse(linePart, lineCnt, lineNumber, linePos, lastPos, machine);
		return REM_MARKER;
	}

	/* (non-Javadoc)
	 * @see sixtyfour.elements.commands.AbstractCommand#execute(sixtyfour.system.Machine)
	 */
	@Override
	public ProgramCounter execute(Machine machine) {
		return null;
	}
}
