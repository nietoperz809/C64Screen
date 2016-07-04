package sixtyfour.elements.functions;

import sixtyfour.elements.Type;
import sixtyfour.system.Machine;
import sixtyfour.util.VarUtils;

/**
 * The Class Tan.
 */
public class Tan extends AbstractFunction {

	/**
	 * Instantiates a new tan.
	 */
	public Tan() {
		super("TAN");
	}

	/* (non-Javadoc)
	 * @see sixtyfour.parser.Atom#getType()
	 */
	@Override
	public Type getType() {
		return Type.REAL;
	}

	/* (non-Javadoc)
	 * @see sixtyfour.parser.Atom#eval(sixtyfour.system.Machine)
	 */
	@Override
	public Object eval(Machine machine) {
		if (!term.getType().equals(Type.STRING)) {
			return Float.valueOf((float) Math.tan(VarUtils.getFloat(term.eval(machine))));
		}
		throw new RuntimeException("Type mismatch error: " + term.getType());
	}

}
