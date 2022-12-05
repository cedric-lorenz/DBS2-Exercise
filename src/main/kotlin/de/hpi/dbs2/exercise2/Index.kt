package de.hpi.dbs2.exercise2

/**
 * This interface represents a simple abstraction for a database index.
 * It acts somewhat similar to a SortedMap by mapping comparable keys (which define the ordering)
 * to a non-nullable (!) value.
 */
interface Index<K : Comparable<K>, V : Any> {
    /**
     * Find the value mapped to the given key or null, if the key is not in the index.
     * @return associated value for the given key, if existing
     */
    fun getOrNull(searchKey: K): V?

    /**
     * Find the value mapped to the given key.
     * @throws NoSuchElementException If the key is not in the index.
     * @return associated value for the given key
     */
    fun get(searchKey: K): V

    /**
     * Finds all values for the keys between the given bounds.
     * @return an iterator over all found values
     *         will be empty if lowerBound > upperBound
     *         will contain only a single value if lowerBound = upperBound and the key maps to a value
     */
    fun getRange(lowerBound: K, upperBound: K): Iterator<V>

    /**
     * Insert a new key-value mapping into the index, replacing the old value for existing keys.
     * @return previously associated value for the given key, if existing
     */
    fun insert(key: K, value: V): V?

    /**
     * Remove a key-value mapping from the index.
     * @return previously associated value for the given key, if existing
     */
    fun remove(key: K): V?
}
