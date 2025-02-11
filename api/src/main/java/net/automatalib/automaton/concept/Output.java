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
package net.automatalib.automaton.concept;

import java.util.Collection;

import net.automatalib.word.Word;
import net.automatalib.word.WordBuilder;

/**
 * Feature for automata that compute an output.
 *
 * @param <I>
 *         input symbol type
 * @param <D>
 *         output domain type
 */
public interface Output<I, D> {

    D computeOutput(Iterable<? extends I> input);

    static <T> WordBuilder<T> getBuilderFor(Iterable<?> iterable) {
        if (iterable instanceof Word) {
            return new WordBuilder<>(((Word<?>) iterable).length());
        } else if (iterable instanceof Collection) {
            return new WordBuilder<>(((Collection<?>) iterable).size());
        } else {
            return new WordBuilder<>();
        }
    }
}
