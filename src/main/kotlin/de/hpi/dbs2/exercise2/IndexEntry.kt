package de.hpi.dbs2.exercise2

open class IndexEntry<K : Comparable<K>, V : Any>(
    val key: K,
    val value: V
) {
    override fun toString(): String = "[$key]->$value"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is IndexEntry<*, *>) return false

        return key == other.key && value == other.value
    }

    override fun hashCode(): Int {
        var result = key.hashCode()
        result = 31 * result + value.hashCode()
        return result
    }
}
