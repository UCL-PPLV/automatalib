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
package net.automatalib.util.automaton.fsa;

import java.util.Collection;

import net.automatalib.automaton.fsa.MutableDFA;
import net.automatalib.util.automaton.minimizer.HopcroftMinimizer;

public final class MutableDFAs {

    private MutableDFAs() {
        // prevent instantiation
    }

    public static <I> void complete(MutableDFA<?, I> dfa, Collection<? extends I> inputs) {
        complete(dfa, inputs, false);
    }

    public static <I> void complete(MutableDFA<?, I> dfa, Collection<? extends I> inputs, boolean minimize) {
        complete(dfa, inputs, minimize, false);
    }

    public static <S, I> void complete(MutableDFA<S, I> dfa,
                                       Collection<? extends I> inputs,
                                       boolean minimize,
                                       boolean undefinedAcceptance) {
        S sink = null;

        for (S state : dfa) {
            for (I input : inputs) {
                S succ = dfa.getSuccessor(state, input);
                if (succ == null) {
                    if (sink == null) {
                        sink = dfa.addState(undefinedAcceptance);
                        for (I inputSym : inputs) {
                            dfa.addTransition(sink, inputSym, sink);
                        }
                    }
                    dfa.addTransition(state, input, sink);
                }
            }
        }

        if (minimize) {
            HopcroftMinimizer.minimizeDFAInvasive(dfa, inputs);
        }
    }

    public static <I> void complement(MutableDFA<?, I> dfa, Collection<? extends I> inputs) {
        dfa.flipAcceptance();
        complete(dfa, inputs, false, true);
    }
}
