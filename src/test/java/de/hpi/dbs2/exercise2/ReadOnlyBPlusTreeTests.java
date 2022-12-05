package de.hpi.dbs2.exercise2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ReadOnlyBPlusTreeTests {
    @Test
    public void testTreesAreValid() {
        Assertions.assertTrue(TestFixtures.exampleTree.isValid());

        AbstractBPlusTree emptyTree1 = new ReadOnlyBPlusTree(4);
        Assertions.assertTrue(emptyTree1.isValid());

        AbstractBPlusTree emptyTree2 = new ReadOnlyBPlusTree(new InitialRootNode(4));
        Assertions.assertTrue(emptyTree2.isValid());

        AbstractBPlusTree singleEntryTree = new ReadOnlyBPlusTree(
            new InitialRootNode(4, TestFixtures.getOrCreateEntry(0))
        );
        Assertions.assertTrue(singleEntryTree.isValid());

        AbstractBPlusTree dualEntryTree = new ReadOnlyBPlusTree(
            new InitialRootNode(4, TestFixtures.getOrCreateEntry(0))
        );
        Assertions.assertTrue(dualEntryTree.isValid());
    }

    @Test
    public void testGetOrNull() {
        Assertions.assertNull(TestFixtures.exampleTree.getOrNull(0));
        Assertions.assertEquals(TestFixtures.entries.get(5).getValue(), TestFixtures.exampleTree.getOrNull(5));
        Assertions.assertEquals(TestFixtures.entries.get(7).getValue(), TestFixtures.exampleTree.getOrNull(7));
        Assertions.assertEquals(TestFixtures.entries.get(13).getValue(), TestFixtures.exampleTree.getOrNull(13));
        Assertions.assertEquals(TestFixtures.entries.get(19).getValue(), TestFixtures.exampleTree.getOrNull(19));
        Assertions.assertNull(TestFixtures.exampleTree.getOrNull(30));
        Assertions.assertNull(TestFixtures.exampleTree.getOrNull(35));
        Assertions.assertNull(TestFixtures.exampleTree.getOrNull(44));
        Assertions.assertNull(TestFixtures.exampleTree.getOrNull(99));
    }
}
