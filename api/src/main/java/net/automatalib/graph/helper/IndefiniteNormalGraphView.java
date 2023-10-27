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
package net.automatalib.graph.helper;

import java.util.Iterator;

import net.automatalib.common.util.mapping.MutableMapping;
import net.automatalib.graph.IndefiniteGraph;
import net.automatalib.graph.IndefiniteSimpleGraph;
import org.checkerframework.checker.nullness.qual.Nullable;

public class IndefiniteNormalGraphView<N, G extends IndefiniteSimpleGraph<N>> implements IndefiniteGraph<N, N> {

    protected final G simpleGraph;

    public IndefiniteNormalGraphView(G simpleGraph) {
        this.simpleGraph = simpleGraph;
    }

    @Override
    public Iterator<N> getOutgoingEdgesIterator(N node) {
        return simpleGraph.getAdjacentTargetsIterator(node);
    }

    @Override
    public N getTarget(N edge) {
        return edge;
    }

    @Override
    public boolean isConnected(N source, N target) {
        return simpleGraph.isConnected(source, target);
    }

    @Override
    public <@Nullable V> MutableMapping<N, V> createStaticNodeMapping() {
        return simpleGraph.createStaticNodeMapping();
    }

    @Override
    public <@Nullable V> MutableMapping<N, V> createDynamicNodeMapping() {
        return simpleGraph.createDynamicNodeMapping();
    }
}
