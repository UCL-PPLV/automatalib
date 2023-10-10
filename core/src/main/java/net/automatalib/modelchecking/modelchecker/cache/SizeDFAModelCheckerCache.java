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
package net.automatalib.modelchecking.modelchecker.cache;

import java.util.Collection;

import net.automatalib.automaton.fsa.DFA;
import net.automatalib.modelchecking.ModelChecker;
import net.automatalib.modelchecking.ModelCheckerCache.DFAModelCheckerCache;

/**
 * A DFAModelCheckerCache that invalidates the cached counter examples when
 * {@link ModelChecker#findCounterExample(Object, Collection, Object)} is called with a DFA with a size different, and
 * an input alphabet different from the previous call.
 * <p>
 * In active learning the automaton increases in size with every proper counter example. Hence, these caches are useful
 * in between calls to disproving properties and finding counter examples to hypotheses.
 *
 * @param <I>
 *         the input type
 * @param <P>
 *         the property type
 */
public class SizeDFAModelCheckerCache<I, P, R> extends SizeModelCheckerCache<I, DFA<?, I>, P, R>
        implements DFAModelCheckerCache<I, P, R> {

    public SizeDFAModelCheckerCache(DFAModelChecker<I, P, R> modelChecker) {
        super(modelChecker);
    }
}
