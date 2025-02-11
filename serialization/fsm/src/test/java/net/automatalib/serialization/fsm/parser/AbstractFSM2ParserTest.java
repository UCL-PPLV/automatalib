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
package net.automatalib.serialization.fsm.parser;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import net.automatalib.automaton.UniversalDeterministicAutomaton;
import net.automatalib.automaton.base.AbstractCompactDeterministic;
import net.automatalib.exception.FormatException;
import net.automatalib.word.Word;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.testng.Assert;
import org.testng.annotations.Test;

public abstract class AbstractFSM2ParserTest {

    protected abstract UniversalDeterministicAutomaton<?, Character, ?, ?, ?> getParsedAutomaton(@Nullable Collection<Character> requiredInputs)
            throws IOException, FormatException;

    /**
     * Asserts that no NullPointerException is thrown by implementations of
     * {@link AbstractCompactDeterministic#getTransition(int, Object)}.
     * These implementations can be constructed by the parsers in this package.
     * The NullPointerException should not be thrown when getTransitions() is called with a symbol that is not in
     * {@link AbstractCompactDeterministic#getInputAlphabet()}.
     *
     * @throws IOException if a test .fsm file could not be read
     * @throws FormatException if a test .fsm was malformed
     */
    @Test
    public void testInputAlphabet() throws IOException, FormatException {

        final Collection<Character> existingInputs = Collections.singleton('a');
        final Collection<Character> nonExistingInputs = Collections.singleton('[');
        final Collection<Character> mixedInputs = Arrays.asList('a', '[');

        final Word<Character> existingWord = Word.fromString("aaaa");
        final Word<Character> nonExistingWord = Word.fromString("[[[[");
        final Word<Character> mixedWord = Word.fromString("a[a[a[");

        // check automatically parsed inputs
        final UniversalDeterministicAutomaton<?, Character, ?, ?, ?> automaticAutomaton = getParsedAutomaton(null);

        // just trace the word and ignore the result -- checks to not throw an exception
        automaticAutomaton.getState(existingWord);
        Assert.assertThrows(IllegalArgumentException.class, () -> automaticAutomaton.getState(nonExistingInputs));

        // check parsed automaton with existing inputs
        final UniversalDeterministicAutomaton<?, Character, ?, ?, ?> existingAutomaton =
                getParsedAutomaton(existingInputs);

        // just trace the word and ignore the result -- checks to not throw an exception
        existingAutomaton.getState(existingWord);
        Assert.assertThrows(IllegalArgumentException.class, () -> existingAutomaton.getState(nonExistingInputs));

        // check parsed automaton with non-existing inputs
        final UniversalDeterministicAutomaton<?, Character, ?, ?, ?> nonExistingAutomaton =
                getParsedAutomaton(nonExistingInputs);

        // check that we trace undefined transitions but don't throw an exception
        Assert.assertNull(nonExistingAutomaton.getState(nonExistingWord));

        // check parsed automaton with a mix of (non)-existing inputs
        final UniversalDeterministicAutomaton<?, Character, ?, ?, ?> mixedAutomaton = getParsedAutomaton(mixedInputs);

        // check that we trace undefined transitions but don't throw an exception
        Assert.assertNull(mixedAutomaton.getState(mixedWord));

    }
}
