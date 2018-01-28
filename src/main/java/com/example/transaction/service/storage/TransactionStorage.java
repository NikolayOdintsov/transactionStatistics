package com.example.transaction.service.storage;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Class provides wrapper for LinkedHashMap collection for concurrent access.
 * Class provides constant time complexity O(1) on behalf of LinkedHashMap implementation.
 * Class provides constant space complexity O(1) by maintaining fixed number of entries
 * and dropping off the oldest entries in case a new one needs to be added.
 * It is implemented by overriding The removeEldestEntry method.
 *
 * @see Map
 * @see LinkedHashMap
 * @see Collections#synchronizedMap
 * <p/>
 * Created by nikolay.odintsov on 28.01.18.
 */
public class TransactionStorage<K, V> {
    private final Map<K, V> dataStorage;

    public TransactionStorage(final int maxEntries) {
        this.dataStorage = Collections.synchronizedMap(new LinkedHashMap<K, V>(maxEntries, 0.75f, true) {

            @Override
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                return size() > maxEntries;
            }
        });
    }

    public V put(K key, V value) {
        return this.dataStorage.put(key, value);
    }

    public V get(K key) {
        return this.dataStorage.get(key);
    }

    public Map<K, V> getAll() {
        return this.dataStorage;
    }
}
