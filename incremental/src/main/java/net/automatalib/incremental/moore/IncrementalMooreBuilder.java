/* Copyright (C) 2013-2023 TU Dortmund
 * This file is part of AutomataLib, http://www.automatalib.net/.
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
package net.automatalib.incremental.moore;

import java.util.ArrayList;
import java.util.List;

import net.automatalib.SupportsGrowingAlphabet;
import net.automatalib.automaton.transducer.MooreMachine;
import net.automatalib.graph.Graph;
import net.automatalib.incremental.ConflictException;
import net.automatalib.incremental.IncrementalConstruction;
import net.automatalib.incremental.mealy.AdaptiveMealyBuilder;
import net.automatalib.incremental.mealy.IncrementalMealyBuilder;
import net.automatalib.ts.output.MooreTransitionSystem;
import net.automatalib.word.Word;
import net.automatalib.word.WordBuilder;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A utility interface to share functionality between {@link IncrementalMealyBuilder}s and
 * {@link AdaptiveMealyBuilder}s.
 *
 * @param <I>
 *         input symbol type
 * @param <O>
 *         output symbol type
 */
public interface IncrementalMooreBuilder<I, O>
        extends IncrementalConstruction<MooreMachine<?, I, ?, O>, I>, SupportsGrowingAlphabet<I> {

    /**
     * Retrieves the output word for the given input word. If no definitive information for the input word exists, the
     * output for the longest known prefix will be returned.
     *
     * @param inputWord
     *         the input word
     * @param output
     *         a consumer for constructing the output word
     *
     * @return {@code true} if the information contained was complete (in this case,
     * {@code word.length() == output.size() + 1} will hold), {@code false} otherwise.
     */
    boolean lookup(Word<? extends I> inputWord, List<? super O> output);

    default Word<O> lookup(Word<? extends I> inputWord) {
        WordBuilder<O> wb = new WordBuilder<>(inputWord.size() + 1);
        lookup(inputWord, wb);
        return wb.toWord();
    }

    @Override
    default boolean hasDefinitiveInformation(Word<? extends I> word) {
        return lookup(word, new ArrayList<>(word.length() + 1));
    }

    /**
     * Incorporates a pair of input/output words into the stored information.
     *
     * @param inputWord
     *         the input word
     * @param outputWord
     *         the corresponding output word
     *
     * @throws ConflictException
     *         if this information conflicts with information already stored
     */
    void insert(Word<? extends I> inputWord, Word<? extends O> outputWord);

    @Override
    GraphView<I, O, ?, ?> asGraph();

    @Override
    MooreTransitionSystem<?, I, ?, O> asTransitionSystem();

    interface GraphView<I, O, N, E> extends Graph<N, E> {

        I getInputSymbol(E edge);

        O getOutputSymbol(N node);

        @Nullable N getInitialNode();
    }
}
