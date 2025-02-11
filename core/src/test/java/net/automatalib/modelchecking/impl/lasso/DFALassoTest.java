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
package net.automatalib.modelchecking.impl.lasso;

import java.util.Collection;

import net.automatalib.automaton.fsa.DFA;
import net.automatalib.common.util.collection.CollectionUtil;
import net.automatalib.modelchecking.impl.DFALassoImpl;
import net.automatalib.word.Word;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.testng.Assert;
import org.testng.annotations.Test;

public class DFALassoTest extends AbstractLassoTest<DFALassoImpl<String>> {

    @Override
    protected DFALassoImpl<String> getLasso(Word<String> prefix, Word<String> loop, int unfoldTimes) {
        return new DFALassoImpl<>(new DFAMock(prefix, loop), getAlphabet(), unfoldTimes);
    }

    @Test
    public void testGetOutput() {
        final DFALassoImpl<String> lasso = getLasso(Word.epsilon(), Word.fromSymbols("a"), 1);
        Assert.assertTrue(lasso.getOutput());
    }

    private static class DFAMock implements DFA<Integer, String> {

        private final Word<String> prefix;
        private final Word<String> word;

        DFAMock(Word<String> prefix, Word<String> loop) {
            this.prefix = prefix;
            word = prefix.concat(loop);
        }

        @Override
        public Collection<Integer> getStates() {
            return CollectionUtil.intRange(0, word.length());
        }

        @Override
        public @Nullable Integer getTransition(Integer state, String input) {
            final Integer result;

            if (word.getSymbol(state).equals(input)) {
                if (state < word.length() - 1) {
                    result = state + 1;
                } else {
                    result = prefix.length();
                }
            } else {
                result = null;
            }

            return result;
        }

        @Override
        public boolean isAccepting(Integer state) {
            // dfa is prefix-closed; always return true
            return true;
        }

        @Override
        public Integer getInitialState() {
            return 0;
        }
    }
}
