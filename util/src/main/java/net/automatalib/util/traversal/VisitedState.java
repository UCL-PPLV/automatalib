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
package net.automatalib.util.traversal;

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Enum to use for indicating if a node/state has been visited.
 * <p>
 * Note that this enum only declares one value. The value {@link #NOT_VISITED} for unvisited nodes/states is identified
 * with {@code null}.
 */
public enum VisitedState {
    /**
     * Nodes that have already been visited.
     */
    VISITED;

    /**
     * Nodes that have not yet been visited.
     */
    public static final @Nullable VisitedState NOT_VISITED = null;
}
