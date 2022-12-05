package de.hpi.dbs2.exercise2;

import java.util.HashMap;
import java.util.Map;

public class TestFixtures {
    public final static Map<Integer, AbstractBPlusTree.Entry> entries = new HashMap<>();
    public final static AbstractBPlusTree.Entry getOrCreateEntry(int key) {
        if(entries.containsKey(key)) {
            return entries.get(key);
        } else {
            AbstractBPlusTree.Entry entry = new AbstractBPlusTree.Entry(
                key, new ValueReference(entries.size())
            );
            entries.put(key, entry);
            return entry;
        }
    }
    public final static LeafNode[] leaves = new LeafNode[] {
        new LeafNode(4, // leaves[0]
            getOrCreateEntry(2),
            getOrCreateEntry(3),
            getOrCreateEntry(5)
        ),
        new LeafNode(4, // leaves[1]
            getOrCreateEntry(7),
            getOrCreateEntry(11)
        ),
        new LeafNode(4, // leaves[2]
            getOrCreateEntry(13),
            getOrCreateEntry(17),
            getOrCreateEntry(19)
        ),
        new LeafNode(4, // leaves[3]
            getOrCreateEntry(23),
            getOrCreateEntry(29)
        ),
        new LeafNode(4, // leaves[4]
            getOrCreateEntry(31),
            getOrCreateEntry(37),
            getOrCreateEntry(41)
        ),
        new LeafNode(4, // leaves[5]
            getOrCreateEntry(43),
            getOrCreateEntry(47)
        )
    };
    public final static BPlusTreeNode<?> exampleRoot = new InnerNode(4,
        new InnerNode(4,
            leaves[0],
            leaves[1]
        ),
        new InnerNode(4,
            leaves[2],
            leaves[3],
            leaves[4],
            leaves[5]
        )
    );
    static {
        ((InnerNode) exampleRoot).fixLeafLinks();
    }
    public final static AbstractBPlusTree exampleTree = new ReadOnlyBPlusTree(exampleRoot);
}
