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
package net.automatalib.automaton.impl;

import net.automatalib.alphabet.Alphabet;
import net.automatalib.alphabet.impl.Alphabets;
import net.automatalib.automaton.fsa.impl.CompactDFA;
import net.automatalib.automaton.util.TestUtil;
import net.automatalib.word.Word;
import org.testng.annotations.Test;

public class DFATests {

    @Test
    public void testOutputOfUndefinedTransitions() {
        final Alphabet<Character> sigma = Alphabets.characters('a', 'b');
        final CompactDFA<Character> dfa = new CompactDFA<>(sigma);

        final int q0 = dfa.addIntInitialState(true);
        final int q1 = dfa.addIntState(false);

        dfa.setTransition(q0, sigma.getSymbolIndex('a'), q1);
        dfa.setTransition(q1, sigma.getSymbolIndex('b'), q0);

        TestUtil.checkOutput(dfa, Word.fromString("ababab"), true);
        TestUtil.checkOutput(dfa, Word.fromString("aabb"), false);
        TestUtil.checkOutput(dfa, Word.fromString("baba"), false);
    }
}
