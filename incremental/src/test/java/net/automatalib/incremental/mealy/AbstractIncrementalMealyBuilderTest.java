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
package net.automatalib.incremental.mealy;

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.util.ArrayList;

import net.automatalib.alphabet.Alphabet;
import net.automatalib.alphabet.GrowingAlphabet;
import net.automatalib.alphabet.impl.Alphabets;
import net.automatalib.alphabet.impl.GrowingMapAlphabet;
import net.automatalib.automaton.transducer.impl.CompactMealy;
import net.automatalib.common.util.IOUtil;
import net.automatalib.incremental.ConflictException;
import net.automatalib.serialization.dot.GraphDOT;
import net.automatalib.ts.output.MealyTransitionSystem;
import net.automatalib.word.Word;
import net.automatalib.word.WordBuilder;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@Test
public abstract class AbstractIncrementalMealyBuilderTest {

    private static final Alphabet<Character> TEST_ALPHABET = Alphabets.characters('a', 'c');
    private static final Word<Character> W_1 = Word.fromString("abc");
    private static final Word<Character> W_1_O = Word.fromString("xyz");
    private static final Word<Character> W_2 = Word.fromString("ac");
    private static final Word<Character> W_2_O = Word.fromString("xw");
    private static final Word<Character> W_3 = Word.fromString("acb");
    private static final Word<Character> W_3_O = Word.fromString("xwu");

    // Confluence Bug
    private static final Word<Character> W_B_1 = Word.fromString("aaa");
    private static final Word<Character> W_B_1_O = Word.fromString("xxx");
    private static final Word<Character> W_B_2 = Word.fromString("bba");
    private static final Word<Character> W_B_2_O = Word.fromString("xxx");
    private static final Word<Character> W_B_3 = Word.fromString("aabaa");
    private static final Word<Character> W_B_3_O = Word.fromString("xxxxx");

    private IncrementalMealyBuilder<Character, Character> incMealy;

    @BeforeClass
    public void setUp() {
        this.incMealy = createIncrementalMealyBuilder(TEST_ALPHABET);
    }

    protected abstract <I, O> IncrementalMealyBuilder<I, O> createIncrementalMealyBuilder(Alphabet<I> alphabet);

    protected abstract String getDOTResource();

    @Test
    public void testConfluenceBug() {
        incMealy.insert(W_B_1, W_B_1_O);
        incMealy.insert(W_B_2, W_B_2_O);
        incMealy.insert(W_B_3, W_B_3_O);

        Assert.assertFalse(incMealy.lookup(Word.fromString("aababaa"), new ArrayList<>()));
        // reset for further tests
        this.incMealy = createIncrementalMealyBuilder(TEST_ALPHABET);
    }

    @Test(dependsOnMethods = "testConfluenceBug")
    public void testLookup() {
        Assert.assertFalse(incMealy.hasDefinitiveInformation(W_1));
        Assert.assertFalse(incMealy.hasDefinitiveInformation(W_2));
        Assert.assertFalse(incMealy.hasDefinitiveInformation(W_3));

        incMealy.insert(W_1, W_1_O);
        Assert.assertTrue(incMealy.hasDefinitiveInformation(W_1));
        Assert.assertTrue(incMealy.hasDefinitiveInformation(W_1.prefix(2)));
        Assert.assertFalse(incMealy.hasDefinitiveInformation(W_1.append('a')));

        WordBuilder<Character> wb = new WordBuilder<>();

        Assert.assertTrue(incMealy.lookup(W_1, wb));
        Assert.assertEquals(wb.toWord(), W_1_O);
        wb.clear();
        Assert.assertTrue(incMealy.lookup(W_1.prefix(2), wb));
        Assert.assertEquals(wb.toWord(), W_1_O.prefix(2));
        wb.clear();
        Assert.assertFalse(incMealy.hasDefinitiveInformation(W_2));
        Assert.assertFalse(incMealy.hasDefinitiveInformation(W_3));

        incMealy.insert(W_2, W_2_O);
        Assert.assertTrue(incMealy.hasDefinitiveInformation(W_1));
        Assert.assertTrue(incMealy.hasDefinitiveInformation(W_2));
        Assert.assertFalse(incMealy.hasDefinitiveInformation(W_3));

        Assert.assertTrue(incMealy.lookup(W_2, wb));
        Assert.assertEquals(wb.toWord(), W_2_O);
        wb.clear();
        Assert.assertTrue(incMealy.lookup(W_2.prefix(1), wb));
        Assert.assertEquals(wb.toWord(), W_2_O.prefix(1));
        wb.clear();
        Assert.assertTrue(incMealy.lookup(W_1, wb));
        Assert.assertEquals(wb.toWord(), W_1_O);
        wb.clear();

        incMealy.insert(W_3, W_3_O);
        Assert.assertTrue(incMealy.hasDefinitiveInformation(W_1));
        Assert.assertTrue(incMealy.hasDefinitiveInformation(W_2));
        Assert.assertTrue(incMealy.hasDefinitiveInformation(W_3));

        Assert.assertTrue(incMealy.lookup(W_3, wb));
        Assert.assertEquals(wb.toWord(), W_3_O);
        wb.clear();
        Assert.assertTrue(incMealy.lookup(W_3.prefix(2), wb));
        Assert.assertEquals(wb.toWord(), W_3_O.prefix(2));
        wb.clear();
        Assert.assertTrue(incMealy.lookup(W_1, wb));
        Assert.assertEquals(wb.toWord(), W_1_O);
        wb.clear();
        Assert.assertTrue(incMealy.lookup(W_2, wb));
        Assert.assertEquals(wb.toWord(), W_2_O);
        wb.clear();
    }

