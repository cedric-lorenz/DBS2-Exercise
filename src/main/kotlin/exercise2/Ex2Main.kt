package exercise2

import de.hpi.dbs2.exercise2.AbstractBPlusTree
import de.hpi.dbs2.exercise2.BPlusTreeNode
import de.hpi.dbs2.exercise2.*

fun main() {
    val order = 4

    val root = BPlusTreeNode.buildTree(order,
        arrayOf(
            entryArrayOf(
                2 to ref(0),
                3 to ref(1),
                5 to ref(2)
            ), entryArrayOf(
                7 to ref(3),
                11 to ref(4)
            )
        ), arrayOf(
            entryArrayOf(
                13 to ref(5),
                17 to ref(6),
                19 to ref(7),
            ), entryArrayOf(
                23 to ref(8),
                29 to ref(9)
            ), entryArrayOf(
                31 to ref(10),
                37 to ref(11),
                41 to ref(12)
            ), entryArrayOf(
                43 to ref(13),
                47 to ref(14)
            )
        )
    )
    println(root)

    val tree: AbstractBPlusTree = BPlusTreeKotlin(root)
    println(tree)

    val leafNode = LeafNode(4)
    println(leafNode)
    val innerNode = InnerNode(4)
    println(innerNode)

    /*
     * playground
     * ~ feel free to experiment with the tree and tree nodes here
     */
}
