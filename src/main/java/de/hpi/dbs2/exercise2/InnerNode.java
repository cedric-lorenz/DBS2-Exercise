package de.hpi.dbs2.exercise2;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Streams;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public non-sealed class InnerNode extends BPlusTreeNode<BPlusTreeNode<?>> {
    public InnerNode(int order, BPlusTreeNode<?>... nodes) {
        super(order);
        references = new BPlusTreeNode[order];
        // we can't insert more nodes than the given order
        Preconditions.checkArgument(nodes.length <= order);
        for (int i = 0; i < nodes.length; i++) {
            references[i] = nodes[i];
            if (i > 0) {
                keys[i - 1] = nodes[i].getSmallestKey();
            }
        }
    }

    /**
     * Fixes leaf references after manually creating InnerNodes.
     * You are _NOT_ allowed to use this method in the exercise.
     */
    public void fixLeafLinks() {
        List<LeafNode> leafs = getDepthFirstNodeStream()
            .filter(node -> node instanceof LeafNode)
            .map(leafNode -> (LeafNode) leafNode)
            .toList();
        Streams.forEachPair(
            leafs.stream(),
            leafs.stream().skip(1),
            (first, second) -> {
                first.nextSibling = second;
            }
        );
        leafs.get(leafs.size()-1).nextSibling = null;
    }

    @Override
    public int getHeight() {
        // this reference requires to exist, because otherwise this InnerNode would be empty
        return references[0].getHeight() + 1;
    }

    @NotNull
    @Override
    public Integer getSmallestKey() {
        // this reference requires to exist, because otherwise this InnerNode would be empty
        BPlusTreeNode<?> leftNode = references[0];
        return leftNode.getSmallestKey();
    }

    /**
     * Returns the child-node in which the given searchKey could be located.
     */
    @NotNull
    public BPlusTreeNode<?> selectChild(@NotNull Integer searchKey) {
        for (int i = 0; i < keys.length; i++) {
            Integer key = keys[i];
            if (key != null) {
                if (searchKey < key) {
                    return references[i]; // "left" reference
                }
            } else {
                return references[i]; // "right" reference
            }
        }
        return references[order - 1]; // "rightest" reference
    }

    @NotNull
    public LeafNode findLeaf(@NotNull Integer searchKey) {
        return selectChild(searchKey).findLeaf(searchKey);
    }

    /**
     * @return the n-th child node of this node
     */
    public BPlusTreeNode<?> getChildNode(int i) {
        return references[i];
    }

    @Nullable
    @Override
    public ValueReference getOrNull(@NotNull Integer searchKey) {
        return findLeaf(searchKey).getOrNull(searchKey);
    }

    @Override
    public boolean isValid() {
        if (keys.length != n)
            return false;
        int size = getNodeSize();
        if (isEmpty() || size < (int) Math.ceil(order / 2.0))
            return false;
        if (references.length != order)
            return false;
        if (Arrays.stream(keys).filter(Objects::nonNull).count() != getNodeSize() - 1)
            return false;
        int height = getHeight();
        for (int i = 0; i < n; i++) {
            if (keys[i] == null) break;
            if (references[i] == null || references[i + 1] == null)
                return false;
            if(height == 1) {
                if(references[i] instanceof LeafNode leafNode) {
                    if(leafNode.nextSibling != references[i + 1])
                        return false;
                } else {
                    return false;
                }
            }
            if (!references[i + 1].getSmallestKey().equals(keys[i]))
                return false;
        }
        if (!Arrays.stream(references).filter(Objects::nonNull)
            .allMatch(node ->
                node.getHeight() == height - 1
                    && node.order == order
                    && node.isValid()
            ))
            return false;
        return true;
    }

    @Override
    public void stringifyTree(StringBuilder builder, int depth, boolean hideEmptyReferences) {
        String indentation = Strings.repeat("\t", depth);
        String keysString = Arrays.stream(keys)
            .map((Integer key) -> (key == null) ? "-" : key.toString())
            .collect(Collectors.joining(",", "[", "]"));
        builder.append(indentation).append("Node").append(keysString).append(" {\n");
        for (int i = 0; i < order; i++) {
            BPlusTreeNode<?> reference = references[i];
            if (reference == null) {
                if (hideEmptyReferences) continue;
                builder.append(indentation).append("\t").append("-");
            } else {
                reference.stringifyTree(builder, depth + 1, hideEmptyReferences);
            }
            if (i < references.length - 1) {
                builder.append(",\n");
            } else {
                builder.append("\n");
            }
        }
        builder.append(indentation).append("}");
    }

    @Override
    public Stream<BPlusTreeNode<?>> getDepthFirstNodeStream() {
        return Stream.concat(
            Arrays.stream(references)
                .takeWhile(Objects::nonNull)
                .flatMap(BPlusTreeNode::getDepthFirstNodeStream),
            Stream.of(this)
        );
    }

    @Override
    public Stream<AbstractBPlusTree.Entry> getEntries() {
        return Arrays.stream(references)
            .takeWhile(Objects::nonNull)
            .flatMap(BPlusTreeNode::getEntries);
    }
}
