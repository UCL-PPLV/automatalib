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
package net.automatalib.ts.modal.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.function.Predicate;

import net.automatalib.alphabet.Alphabet;
import net.automatalib.alphabet.impl.Alphabets;
import net.automatalib.ts.modal.MutableModalTransitionSystem;
import net.automatalib.ts.modal.transition.ModalEdgeProperty;
import net.automatalib.ts.modal.transition.ModalEdgeProperty.ModalType;
import net.automatalib.ts.modal.transition.MutableModalEdgeProperty;
import net.automatalib.ts.modal.transition.impl.ModalEdgePropertyImpl;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class CompactMTSTest {

    @DataProvider(name = "default")
    private static Object[][] generateTSWithTP() {
        final Alphabet<Character> alphabet = Alphabets.characters('a', 'd');
        final CompactMTS<Character> mts = new CompactMTS<>(alphabet);

        final Integer s0 = mts.addInitialState();
        final Integer s1 = mts.addState();
        final Integer s2 = mts.addState();
        final ModalEdgeProperty tprop1 = new ModalEdgePropertyImpl(null);
        final ModalEdgeProperty tprop2 = new ModalEdgePropertyImpl(ModalType.MAY);

        return new Object[][] {{mts, s0, s1, 'a', tprop1}, {mts, s0, s2, 'd', tprop2}, {mts, s2, s0, 'a', tprop2}};
    }

    @Test(dataProvider = "default", description = "Add Transition with non-null Property")
    <A extends MutableModalTransitionSystem<S, I, T, TP>, S, I, T, TP extends MutableModalEdgeProperty> void addTransitionWithTP(
            A mts,
            S src,
            S dest,
            I label,
            TP tprop) {
        Assert.assertTrue(mts.getStates().containsAll(Arrays.asList(src, dest)));
        Assert.assertTrue(mts.getInputAlphabet().contains(label));
        Assert.assertTrue(mts.getTransitions(src, label).isEmpty());
        Assert.assertNotNull(tprop);

        int tsize = 0;
        for (S s : mts.getStates()) {
            tsize += mts.getTransitions(s).size();
        }

        T t = mts.addTransition(src, label, dest, tprop);
        Assert.assertNotNull(t);
        Assert.assertEquals(mts.getSuccessor(t), dest);
        Assert.assertEquals(mts.getTransitionProperty(t), tprop);
        Assert.assertEquals(mts.getTransitions(src, label), Collections.singleton(t));

        int tsizeL = 0;
        for (S s : mts.getStates()) {
            tsizeL += mts.getTransitions(s).size();
        }
        Assert.assertEquals(tsizeL, tsize + 1);
    }

    @Test(dataProvider = "default", description = "Add Transition with null as Property")
    <A extends MutableModalTransitionSystem<S, I, T, TP>, S, I, T, TP extends MutableModalEdgeProperty> void addTransitionWithoutTP(
            A mts,
            S src,
            S dest,
            I label,
            TP tprop) {

        Assert.assertTrue(mts.getStates().containsAll(Arrays.asList(src, dest)));
        Assert.assertTrue(mts.getInputAlphabet().contains(label));
        Assert.assertTrue(mts.getTransitions(src, label).isEmpty());

        T t = mts.addTransition(src, label, dest, null);
        Assert.assertNotNull(t);
        Assert.assertEquals(mts.getSuccessor(t), dest);
        Assert.assertNotNull(mts.getTransitionProperty(t));
        Assert.assertEquals(mts.getTransitions(src, label), Collections.singleton(t));
    }

    @Test(dataProvider = "default", description = "Create Transition with null as Property and then add it")
    <A extends MutableModalTransitionSystem<S, I, T, TP>, S, I, T, TP extends MutableModalEdgeProperty> void addTransitionWithExternT(
            A mts,
            S src,
            S dest,
            I label,
            TP tprop) {
        Assert.assertTrue(mts.getStates().containsAll(Arrays.asList(src, dest)));
        Assert.assertTrue(mts.getInputAlphabet().contains(label));
        Assert.assertTrue(mts.getTransitions(src, label).isEmpty());

        T t = mts.createTransition(dest, null);
        mts.addTransition(src, label, t);
        Assert.assertNotNull(t);
        Assert.assertEquals(mts.getSuccessor(t), dest);
        Assert.assertNotNull(mts.getTransitionProperty(t));
        Assert.assertEquals(mts.getTransitions(src, label), Collections.singleton(t));
    }

    @Test(dataProvider = "default", description = "Create Transition with non-null Property and then add it")
    <A extends MutableModalTransitionSystem<S, I, T, TP>, S, I, T, TP extends MutableModalEdgeProperty> void addTransitionWithExternT2(
            A mts,
            S src,
            S dest,
            I label,
            TP tprop) {
        Assert.assertTrue(mts.getStates().containsAll(Arrays.asList(src, dest)));
        Assert.assertTrue(mts.getInputAlphabet().contains(label));
        Assert.assertTrue(mts.getTransitions(src, label).isEmpty());

        T t = mts.createTransition(dest, tprop);
        mts.addTransition(src, label, t);
        Assert.assertNotNull(t);
        Assert.assertEquals(mts.getSuccessor(t), dest);
        Assert.assertNotNull(mts.getTransitionProperty(t));
        Assert.assertEquals(mts.getTransitions(src, label), Collections.singleton(t));
    }

    @Test(description = "Example graph from Jasper")
    void testJasper() {
        final Alphabet<String> alphabet = Alphabets.closedCharStringRange('a', 'b');
        final CompactMTS<String> s = new CompactMTS<>(alphabet);

        final Integer as0 = s.addInitialState();
        final Integer as1 = s.addState();
        final Integer as2 = s.addState();

        final MTSTransition<MutableModalEdgeProperty> t1 = s.addModalTransition(as0, "a", as0, ModalType.MUST);
        final MTSTransition<MutableModalEdgeProperty> t2 = s.addModalTransition(as0, "b", as1, ModalType.MUST);
        final MTSTransition<MutableModalEdgeProperty> t3 = s.addModalTransition(as1, "a", as1, ModalType.MUST);
        final MTSTransition<MutableModalEdgeProperty> t4 = s.addModalTransition(as1, "b", as2, ModalType.MAY);
        final MTSTransition<MutableModalEdgeProperty> t5 = s.addModalTransition(as2, "a", as2, ModalType.MAY);
        final MTSTransition<MutableModalEdgeProperty> t6 = s.addModalTransition(as2, "b", as2, ModalType.MAY);

        final Collection<MTSTransition<MutableModalEdgeProperty>> r1 = s.getTransitions(as0, "a");
        Assert.assertEquals(r1, Collections.singleton(t1));
        Assert.assertTrue(r1.stream().allMatch(propertyPredicate(as0, ModalType.MUST)));

        final Collection<MTSTransition<MutableModalEdgeProperty>> r2 = s.getTransitions(as0, "b");
        Assert.assertEquals(r2, Collections.singleton(t2));
        Assert.assertTrue(r2.stream().allMatch(propertyPredicate(as1, ModalType.MUST)));

        final Collection<MTSTransition<MutableModalEdgeProperty>> r3 = s.getTransitions(as1, "a");
        Assert.assertEquals(r3, Collections.singleton(t3));
        Assert.assertTrue(r3.stream().allMatch(propertyPredicate(as1, ModalType.MUST)));

        final Collection<MTSTransition<MutableModalEdgeProperty>> r4 = s.getTransitions(as1, "b");
        Assert.assertEquals(r4, Collections.singleton(t4));
        Assert.assertTrue(r4.stream().allMatch(propertyPredicate(as2, ModalType.MAY)));

        final Collection<MTSTransition<MutableModalEdgeProperty>> r5 = s.getTransitions(as2, "a");
        Assert.assertEquals(r5, Collections.singleton(t5));
        Assert.assertTrue(r5.stream().allMatch(propertyPredicate(as2, ModalType.MAY)));

        final Collection<MTSTransition<MutableModalEdgeProperty>> r6 = s.getTransitions(as2, "b");
        Assert.assertEquals(r6, Collections.singleton(t6));
        Assert.assertTrue(r6.stream().allMatch(propertyPredicate(as2, ModalType.MAY)));
    }

    private static Predicate<MTSTransition<MutableModalEdgeProperty>> propertyPredicate(Integer tgt,
                                                                                        ModalType modalType) {
        return t -> Objects.equals(t.getTarget(), tgt) && t.getProperty().getModalType() == modalType;
    }
}
