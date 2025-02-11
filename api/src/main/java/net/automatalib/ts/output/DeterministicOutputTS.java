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
package net.automatalib.ts.output;

import java.util.List;

import net.automatalib.ts.DeterministicTransitionSystem;

public interface DeterministicOutputTS<S, I, T, O> extends DeterministicTransitionSystem<S, I, T> {

    default boolean trace(Iterable<? extends I> input, List<? super O> output) {
        final S init = getInitialState();

        return init != null && trace(init, input, output);
    }

    boolean trace(S state, Iterable<? extends I> input, List<? super O> output);
}
