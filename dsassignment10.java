// Name: Chidera Anamege
// Class: CS 3305/W01
// Term: Spring 2024
// Instructor: Carla McManus
// Assignment: 10-Part-1-Hashing


import java.util.*;
// Declare an interface representing a custom Map structure.
interface MyMap<K, V> {
    // Remove all mappings from this map.
    public void clear();

    // Check if this map contains a mapping for the specified key.
    public boolean containsKey(K key);

    // Check if this map contains no key-value
    public boolean isEmpty();

    // Retrieve the value associated with the specified key in this map.
    public V get(K key);

    // Associate the specified value with the specified key in this map and if the key already exists, updates
    // the value and returns the old value.
    public V put(K key, V value);

    // Remove the mapping for the specified key from this map if present.
    public V remove(K key);

    // Return the number of key-value mappings in this map.
    public int size();
}

// Concrete implementation of the MyMap interface using open addressing with linear probing.
class dsassignment10<K, V> implements MyMap<K, V> {
    // Define initial capacity and load factor threshold.
    private static final int INITIAL_CAPACITY = 4;
    private static final double LOAD_FACTOR_THRESHOLD = 0.5;

    // Arrays to store keys and values.
    private K[] keys;
    private V[] values;
    private int size;

    // Constructor to initialize arrays with initial capacity.
    public dsassignment10() {
        keys = (K[]) new Object[INITIAL_CAPACITY];
        values = (V[]) new Object[INITIAL_CAPACITY];
        size = 0;
    }

    // Remove all mappings from this map.
    @Override
    public void clear() {
        Arrays.fill(keys, null);
        Arrays.fill(values, null);
        size = 0;
    }

    // Check if this map contains a mapping for the specified key.
    @Override
    public boolean containsKey(K key) {
        return get(key) != null;
    }

    // Check if this map contains no key-value mappings.
    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    // Retrieve the value associated with the specified key in this map.
    @Override
    public V get(K key) {
        int index = findKeyIndex(key);
        return index != -1 ? values[index] : null;
    }

    // Associate the specified value with the specified key in this map.
    // If the key already exists, updates and returns the old value.
    @Override
    public V put(K key, V value) {
        if ((double) size / keys.length >= LOAD_FACTOR_THRESHOLD) {
            resize();
        }

        int index = findKeyIndex(key);
        if (index != -1) {
            // Key exists, update value.
            V oldValue = values[index];
            values[index] = value;
            return oldValue;
        } else {
            // Key does not exist, insert new.
            int i = hash(key);
            while (keys[i] != null) {
                i = (i + 1) % keys.length;
            }
            keys[i] = key;
            values[i] = value;
            size++;
            return null;
        }
    }

    // Remove the mapping for the specified key from this map if present.
    @Override
    public V remove(K key) {
        int index = findKeyIndex(key);
        if (index != -1) {
            // Key exists, remove entry.
            V removedValue = values[index];
            keys[index] = null;
            values[index] = null;
            size--;
            // Rehash remaining keys after removal.
            rehash();
            return removedValue;
        } else {
            return null;
        }
    }

    // Return the number of key-value mappings in this map.
    @Override
    public int size() {
        return size;
    }

    // Private helper method to find the index of a key using linear probing.
    private int findKeyIndex(K key) {
        int index = hash(key);
        int initialIndex = index;

        // Linear probing with wrapping around.
        do {
            if (keys[index] != null && keys[index].equals(key)) {
                return index; // Key found.
            }
            index = (index + 1) % keys.length; // Move to next index, wrapping around.
        } while (index != initialIndex);

        return -1;
    }

    // Private helper method to calculate hash.
    private int hash(K key) {
        return Math.abs(key.hashCode()) % keys.length;
    }

    // Private helper method to resize the arrays.
    private void resize() {
        int newSize = keys.length * 2;
        K[] newKeys = (K[]) new Object[newSize];
        V[] newValues = (V[]) new Object[newSize];
        // Rehash all existing keys into the new array.
        for (int i = 0; i < keys.length; i++) {
            if (keys[i] != null) {
                int newIndex = hash(keys[i]);
                while (newKeys[newIndex] != null) {
                    // Linear probing in the new array.
                    newIndex = (newIndex + 1) % newSize;
                }
                newKeys[newIndex] = keys[i];
                newValues[newIndex] = values[i];
            }
        }
        keys = newKeys;
        values = newValues;
    }

    // Private helper method to rehash remaining keys after removal.
    private void rehash() {
        K[] oldKeys = keys;
        V[] oldValues = values;
        keys = (K[]) new Object[keys.length];
        values = (V[]) new Object[values.length];
        size = 0;
        // Reinsert remaining keys into the new array.
        for (int i = 0; i < oldKeys.length; i++) {
            if (oldKeys[i] != null) {
                put(oldKeys[i], oldValues[i]);
            }
        }
    }
}

// Main class to test the MyHashMap implementation.
class Main {
    public static void main(String[] args) {
        // Test the MyHashMap implementation.
        MyMap<String, String> map = new dsassignment10<>();
        map.put("Walmart", "Supercenter");
        map.put("Target", "Retail Store");
        map.put("Dollar Tree", "Discount Store");

        System.out.println("Size of map: " + map.size());
        System.out.println("Type of store at 'Target': " + map.get("Target"));

        map.remove("Target");
        System.out.println("Size of map after removing 'Target': " + map.size());
        System.out.println("Type of store at 'Target' after removal: " + map.get("Target"));
    }
}
