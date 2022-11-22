package exercise1

import de.hpi.dbs2.ChosenImplementation
import de.hpi.dbs2.dbms.BlockManager
import de.hpi.dbs2.dbms.utils.BlockSorter
import de.hpi.dbs2.dbms.BlockOutput
import de.hpi.dbs2.dbms.Relation
import de.hpi.dbs2.dbms.Block
import de.hpi.dbs2.exercise1.SortOperation
import java.util.*

@ChosenImplementation(true)
class TPMMSKotlin(
    manager: BlockManager,
    sortColumnIndex: Int
) : SortOperation(manager, sortColumnIndex) {
    override fun estimatedIOCost(relation: Relation): Int {
        /*
        Let n be the amount of blocks in the relation.
        Then, in phase I we read the blocks into memory and write them to disk once each.
        Therefore, we have 2 * n I/O operations.
        Finally, in phase II, we load fragments of the relation into memory,
        such that every block of the memory will have been read and loaded into memory once in order to incooperate it into the output.
        The output does not necessarily need to be written to disk. If it is not, we hence need only 1 * n I/O operations.

        Overall, we therefore receive an estimated amount of 3 * n I/O operations.
         */

        return 3 * relation.estimatedSize
    }

    override fun sort(relation: Relation, output: BlockOutput) {
        if (relation.estimatedSize > blockManager.freeBlocks * blockManager.freeBlocks) {
            throw RelationSizeExceedsCapacityException()
        }
        val relationIterator = relation.iterator()
        val sublists = mutableListOf<MutableList<Block>>()
        val comparator = relation.columns.getColumnComparator(sortColumnIndex)

        //  SORT ALL SUBLISTS
        while (relationIterator.hasNext()) {
            sublists.add(mutableListOf())
            while (blockManager.freeBlocks > 0) {
                val blockInMemory = blockManager.load(relationIterator.next())
                sublists.last().add(blockInMemory)
            }

            BlockSorter.sort(relation, sublists.last(), comparator)

            for (i in sublists.last().indices) {
                blockManager.release(sublists.last()[i], true)!!
            }
        }

        // MERGE ALL SUBLISTS

        val outputBlock = blockManager.allocate(true)

        // create index structures to memorize which blocks and tuples have already been processed
        val sublistAmount = sublists.size
        val currentBlocks: MutableList<Int> = Collections.nCopies(sublistAmount, 0).toMutableList()
        val currentTuples: MutableList<Int> = Collections.nCopies(sublistAmount, 0).toMutableList()

        // load first blocks into memory
        for (list in sublists) {
            blockManager.load(list[0])
        }
        var finishFlag = false
        while (!finishFlag) {
            finishFlag = true
            var min = 0
            for (currentList in sublists.indices) {
                //if current list is fully read, continue
                if (currentBlocks[currentList] >= sublists[currentList].size) {
                    if (min == currentList)
                        min++
                    continue
                }
                //check whether the current list has a smaller item than the current minList
                val currentListTuple = sublists[currentList][currentBlocks[currentList]][currentTuples[currentList]]
                val minListTuple = sublists[min][currentBlocks[min]][currentTuples[min]]
                if (comparator.compare(currentListTuple, minListTuple) < 0)
                    min = currentList
                finishFlag = false
            }

            if (finishFlag)
                break

            // write minimal item to output block and flush block if full
            outputBlock.append(sublists[min][currentBlocks[min]][currentTuples[min]])
            if (outputBlock.isFull())
                output.output(outputBlock)
            currentTuples[min] = currentTuples[min] + 1

            // if block of minimal list has been fully read, load new block if present
            if (currentTuples[min] == sublists[min][currentBlocks[min]].size) {
                blockManager.release(sublists[min][currentBlocks[min]], false)
                currentTuples[min] = 0
                currentBlocks[min]++
                if (currentBlocks[min] < sublists[min].size) {
                    blockManager.load(sublists[min][currentBlocks[min]])
                }
            }

        }
        // flush output block one final time
        if (!outputBlock.isEmpty())
            output.output(outputBlock)
        blockManager.release(outputBlock, false)
    }
}
