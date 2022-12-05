package de.hpi.dbs2.exercise2;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

public sealed abstract class BPlusTreeNode<V> permits InnerNode, LeafNode {
    /**
     * Allowed number of direct child nodes, also known as branching factor.
     */
    public final int order;
    /**
     * Number of keys per node = order - 1
     */
    public final int n;

    /**
     * Array of keys (length == n). Can contain nulls if the node is not full.
     */
    public final Integer[] keys;

    /**
     * References to either sub-nodes (length == order) for InnerNodes
     * or ValueReferences (length == order-1) for LeafNodes.
     * Can contain nulls if the node is not full.
     * <p>
     * <pIn LeafNodes, the last reference would typically point to the
     * right-most sibling LeafNode, but we save this reference in LeafNode::nextSibling.
     */
    public V[] references;

    public BPlusTreeNode(int order) {
        this.order = order;
        this.n = order - 1;
        Preconditions.checkArgument(order > 1, "order must be larger than 1");
        this.keys = new Integer[n];
    }

    /**
     * @return the number of references in this node
     */
    public int getNodeSize() {
        return (int) Arrays.stream(references)
            .takeWhile(Objects::nonNull)
            .count();
    }

    /**
     * @return returns the height of this node in comparison to it's left-most leaf node
     */
    public abstract int getHeight();

    /**
     * helper method for checking the node's fill status in a delete operation
     */
    public boolean isEmpty() {
        return references[0] == null;
    }

    /**
     * helper method for checking the node's fill status in an insert operation
     */
    public boolean isFull() {
        return getNodeSize() == references.length;
    }

    /**
     * Finds the smallest key in the leftmost LeafNode of this subtree.
     *
     * @return the smallest key in this subtree
     */
    @NotNull
    public abstract Integer getSmallestKey();

    /**
     * Finds the LeafNode in which the given key could be located.
     */
    @NotNull
    public abstract LeafNode findLeaf(@NotNull Integer searchKey);

    @Nullable
    public abstract ValueReference getOrNull(@NotNull Integer searchKey);

    public abstract boolean isValid();

    /**
     * @return A stream of all nodes in this subtree depth-first
     * starting with the left-most LeafNode and ending with this node.
     */
    public abstract Stream<BPlusTreeNode<?>> getDepthFirstNodeStream();

    /**
     * @return A stream of all entries (key-value mappings) in this subtree.
     */
    public abstract Stream<AbstractBPlusTree.Entry> getEntries();

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        stringifyTree(builder, 0, false);
        return builder.toString();
    }

    protected abstract void stringifyTree(StringBuilder builder, int depth, boolean hideEmptyReferences);

    /**
     * Utility builder method for creating a tree structure from an array (of arrays)+ of entries.
     *
     * <p><pre>{@code
     * buildTree(4,
     *     new Entry[]{
     *         new Entry(2, new ValueReference(0)),
     *         new Entry(3, new ValueReference(1)),
     *         new Entry(5, new ValueReference(2)),
     *     },
     *     new Entry[]{
     *         new Entry(7, new ValueReference(3)),
     *         new Entry(11, new ValueReference(4))
     *     }
     * );
     * }
     * @return the root of the build tree structure
     */
    public static BPlusTreeNode<?> buildTree(int order, Object[]... root) {
        return buildTreeNodes(order, root);
    }
    public static BPlusTreeNode<?> buildTree(int order, Object[] root) {
        return buildTreeNodes(order, root);
    }

    protected static BPlusTreeNode<?> buildTreeNodes(int order, Object[] entries) {
        ArrayList<BPlusTreeNode<?>> subNodes = new ArrayList<>();
        ArrayList<AbstractBPlusTree.Entry> referencedValues = new ArrayList<>();
        for (Object any : entries) {
            if (any instanceof Object[]) {
                subNodes.add(buildTreeNodes(order, (Object[]) any));
            } else if (any instanceof AbstractBPlusTree.Entry) {
                referencedValues.add((AbstractBPlusTree.Entry) any);
            } else {
                throw new IllegalArgumentException(
                    "Only provide entries or arrays of entries (subtrees).");
            }
        }
        if (!referencedValues.isEmpty() && !subNodes.isEmpty()) {
            throw new IllegalArgumentException("Don't mix entries and subtrees in a node.");
        } else if (referencedValues.isEmpty() && subNodes.isEmpty()) {
            throw new IllegalArgumentException("Empty node");
        } else if (!subNodes.isEmpty()) {
            InnerNode subTree = new InnerNode(order, subNodes.toArray(new BPlusTreeNode[0]));
            subTree.fixLeafLinks();
            return subTree;
        } else {
            return new LeafNode(order, referencedValues.toArray(new AbstractBPlusTree.Entry[0]));
        }
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof BPlusTreeNode<?> that)) return false;
        return order == that.order
            && Arrays.equals(keys, that.keys)
            && Arrays.equals(references, that.references);
    }
}
