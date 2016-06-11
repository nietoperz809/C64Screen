package sixtyfour.parser.logic;

import java.util.ArrayList;
import java.util.List;

import sixtyfour.system.Machine;

public class LogicTerm implements LogicBlock {

	private boolean not = false;

	private List<LogicBlock> blocks = new ArrayList<LogicBlock>();
	private List<LogicOp> ops = new ArrayList<LogicOp>();
	private String name;

	public LogicTerm(String name) {
		ops.add(new LogicAnd());
		this.name = name;
	}

	public void add(LogicBlock block, LogicOp op) {
		blocks.add(block);
		ops.add(op);
	}

	@Override
	public boolean eval(Machine machine) {
		if (blocks.size() == 0) {
			return true;
		}
		boolean res = true;
		for (int i = 0; i < blocks.size(); i++) {
			LogicBlock nextBlock = blocks.get(i);
			LogicOp nextOp = ops.get(i);
			res = nextOp.eval(machine, res, nextBlock);
		}

		if (not) {
			res = !res;
		}
		return res;
	}

	@Override
	public void not() {
		not = !not;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (not) {
			sb.append(" NOT ");
		}
		for (int i = 0; i < blocks.size(); i++) {
			LogicBlock nextBlock = blocks.get(i);
			LogicOp nextOp = ops.get(i + 1);
			sb.append("(").append(nextBlock).append(" ").append(i != blocks.size() - 1 ? nextOp : "").append(")");
		}
		return sb.toString();
	}

}
