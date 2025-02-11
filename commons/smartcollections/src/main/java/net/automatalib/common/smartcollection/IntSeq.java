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
package net.automatalib.common.smartcollection;

import java.util.Arrays;
import java.util.List;
import java.util.PrimitiveIterator.OfInt;

/**
 * An {@link IntSeq} is an abstract read-only view on a finite, random-access data-structure for primitive integer
 * values. It allows for a unified view on integer arrays, {@link List lists} of integers, words with an accompanying
 * alphabet index function, etc.
 */
public interface IntSeq extends Iterable<Integer> {

    int size();

    int get(int index);

    @Override
    default OfInt iterator() {
        return new OfInt() {

            int curr;

            {
                this.curr = 0;
            }

            @Override
            public int nextInt() {
                return get(curr++);
            }

            @Override
            public boolean hasNext() {
                return curr < size();
            }
        };
    }

    static IntSeq of(int... ints) {
        return new IntSeq() {

            @Override
            public int size() {
                return ints.length;
            }

            @Override
            public int get(int index) {
                return ints[index];
            }

            @Override
            public String toString() {
                return Arrays.toString(ints);
            }
        };
    }

    static IntSeq of(List<Integer> ints) {
        return new IntSeq() {

            @Override
            public int size() {
                return ints.size();
            }

            @Override
            public int get(int index) {
                return ints.get(index);
            }

            @Override
            public String toString() {
                return ints.toString();
            }
        };
    }
}
