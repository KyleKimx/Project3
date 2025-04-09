package com.example.project3.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * The List class represents a generic list.
 * It provides methods to add, remove, and access elements in the list.
 * The list can grow dynamically as elements are added.
 * Implements Iterable to allow iteration over the list elements.
 * @param <E> the type of elements in this list
 * @author Alison Chu, Byounguk Kim
 */
public class List<E> implements Iterable<E> {
    private static final int GROWTH = 4;
    private E[] objects;
    private int size;

    /**
     * Constructs an empty list with an initial capacity.
     */
    public List() { 
        this.objects = (E[]) new Object[GROWTH];
        this.size = 0;
    }

    /**
     * Finds the index of the specified element in the list.
     * @param e the element to find
     * @return the index of the element, or -1 if not found
     */
    private int find(E e) {
        for (int i = 0; i < size; i++) {
            if (objects[i].equals(e)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Grows the internal array to accommodate more elements.
     */
    private void grow() {
        E[] newArray = (E[]) new Object[objects.length + GROWTH];
        for (int i = 0; i < size; i++) {
            newArray[i] = objects[i];
        }
        objects = newArray; 
    }

    /**
     * Checks if the list contains the specified element.
     * @param e the element to check
     * @return true if the list contains the element, false otherwise
     */
    public boolean contains(E e) {
        return (find(e) != -1);
    }

    /**
     * Adds an element to the list.
     * @param e the element to add
     */
    public void add(E e) {
        if (size == objects.length) {
            grow();
        }
        objects[size++] = e;
    }

    /**
     * Removes the specified element from the list.
     * @param e the element to remove
     */
    public void remove(E e) {
        int idx = find(e);
        if (idx == -1) return;
        for (int i = idx; i < size - 1; i++) {
            objects[i] = objects[i + 1];
        }
        objects[--size] = null;
    }

    /**
     * Checks if the list is empty.
     * @return true if the list is empty, false otherwise
     */
    public boolean isEmpty() {

        return size == 0;
    }

    /**
     * Returns the number of elements in the list.
     * @return the number of elements in the list
     */
    public int size() {

        return size;
    }

    /**
     * Returns an iterator over the elements in the list.
     * @return an iterator over the elements in the list
     */
    @Override
    public Iterator<E> iterator() {

        return new ListIterator<E>();
    }


    /**
     * Returns the element at the specified position in the list.
     * @param index the index of the element to return
     * @return the element at the specified position
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    public E get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("List index out of range.");
        }
        return objects[index];
    }

    /**
     * Replaces the element at the specified position in the list with the specified element.
     * @param index the index of the element to replace
     * @param e the element to be stored at the specified position
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    public void set(int index, E e) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("List index out of range.");
        }
        objects[index] = e;
    }

    /**
     * Returns the index of the first occurrence of the specified element in the list, or -1 if the list does not contain the element.
     * @param e the element to search for
     * @return the index of the first occurrence of the specified element, or -1 if not found
     */
    public int indexOf(E e) {

        return find(e);
    }

    /**
     * The ListIterator class provides an iterator over the elements in the list.
     * @param <E> the type of elements in this iterator
     */
    private class ListIterator<E> implements Iterator<E> {
        int current = 0;

        /**
         * Returns true if the iteration has more elements.
         * @return true if the iteration has more elements, false otherwise
         */
        @Override
        public boolean hasNext(){

            return current < size;
        }

        /**
         * Returns the next element in the iteration.
         * @return the next element in the iteration
         * @throws NoSuchElementException if the iteration has no more elements
         */
        @Override
        public E next(){
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            return (E) objects[current++];
        }
    } 
}