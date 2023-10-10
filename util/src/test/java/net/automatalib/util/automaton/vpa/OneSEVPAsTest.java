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
package net.automatalib.util.automaton.vpa;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import com.google.common.collect.Sets;
import net.automatalib.alphabet.VPAlphabet;
import net.automatalib.alphabet.impl.Alphabets;
import net.automatalib.alphabet.impl.DefaultVPAlphabet;
import net.automatalib.automaton.vpa.DefaultOneSEVPA;
import net.automatalib.automaton.vpa.Location;
import net.automatalib.util.automaton.Automata;
import net.automatalib.util.automaton.random.RandomAutomata;
import org.testng.Assert;
import org.testng.annotations.Test;

public class OneSEVPAsTest {

    @Test
    public void testMinimization() {

        final VPAlphabet<Character> alphabet = new DefaultVPAlphabet<>(Alphabets.characters('1', '3'),
                                                                       Alphabets.characters('a', 'c'),
                                                                       Alphabets.characters('r', 't'));
        final int size = 10;
        final double accProb = 0.5;
        final double initRetProb = 0.1;

        final DefaultOneSEVPA<Character> orig =
                RandomAutomata.randomOneSEVPA(new Random(0), size, alphabet, accProb, initRetProb, false);
        final DefaultOneSEVPA<Character> copy =
                RandomAutomata.randomOneSEVPA(new Random(0), size, alphabet, accProb, initRetProb, false);

        Assert.assertNotNull(copy);
        Assert.assertNotNull(orig);
        Assert.assertTrue(Automata.testEquivalence(orig, copy, alphabet));

        addRedundantState(copy, alphabet);

        Assert.assertTrue(orig.size() < copy.size());
        Assert.assertTrue(Automata.testEquivalence(orig, copy, alphabet));

        final DefaultOneSEVPA<Character> minimized = OneSEVPAs.minimize(copy, alphabet);

        Assert.assertNotNull(minimized);
        Assert.assertTrue(minimized.size() < copy.size());
        Assert.assertTrue(Automata.testEquivalence(copy, minimized, alphabet));
    }

    private static <I> void addRedundantState(DefaultOneSEVPA<I> automaton, VPAlphabet<? extends I> alphabet) {

        // cache reached states, so we copy the first state reached by two incoming transitions
        final Set<Location> locationCache = Sets.newHashSetWithExpectedSize(automaton.size());

        Location incomingLoc = null;
        I incomingInput = null;
        Location locToCopy = null;

        outer:
        for (Location l : automaton.getLocations()) {
            for (I i : alphabet.getInternalAlphabet()) {
                final Location succ = automaton.getInternalSuccessor(l, i);
                if (!locationCache.add(succ)) {
                    incomingLoc = l;
                    incomingInput = i;
                    locToCopy = succ;
                    break outer;
                }
            }
        }

        Assert.assertNotNull(incomingLoc);
        Assert.assertNotNull(incomingInput);
        Assert.assertNotNull(locToCopy);

        final Set<Location> oldStates = new HashSet<>(automaton.getLocations());
        final Location locCopy = automaton.addLocation(automaton.isAcceptingLocation(locToCopy));

        // make return transitions of old states behave identical for the new stack symbol
        for (I callSym : alphabet.getCallAlphabet()) {
            final int oldStackSym = automaton.encodeStackSym(locToCopy, callSym);
            final int newStackSym = automaton.encodeStackSym(locCopy, callSym);

            for (Location l : oldStates) {
                for (I retSym : alphabet.getReturnAlphabet()) {
                    final Location oldReturn = automaton.getReturnSuccessor(l, retSym, oldStackSym);
                    automaton.setReturnSuccessor(l, retSym, newStackSym, oldReturn);
                }
            }
        }

        // make internal transitions of new state behave identical to the state to copy
        for (I i : alphabet.getInternalAlphabet()) {
            final Location target = automaton.getInternalSuccessor(locToCopy, i);
            automaton.setInternalSuccessor(locCopy, i, target);
        }

        // make return transitions of new state behave identical to the state to copy
        for (I i : alphabet.getReturnAlphabet()) {
            for (int stackSym = 0; stackSym < automaton.getNumStackSymbols(); stackSym++) {
                final Location target = automaton.getReturnSuccessor(locToCopy, i, stackSym);
                automaton.setReturnSuccessor(locCopy, i, stackSym, target);
            }
        }

        // change old transition to redundant state
        automaton.setInternalSuccessor(incomingLoc, incomingInput, locCopy);
    }
}
