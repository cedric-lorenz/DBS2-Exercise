package de.hpi.dbs2.exercise2;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ReadOnlyBPlusTree extends AbstractBPlusTree {
    public ReadOnlyBPlusTree(int order) {
        super(order);
    }

    public ReadOnlyBPlusTree(BPlusTreeNode<?> rootNode) {
        super(rootNode);
    }

    @Nullable
    @Override
    public ValueReference insert(@NotNull Integer key, @NotNull ValueReference value) {
        throw new UnsupportedOperationException("This tree is read-only.");
    }
}