    @Test(dependsOnMethods = "testLookup")
    public void testInsertSame() {
        incMealy.insert(W_1, W_1_O);
    }

    @Test(expectedExceptions = ConflictException.class, dependsOnMethods = "testLookup")
    public void testConflict() {
        incMealy.insert(W_1, W_3_O);
    }

    @Test(dependsOnMethods = "testLookup")
    public void testFindSeparatingWord() {
        CompactMealy<Character, Character> testMealy = new CompactMealy<>(TEST_ALPHABET);

        Word<Character> sepWord;
        sepWord = incMealy.findSeparatingWord(testMealy, TEST_ALPHABET, true);
        Assert.assertNull(sepWord);
        sepWord = incMealy.findSeparatingWord(testMealy, TEST_ALPHABET, false);
        Assert.assertEquals(sepWord, Word.epsilon());

        int s0 = testMealy.addInitialState();
        int s1 = testMealy.addState();
        int s2 = testMealy.addState();
        int s3 = testMealy.addState();
        int s4 = testMealy.addState();
        int s5 = testMealy.addState();

        testMealy.addTransition(s0, 'a', s1, 'x');
        testMealy.addTransition(s0, 'b', s2, 'u');
        testMealy.addTransition(s1, 'b', s3, 'y');
        testMealy.addTransition(s3, 'c', s4, 'z');
        testMealy.addTransition(s1, 'c', s5, 'w');

        sepWord = incMealy.findSeparatingWord(testMealy, TEST_ALPHABET, true);
        Assert.assertNull(sepWord);
        sepWord = incMealy.findSeparatingWord(testMealy, TEST_ALPHABET, false);
        Assert.assertEquals(sepWord, Word.fromString("acb"));

        testMealy.addTransition(s5, 'b', s4, 'u');
        sepWord = incMealy.findSeparatingWord(testMealy, TEST_ALPHABET, true);
        Assert.assertNull(sepWord);
        sepWord = incMealy.findSeparatingWord(testMealy, TEST_ALPHABET, false);
        Assert.assertNull(sepWord);

        testMealy.setTransition(s5, (Character) 'b', testMealy.getSuccessor(s5, (Character) 'b'), (Character) 'w');

        sepWord = incMealy.findSeparatingWord(testMealy, TEST_ALPHABET, true);
        Assert.assertEquals(sepWord, Word.fromString("acb"));
        sepWord = incMealy.findSeparatingWord(testMealy, TEST_ALPHABET, false);
        Assert.assertEquals(sepWord, Word.fromString("acb"));
    }

    @Test(dependsOnMethods = "testLookup")
    public void testVisualization() throws IOException {
        final StringWriter dotWriter = new StringWriter();
        final StringWriter expectedWriter = new StringWriter();

        try (Reader reader = IOUtil.asBufferedUTF8Reader(AbstractIncrementalMealyBuilderTest.class.getResourceAsStream(
                getDOTResource()))) {

            GraphDOT.write(incMealy.asGraph(), dotWriter);
            IOUtil.copy(reader, expectedWriter);

            Assert.assertEquals(dotWriter.toString(), expectedWriter.toString());
        }
    }

    @Test(dependsOnMethods = "testLookup")
    public void testTSView() {
        final MealyTransitionSystem<?, Character, ?, Character> tsView = incMealy.asTransitionSystem();
        final WordBuilder<Character> wb = new WordBuilder<>();

        tsView.trace(W_1, wb);
        Assert.assertEquals(wb.toWord(), W_1_O);
        wb.clear();

        tsView.trace(W_2, wb);
        Assert.assertEquals(wb.toWord(), W_2_O);
        wb.clear();

        tsView.trace(W_3, wb);
        Assert.assertEquals(wb.toWord(), W_3_O);
    }

    @Test
    public void testCounterexampleOfLengthOne() {
        final IncrementalMealyBuilder<Character, Character> incMealy = createIncrementalMealyBuilder(TEST_ALPHABET);
        incMealy.insert(Word.fromLetter('a'), Word.fromLetter('x'));

        final CompactMealy<Character, Character> dfa = new CompactMealy<>(TEST_ALPHABET);
        final Integer q0 = dfa.addInitialState();
        final Integer q1 = dfa.addState();

        dfa.addTransition(q0, 'a', q1, 'y');

        final Word<Character> ce = incMealy.findSeparatingWord(dfa, TEST_ALPHABET, false);
        Assert.assertNotNull(ce);
    }

    @Test(dependsOnMethods = "testLookup")
    public void testNewInputSymbol() {
        final GrowingAlphabet<Character> alphabet = new GrowingMapAlphabet<>(TEST_ALPHABET);
        final IncrementalMealyBuilder<Character, Character> growableBuilder = createIncrementalMealyBuilder(alphabet);

        growableBuilder.addAlphabetSymbol('d');
        growableBuilder.addAlphabetSymbol('d');

        final Word<Character> input1 = Word.fromString("dcba");
        final Word<Character> output1 = Word.fromString("1234");

        growableBuilder.insert(input1, output1);

        Assert.assertTrue(growableBuilder.hasDefinitiveInformation(input1));
        Assert.assertEquals(growableBuilder.lookup(input1), output1);

        growableBuilder.addAlphabetSymbol('e');

        final Word<Character> input2 = Word.fromString("ddee");

        Assert.assertFalse(growableBuilder.hasDefinitiveInformation(input2));
        Assert.assertEquals(growableBuilder.lookup(input2), Word.fromLetter('1'));
    }

}
