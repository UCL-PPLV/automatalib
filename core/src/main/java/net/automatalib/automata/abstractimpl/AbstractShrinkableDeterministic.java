/* Copyright (C) 2013 TU Dortmund
 * This file is part of AutomataLib, http://www.automatalib.net/.
 * 
 * AutomataLib is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 3.0 as published by the Free Software Foundation.
 * 
 * AutomataLib is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with AutomataLib; if not, see
 * <http://www.gnu.de/documents/lgpl.en.html>.
 */
package net.automatalib.automata.abstractimpl;

import net.automatalib.automata.ShrinkableDeterministic;

public abstract class AbstractShrinkableDeterministic<S, I, T, SP, TP> extends
		AbstractMutableDeterministic<S, I, T, SP, TP> implements
		ShrinkableDeterministic<S, I, T, SP, TP> {

	@Override
	public void removeState(S state) {
		AbstractShrinkableAutomaton.removeState(this, state);
	}

}
