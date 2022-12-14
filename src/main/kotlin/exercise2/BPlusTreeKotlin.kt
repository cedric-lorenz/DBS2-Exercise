package exercise2

import de.hpi.dbs2.ChosenImplementation
import de.hpi.dbs2.exercise2.*

import java.util.*

@ChosenImplementation(true)
class BPlusTreeKotlin : AbstractBPlusTree {
    constructor(order: Int) : super(order)
    constructor(rootNode: BPlusTreeNode<*>) : super(rootNode)

    private fun findPath(root: BPlusTreeNode<*>, key: Int): Stack<BPlusTreeNode<*>> {
        val stack = Stack<BPlusTreeNode<*>>()
        stack.push(root)
        var node = root
        while (node.height > 0) {
            node = (node as InnerNode).selectChild(key)
            stack.push(node)
        }
        return stack
    }

    private fun updateParentNodes(stack: Stack<BPlusTreeNode<*>>, rightLeaf: LeafNode) {
        if (stack.isEmpty()) {
            return
        }
        var parent = stack.pop() as InnerNode
        var currentNode = insertIntoInnerNode(parent, rightLeaf, stack)
        while (currentNode != null) {
            if (!stack.isEmpty()) {
                parent = stack.pop() as InnerNode
                currentNode = insertIntoInnerNode(parent, currentNode, stack)
            } else {
                currentNode = null
            }
        }
    }

    override fun insert(key: Int, value: ValueReference): ValueReference? {
        val stack = findPath(rootNode, key)
        val leafNode = stack.pop() as LeafNode
        var returnValue: ValueReference? = null

        // If key already exists, overwrite and return old ValueReference
        if (leafNode.getOrNull(key) != null) {
            returnValue = overwriteKeyInLeaf(leafNode, key, value)
        } else {
            // Returns a new leaf if target node had to be split, otherwise null
            val newLeaf = insertIntoLeafNode(leafNode, key, value)
            newLeaf?.let { updateParentNodes(stack, it) }
        }
        return returnValue
    }

    private fun overwriteKeyInLeaf(leafNode: LeafNode, key: Int, value: ValueReference?): ValueReference? {
        var result: ValueReference? = null
        // find existing key in node and return old ValueReference
        for (i in leafNode.keys.indices) {
            if (leafNode.keys[i] == key) {
                result = leafNode.references[i]
                leafNode.references[i] = value
                break
            }
        }
        return result
    }

    private fun insertIntoLeafNode(leaf: LeafNode, key: Int, value: ValueReference?): LeafNode? {
        if (leaf.isFull) { // if leaf is full, split keys evenly into two nodes and insert
            return splitLeafAndInsert(leaf, key, value)
        } else { // If leaf is not full, shift entries one index to the right to make place for new key and insert
            if (leaf.nodeSize == 0) {
                leaf.keys[0] = key
                leaf.references[0] = value
            } else {
                for (i in leaf.nodeSize - 1 downTo 0) {
                    val k = leaf.keys[i]
                    if (k < key) {
                        leaf.keys[i + 1] = key
                        leaf.references[i + 1] = value
                        break
                    }
                    if (k > key) {
                        leaf.keys[i + 1] = leaf.keys[i]
                        leaf.references[i + 1] = leaf.references[i]
                    }
                    if (i == 0) {
                        leaf.keys[i] = key
                        leaf.references[i] = value
                    }
                }
            }
        }
        return null
    }

