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
package net.automatalib.automaton.visualization;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.automatalib.alphabet.Alphabet;
import net.automatalib.automaton.UniversalDeterministicAutomaton;
import net.automatalib.automaton.concept.StateIDs;
import net.automatalib.common.util.HashUtil;
import net.automatalib.common.util.Pair;
import net.automatalib.common.util.Triple;
import net.automatalib.visualization.DefaultVisualizationHelper;
import net.automatalib.visualization.VisualizationHelper;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Default {@link VisualizationHelper} for procedural systems.
 *
 * @param <S>
 *         state type
 * @param <I>
 *         input symbol type
 */
public class ProceduralVisualizationHelper<S, I> extends DefaultVisualizationHelper<Pair<I, S>, Triple<I, I, S>> {

    private final Map<I, UniversalDeterministicAutomaton<S, I, ?, ?, ?>> subModels;
    private final Map<I, StateIDs<S>> stateIDs;
    private final Alphabet<I> internalAlphabet;

    // cast is fine, because we make sure to only query states belonging to the respective procedures
    @SuppressWarnings("unchecked")
    public ProceduralVisualizationHelper(Alphabet<I> internalAlphabet,
                                         Map<I, ? extends UniversalDeterministicAutomaton<? extends S, I, ?, ?, ?>> subModels) {
        this.internalAlphabet = internalAlphabet;
        this.subModels = (Map<I, UniversalDeterministicAutomaton<S, I, ?, ?, ?>>) subModels;
        this.stateIDs = new HashMap<>(HashUtil.capacity(subModels.size()));

        for (Entry<I, ? extends UniversalDeterministicAutomaton<? extends S, I, ?, ?, ?>> e : subModels.entrySet()) {
            this.stateIDs.put(e.getKey(), (StateIDs<S>) e.getValue().stateIDs());
        }
    }

    @Override
    protected Collection<Pair<I, S>> initialNodes() {
        final List<Pair<I, S>> initialNodes = new ArrayList<>(this.subModels.size());

        for (Entry<I, UniversalDeterministicAutomaton<S, I, ?, ?, ?>> e : subModels.entrySet()) {
            final S init = e.getValue().getInitialState();
            assert init != null;
            initialNodes.add(Pair.of(e.getKey(), init));
        }

        return initialNodes;
    }

    @Override
    public boolean getNodeProperties(Pair<I, S> node, Map<String, String> properties) {
        super.getNodeProperties(node, properties);

        final I identifier = node.getFirst();
        @SuppressWarnings("assignment") // we only use identifiers for which procedures exists
        final @NonNull UniversalDeterministicAutomaton<S, I, ?, ?, ?> subModel = subModels.get(identifier);
        @SuppressWarnings("assignment") // we only use identifiers for which procedures exists
        final @NonNull StateIDs<S> stateID = stateIDs.get(identifier);

        if (Boolean.TRUE.equals(subModel.getStateProperty(node.getSecond()))) {
            properties.put(NodeAttrs.SHAPE, NodeShapes.DOUBLECIRCLE);
        } else {
            properties.put(NodeAttrs.SHAPE, NodeShapes.CIRCLE);
        }

        properties.put(NodeAttrs.LABEL, node.getFirst() + " " + stateID.getStateId(node.getSecond()));

        return true;
    }

    @Override
    public boolean getEdgeProperties(Pair<I, S> src,
                                     Triple<I, I, S> edge,
                                     Pair<I, S> tgt,
                                     Map<String, String> properties) {
        super.getEdgeProperties(src, edge, tgt, properties);

        final S state = src.getSecond();
        final I identifier = edge.getFirst();
        final I input = edge.getSecond();
        @SuppressWarnings("assignment") // we only use identifier for which procedures exists
        final @NonNull UniversalDeterministicAutomaton<S, I, ?, ?, ?> subModel = subModels.get(identifier);

        final Object tp = subModel.getTransitionProperty(state, input);

        if (tp != null) {
            properties.put(NodeAttrs.LABEL, input + " / " + tp);
        } else {
            properties.put(EdgeAttrs.LABEL, String.valueOf(edge.getSecond()));
        }

        if (!internalAlphabet.containsSymbol(edge.getSecond())) {
            properties.put(EdgeAttrs.STYLE, EdgeStyles.BOLD);
        }

        return true;
    }

}
