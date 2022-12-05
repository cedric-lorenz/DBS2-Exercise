package de.hpi.dbs2.exercise2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class BPlusTreeNodeTests {
    @Test
    public void testTreeBuilder() {
        BPlusTreeNode<?> builtTree = BPlusTreeNode.buildTree(4,
            (Object[]) new AbstractBPlusTree.Entry[][]{
                new AbstractBPlusTree.Entry[]{
                    new AbstractBPlusTree.Entry(2, new ValueReference(0)),
                    new AbstractBPlusTree.Entry(3, new ValueReference(1)),
                    new AbstractBPlusTree.Entry(5, new ValueReference(2))
                },
                new AbstractBPlusTree.Entry[]{
                    new AbstractBPlusTree.Entry(7, new ValueReference(3)),
                    new AbstractBPlusTree.Entry(11, new ValueReference(4))
                }
            }
        );
        Assertions.assertTrue(builtTree.isValid());
        InnerNode expectedTree = new InnerNode(4,
            new LeafNode(4,
                new AbstractBPlusTree.Entry(2, new ValueReference(0)),
                new AbstractBPlusTree.Entry(3, new ValueReference(1)),
                new AbstractBPlusTree.Entry(5, new ValueReference(2))
            ),
            new LeafNode(4,
                new AbstractBPlusTree.Entry(7, new ValueReference(3)),
                new AbstractBPlusTree.Entry(11, new ValueReference(4))
            )
        );
        expectedTree.fixLeafLinks();
        Assertions.assertTrue(expectedTree.isValid());
        Assertions.assertEquals(expectedTree, builtTree);
    }

    @Test
    public void testExampleTreeIsValid() {
        Assertions.assertTrue(TestFixtures.exampleRoot.isValid());
    }

    @Test
    public void testNodeHeight() {
        Assertions.assertEquals(2, TestFixtures.exampleRoot.getHeight());
        Assertions.assertEquals(1, ((InnerNode) TestFixtures.exampleRoot).getChildNode(0).getHeight());
        Assertions.assertEquals(0, TestFixtures.leaves[0].getHeight());
    }

    @Test
    public void testNodeSize() {
        LeafNode leafNode0 = new LeafNode(4);
        Assertions.assertFalse(leafNode0.isValid());
        LeafNode leafNode1 = new LeafNode(4,
            TestFixtures.getOrCreateEntry(0)
        );
        Assertions.assertFalse(leafNode1.isValid());
        LeafNode leafNode2 = new LeafNode(4,
            TestFixtures.getOrCreateEntry(0),
            TestFixtures.getOrCreateEntry(1)
        );
        Assertions.assertTrue(leafNode2.isValid());
        LeafNode leafNode3 = new LeafNode(4,
            TestFixtures.getOrCreateEntry(0),
            TestFixtures.getOrCreateEntry(1),
            TestFixtures.getOrCreateEntry(2)
        );
        Assertions.assertTrue(leafNode3.isValid());
        Assertions.assertThrowsExactly(IllegalArgumentException.class,
            // leaves can only have order-1 references
            () -> new LeafNode(4,
                TestFixtures.getOrCreateEntry(0),
                TestFixtures.getOrCreateEntry(1),
                TestFixtures.getOrCreateEntry(2),
                TestFixtures.getOrCreateEntry(3)
            )
        );

        Assertions.assertEquals(0, leafNode0.getNodeSize());
        Assertions.assertEquals(1, leafNode1.getNodeSize());
        Assertions.assertEquals(2, leafNode2.getNodeSize());

        InnerNode innerNode0 = new InnerNode(4);
        Assertions.assertFalse(innerNode0.isValid());
        InnerNode innerNode1 = new InnerNode(4,
            TestFixtures.leaves[0]
        );
        Assertions.assertFalse(innerNode1.isValid());
        InnerNode innerNode4 = new InnerNode(4,
            TestFixtures.leaves[0],
            TestFixtures.leaves[1],
            TestFixtures.leaves[2],
            TestFixtures.leaves[3]
        );
        Assertions.assertTrue(innerNode4.isValid());

        Assertions.assertEquals(0, innerNode0.getNodeSize());
        Assertions.assertEquals(1, innerNode1.getNodeSize());
        Assertions.assertEquals(4, innerNode4.getNodeSize());

        Assertions.assertTrue(leafNode0.isEmpty());
        Assertions.assertTrue(innerNode0.isEmpty());

        Assertions.assertTrue(leafNode3.isFull());
        Assertions.assertTrue(innerNode4.isFull());
    }

    @Test
    public void testFind() {
        Assertions.assertEquals(TestFixtures.leaves[0], TestFixtures.exampleRoot.findLeaf(0));
        Assertions.assertEquals(TestFixtures.leaves[0], TestFixtures.exampleRoot.findLeaf(5));
        Assertions.assertEquals(TestFixtures.leaves[1], TestFixtures.exampleRoot.findLeaf(7));
        Assertions.assertEquals(TestFixtures.leaves[2], TestFixtures.exampleRoot.findLeaf(13));
        Assertions.assertEquals(TestFixtures.leaves[2], TestFixtures.exampleRoot.findLeaf(19));
        Assertions.assertEquals(TestFixtures.leaves[3], TestFixtures.exampleRoot.findLeaf(30));
        Assertions.assertEquals(TestFixtures.leaves[4], TestFixtures.exampleRoot.findLeaf(35));
        Assertions.assertEquals(TestFixtures.leaves[5], TestFixtures.exampleRoot.findLeaf(44));
        Assertions.assertEquals(TestFixtures.leaves[5], TestFixtures.exampleRoot.findLeaf(99));

        InnerNode node = new InnerNode(4,
            TestFixtures.leaves[0],
            TestFixtures.leaves[1],
            TestFixtures.leaves[2]
        );
        Assertions.assertEquals(node.getChildNode(0), node.findLeaf(Integer.MIN_VALUE));
        Assertions.assertEquals(node.getChildNode(2), node.findLeaf(Integer.MAX_VALUE));
    }

    @Test
    public void testGetOrNull() {
        Assertions.assertNull(TestFixtures.exampleRoot.getOrNull(0));
        Assertions.assertEquals(TestFixtures.entries.get(5).getValue(), TestFixtures.exampleRoot.getOrNull(5));
        Assertions.assertEquals(TestFixtures.entries.get(7).getValue(), TestFixtures.exampleRoot.getOrNull(7));
        Assertions.assertEquals(TestFixtures.entries.get(13).getValue(), TestFixtures.exampleRoot.getOrNull(13));
        Assertions.assertEquals(TestFixtures.entries.get(19).getValue(), TestFixtures.exampleRoot.getOrNull(19));
        Assertions.assertNull(TestFixtures.exampleRoot.getOrNull(30));
        Assertions.assertNull(TestFixtures.exampleRoot.getOrNull(35));
        Assertions.assertNull(TestFixtures.exampleRoot.getOrNull(44));
        Assertions.assertNull(TestFixtures.exampleRoot.getOrNull(99));
    }
}
