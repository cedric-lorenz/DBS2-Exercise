package de.hpi.dbs2.exercise2;

import java.util.Arrays;
import java.util.Objects;

/**
 * A specialized LeafNode with different validation criteria (otherwise identical to a LeafNode).
 * Should only be used as a root node for trees which are small enough to fit in one node!
 */
public class InitialRootNode extends LeafNode {
    public InitialRootNode(int order, AbstractBPlusTree.Entry... entries) {
        super(order, entries);
    }

    @Override
    public boolean isValid() {
        if (keys.length != n)
            return false;
        int size = getNodeSize();
        if(Arrays.stream(keys).filter(Objects::nonNull).count() != size)
            return false;
        for(int i = 0; i < n; i++) {
            if(keys[i] == null) break;
            if(references[i] == null)
                return false;
        }
        if(nextSibling != null)
            return false;
        return true;
    }
}
