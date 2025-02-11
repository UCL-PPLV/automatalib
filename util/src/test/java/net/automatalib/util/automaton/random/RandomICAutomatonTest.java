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
package net.automatalib.util.automaton.random;

import java.util.Random;

import net.automatalib.alphabet.Alphabet;
import net.automatalib.alphabet.impl.Alphabets;
import net.automatalib.automaton.fsa.impl.CompactDFA;
import org.testng.Assert;
import org.testng.annotations.Test;

public class RandomICAutomatonTest {

    @Test
    public void testRandomICAutomaton() {

        final Random random = new Random(42);
        final int size = 20;
        final Alphabet<Character> alphabet = Alphabets.characters('a', 'c');

        final CompactDFA<Character> automaton = RandomAutomata.randomICDFA(random, size, alphabet, false);

        for (Integer s : automaton) {
            for (Character i : alphabet) {
                Assert.assertNotNull(automaton.getSuccessor(s, i));
            }
        }
    }
}
