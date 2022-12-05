package exercise2;

import de.hpi.dbs2.exercise2.AbstractBPlusTree;
import de.hpi.dbs2.exercise2.BPlusTreeNode;
import de.hpi.dbs2.exercise2.ReadOnlyBPlusTree;
import de.hpi.dbs2.exercise2.ValueReference;
import de.hpi.dbs2.exerciseframework.ChosenImplementationUtilsKt;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.IntStream;

public class BPlusTreeImplementationTests {
    private AbstractBPlusTree getImplementation(int order) {
        return ChosenImplementationUtilsKt.getChosenImplementation(
            new BPlusTreeJava(order),
            new BPlusTreeKotlin(order)
        );
    }

    @Test
    public void testTreeInsertRandomSet() {
        AbstractBPlusTree tree = getImplementation(4);

        Random random = new Random(1);

        int entryCount = 15;
        List<Integer> keys = new ArrayList<>(
            IntStream.range(0, entryCount).boxed().toList()
        );
        Collections.shuffle(keys, random);
        List<AbstractBPlusTree.Entry> expectedEntries = new ArrayList<>(entryCount);

        for (int i = 0; i < keys.size(); i++) {
            AbstractBPlusTree.Entry entry = new AbstractBPlusTree.Entry(
                // "random" key -> linear value
                keys.get(i), new ValueReference(i)
            );
            expectedEntries.add(entry);
            tree.insert(entry);
            Assertions.assertTrue(tree.isValid());
        }
        expectedEntries.sort(Comparator.comparingInt(
            AbstractBPlusTree.Entry::getKey
        ));

        List<AbstractBPlusTree.Entry> entries = tree.getEntries().toList();
        Assertions.assertEquals(entryCount, entries.size());
        Assertions.assertIterableEquals(expectedEntries, entries);
    }

    @Test
    public void testTreeInsert() {
        AbstractBPlusTree expectedTree = new ReadOnlyBPlusTree(BPlusTreeNode.buildTree(4,
            (Object[]) new AbstractBPlusTree.Entry[][]{
                new AbstractBPlusTree.Entry[]{
                    new AbstractBPlusTree.Entry(1, new ValueReference(6)),
                    new AbstractBPlusTree.Entry(2, new ValueReference(1)),
                    new AbstractBPlusTree.Entry(3, new ValueReference(4))
                },
                new AbstractBPlusTree.Entry[]{
                    new AbstractBPlusTree.Entry(4, new ValueReference(3)),
                    new AbstractBPlusTree.Entry(7, new ValueReference(2))
                },
                new AbstractBPlusTree.Entry[]{
                    new AbstractBPlusTree.Entry(8, new ValueReference(5)),
                    new AbstractBPlusTree.Entry(9, new ValueReference(7))
                }
            }
        ));

        AbstractBPlusTree tree = getImplementation(4);

        tree.insert(2, new ValueReference(1));
        Assertions.assertTrue(tree.isValid());

        tree.insert(7, new ValueReference(2));
        Assertions.assertTrue(tree.isValid());

        tree.insert(4, new ValueReference(3));
        Assertions.assertTrue(tree.isValid());

        tree.insert(3, new ValueReference(4));
        Assertions.assertTrue(tree.isValid());

        tree.insert(8, new ValueReference(5));
        Assertions.assertTrue(tree.isValid());

        tree.insert(1, new ValueReference(6));
        Assertions.assertTrue(tree.isValid());

        tree.insert(9, new ValueReference(7));
        Assertions.assertTrue(tree.isValid());

        Assertions.assertEquals(expectedTree, tree);
    }

    @Test
    public void testTreeInsertOverwrite() {
        AbstractBPlusTree tree = getImplementation(4);

        AbstractBPlusTree.Entry initialEntry1 = new AbstractBPlusTree.Entry(
            1, new ValueReference(1)
        );
        ValueReference returnValue1 = tree.insert(initialEntry1);
        Assertions.assertNull(returnValue1);

        AbstractBPlusTree.Entry overwriteEntry1 = new AbstractBPlusTree.Entry(
            1, new ValueReference(2)
        );
        ValueReference returnValue2 = tree.insert(overwriteEntry1);
        Assertions.assertTrue(tree.isValid());
        Assertions.assertEquals(new ValueReference(1), returnValue2);

        List<AbstractBPlusTree.Entry> entries1 = tree.getEntries().toList();
        Assertions.assertEquals(1, entries1.size());
        Assertions.assertFalse(entries1.contains(initialEntry1));
        Assertions.assertTrue(entries1.contains(overwriteEntry1));

        AbstractBPlusTree.Entry initialEntry2 = new AbstractBPlusTree.Entry(
            2, new ValueReference(3)
        );
        ValueReference returnValue3 = tree.insert(initialEntry2);
        Assertions.assertNull(returnValue3);

        AbstractBPlusTree.Entry overwriteEntry2 = new AbstractBPlusTree.Entry(
            2, new ValueReference(4)
        );
        ValueReference returnValue4 = tree.insert(overwriteEntry2);
        Assertions.assertTrue(tree.isValid());
        Assertions.assertEquals(new ValueReference(3), returnValue4);

        List<AbstractBPlusTree.Entry> entries2 = tree.getEntries().toList();
        Assertions.assertEquals(2, entries2.size());
        Assertions.assertFalse(entries2.contains(initialEntry2));
        Assertions.assertTrue(entries2.contains(overwriteEntry2));

        tree.insert(4, new ValueReference(5));
        tree.insert(3, new ValueReference(6));
        tree.insert(3, new ValueReference(7));
        tree.insert(4, new ValueReference(8));
        Assertions.assertEquals(new ValueReference(7), tree.getOrNull(3));
        Assertions.assertEquals(new ValueReference(8), tree.getOrNull(4));
    }
}
