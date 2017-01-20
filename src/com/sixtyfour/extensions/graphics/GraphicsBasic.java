package com.sixtyfour.extensions.graphics;

import com.sixtyfour.elements.commands.Command;
import com.sixtyfour.elements.functions.Function;
import com.sixtyfour.extensions.BasicExtension;
import com.sixtyfour.extensions.graphics.commands.*;
import com.sixtyfour.extensions.graphics.functions.GetShape;
import com.sixtyfour.extensions.graphics.functions.LoadShape;
import com.sixtyfour.system.Machine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A basic BASIC extension, that adds some simple graphics commands. Note that
 * these graphics are not C64 related, but make full use of the actual machine's
 * capabilities on which your program is running on.
 * 
 * @author EgonOlsen
 * 
 */
public class GraphicsBasic implements BasicExtension {

	private final static List<Command> COMMANDS = Collections.unmodifiableList(new ArrayList<Command>() {
		private static final long serialVersionUID = 1L;
		{
			this.add(new Gron());
			this.add(new Groff());
			this.add(new Line());
			this.add(new Color());
			this.add(new Plot());
			this.add(new Circle());
			this.add(new FillMode());
			this.add(new Rect());
			this.add(new Clear());
			this.add(new Fill());
			this.add(new Gsave());
			this.add(new DrawShape());
			this.add(new RotateShape());
			this.add(new Flip());
			this.add(new BufferMode());
			this.add(new Limit());
			this.add(new Gprint());
			this.add(new Font());
			this.add(new AAMode());
			this.add(new Gget());
			this.add(new Copy());
		}
	});

	private final static List<Function> FUNCTIONS = Collections.unmodifiableList(new ArrayList<Function>() {
		private static final long serialVersionUID = 1L;
		{
			this.add(new LoadShape());
			this.add(new GetShape());
		}
	});

	@Override
	public List<Command> getCommands() {
		return COMMANDS;
	}

	@Override
	public List<Function> getFunctions() {
		return FUNCTIONS;
	}

	@Override
	public void reset(Machine machine) {
		GraphicsDevice window = GraphicsDevice.getDevice(machine);
		if (window != null) {
			window.dispose();
		}

	}

}
