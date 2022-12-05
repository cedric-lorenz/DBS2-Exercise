package de.hpi.dbs2.exercise2

import de.hpi.dbs2.exercise2.AbstractBPlusTree.Entry

fun entryArrayOf(vararg entries: Pair<Int, ValueReference>): Array<Entry> = entries
    .map { Entry(it.first, it.second) }
    .toTypedArray()
fun ref(value: Int): ValueReference = ValueReference(value)
