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
package net.automatalib.ts.acceptor;

import java.util.Collection;

import net.automatalib.ts.AcceptorPowersetViewTS;
import net.automatalib.ts.UniversalTransitionSystem;
import net.automatalib.ts.powerset.AcceptorPowersetView;

/**
 * A transition system whose semantics are defined by whether a state is "accepting" or not.
 *
 * @param <S>
 *         state class
 * @param <I>
 *         input symbol class
 */
public interface AcceptorTS<S, I> extends UniversalTransitionSystem<S, I, S, Boolean, Void> {

    /**
     * Determines whether the given input word is accepted by this acceptor.
     *
     * @param input
     *         the input word.
     *
     * @return {@code true} if the input word is accepted, {@code false} otherwise.
     */
    default boolean accepts(Iterable<? extends I> input) {
        Collection<S> states = getStates(input);

        return isAccepting(states);
    }

    /**
     * Checks whether the given state is accepting.
     *
     * @param state
     *         the state
     *
     * @return {@code true} if the state is accepting, {@code false} otherwise.
     */
    boolean isAccepting(S state);

    boolean isAccepting(Collection<? extends S> states);

    @Override
    default Boolean getStateProperty(S state) {
        return isAccepting(state);
    }

    @Override
    default Void getTransitionProperty(S transition) {
        return null;
    }

    @Override
    default S getSuccessor(S transition) {
        return transition;
    }

    @Override
    default AcceptorPowersetViewTS<?, I, S> powersetView() {
        return new AcceptorPowersetView<>(this);
    }
}