    private fun splitLeafAndInsert(leafNode: LeafNode, key: Int, value: ValueReference?): LeafNode? {
        val rightStartIndex = getRightStartIndex(leafNode, key)
        val rightNodeLength = leafNode.keys.size - rightStartIndex
        val newEntries = arrayOfNulls<Entry>(rightNodeLength)
        // create entries for the new right leaf which we source from the second half of the original node's keys
        for (i in 0 until rightNodeLength) {
            val rightIndex: Int = rightStartIndex + i
            newEntries[i] = Entry(leafNode.keys[rightIndex], leafNode.references[rightIndex])
            //remove the chosen keys from the original node
            leafNode.keys[rightIndex] = null
            leafNode.references[rightIndex] = null
        }
        val rightLeaf = LeafNode(leafNode.order, *newEntries)

        // insert new key into the according of the two nodes
        if (key < rightLeaf.smallestKey) {
            insertIntoLeafNode(leafNode, key, value)
        } else {
            insertIntoLeafNode(rightLeaf, key, value)
        }

        // relink the siblings
        rightLeaf.nextSibling = leafNode.nextSibling
        leafNode.nextSibling = rightLeaf

        // replace InitialRootNode if needed
        if (rootNode is InitialRootNode) {
            val newLeaf = LeafNode(leafNode.order)
            for (i in rootNode.keys.indices) {
                newLeaf.keys[i] = rootNode.keys[i]
                newLeaf.references[i] = (rootNode as LeafNode).references[i]
            }
            newLeaf.nextSibling = rightLeaf
            rootNode = InnerNode(leafNode.order, newLeaf, rightLeaf)
            return null
        }
        return rightLeaf
    }

    private fun splitInnerAndInsert(
        innerNode: InnerNode,
        key: Int,
        childNode: BPlusTreeNode<*>,
        stack: Stack<BPlusTreeNode<*>>
    ): InnerNode {
        val rightStartIndex = getRightStartIndex(innerNode, key)
        val rightNodeLength = innerNode.keys.size - rightStartIndex + 1
        val rightChildren: Array<BPlusTreeNode<*>?> = arrayOfNulls(rightNodeLength)
        // create right children which we source from the second half of the original node's children
        for (i in 0 until rightNodeLength) {
            val rightIndex: Int = rightStartIndex + i
            rightChildren[i] = innerNode.references[rightIndex]
            if (rightIndex <= innerNode.keys.size - 1) {
                innerNode.keys[rightIndex] = null
            }
            innerNode.references[rightIndex] = null
        }
        val rightNode = InnerNode(innerNode.order, *rightChildren)

        // insert new key into the according of the two nodes
        if (key < rightNode.smallestKey) {
            insertIntoInnerNode(innerNode, childNode, stack)
        } else {
            insertIntoInnerNode(rightNode, childNode, stack)
        }
        var endIndex = 0
        while (innerNode.keys[endIndex] != null) {
            endIndex++
        }
        endIndex--
        rightNode.keys[rightNode.keys.size - 1] = innerNode.keys[endIndex]
        innerNode.keys[endIndex] = null
        if (stack.isEmpty()) { // if there are no more parents, aka if we are in the root node, create new one
            rightNode.keys[rightNode.keys.size - 1] = null
            rootNode = InnerNode(innerNode.order, innerNode, rightNode)
        }
        return rightNode
    }

    private fun insertIntoInnerNode(
        innerNode: InnerNode,
        childNode: BPlusTreeNode<*>,
        stack: Stack<BPlusTreeNode<*>>
    ): InnerNode? {
        var key = childNode.smallestKey
        if (childNode is InnerNode) {
            key = childNode.keys[childNode.keys.size - 1]
            childNode.keys[childNode.keys.size - 1] = null
        }
        return if (innerNode.isFull) { // if node is full, split keys evenly into two nodes and insert
            splitInnerAndInsert(innerNode, key, childNode, stack)
        } else { // If node is not full, shift entries one index to the right to make place for new key and insert
            var index = 0
            while (index < innerNode.keys.size && innerNode.keys[index] != null && innerNode.keys[index] < key) {
                index++
            }
            val referenceIndex = index + 1
            for (i in innerNode.keys.size - 1 downTo index + 1) {
                innerNode.keys[i] = innerNode.keys[i - 1]
                innerNode.references[i + 1] = innerNode.references[i - 1 + 1]
            }
            innerNode.keys[index] = key
            innerNode.references[referenceIndex] = childNode
            null
        }
    }

    /**
     * getRightStartIndex() is needed when a node is split into left and right
     * and calculates the index of the original node's keys from which the right
     * node's keys will start.
     */
    private fun getRightStartIndex(node: BPlusTreeNode<*>, key: Int): Int {
        var rightStartIndex = node.keys.size / 2
        var targetIndex = 0
        while (targetIndex < node.keys.size && node.keys[targetIndex] != null && node.keys[targetIndex] < key) {
            targetIndex++
        }
        if (targetIndex > rightStartIndex) {
            rightStartIndex++
        }
        return rightStartIndex
    }
}
