/* Copyright (C) 2013-2025 TU Dortmund University
 * This file is part of AutomataLib <https://automatalib.net>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.automatalib.automaton.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.automatalib.automaton.concept.StateIDs;
import net.automatalib.automaton.simple.SimpleAutomaton;
import net.automatalib.common.util.HashUtil;

public class SimpleStateIDs<S> implements StateIDs<S> {

    private final Map<S, Integer> stateIds;
    private final List<S> states;

    public SimpleStateIDs(SimpleAutomaton<S, ?> automaton) {
        this.states = new ArrayList<>(automaton.getStates());
        int numStates = this.states.size();
        this.stateIds = new HashMap<>(HashUtil.capacity(numStates));

        for (int i = 0; i < numStates; i++) {
            S state = this.states.get(i);
            stateIds.put(state, i);
        }
    }

    @Override
    public int getStateId(S state) {
        final Integer id = stateIds.get(state);

        if (id == null) {
            throw new IllegalArgumentException();
        }

        return id;
    }

    @Override
    public S getState(int id) {
        return states.get(id);
    }

}
