package com.soumakis.collection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * A simple immutable array-based sequence leveraging an underlying array. Since it is immutable it
 * is inherently thread-safe.
 * <p>
 * It does not use structural sharing, so every operation that modifies the sequence creates a new
 * array with the updated elements. This might not be the most efficient way to implement an
 * ArrayList-like data structure, but it uses native array copying which is very fast and uses cache
 * locality effectively.
 * </p>
 *
 * @param <T>
 */
public final class ArraySeq<T> {

  private final T[] elements;
  private final int length;

  private ArraySeq(T[] elements) {
    this.elements = elements;
    length = elements.length;
  }

  /**
   * Creates a new sequence with the given elements.
   *
   * @param elements the elements to add
   * @param <T>      the type of the elements
   * @return a new sequence with the elements added
   */
  @SafeVarargs
  public static <T> ArraySeq<T> of(T... elements) {
    return new ArraySeq<>(elements);
  }

  /**
   * Creates an empty sequence.
   *
   * @param <T> the type of the elements
   * @return an empty sequence
   */
  @SuppressWarnings("unchecked")
  public static <T> ArraySeq<T> empty() {
    return new ArraySeq<>((T[]) new Object[0]);
  }

  /**
   * Returns the length of the sequence.
   *
   * @return the length of the sequence
   */
  public int length() {
    return length;
  }

  /**
   * Appends the given elements to the sequence. Since it is an immutable data structure, it returns
   * a new sequence with the elements appended.
   *
   * @param elementsToAppend the elements to append
   * @return a new sequence with the elements appended
   */
  @SuppressWarnings("unchecked")
  public ArraySeq<T> append(T... elementsToAppend) {
    var newElements = (T[]) new Object[length + elementsToAppend.length];
    System.arraycopy(this.elements, 0, newElements, 0, length);
    System.arraycopy(elementsToAppend, 0, newElements, length, elementsToAppend.length);
    return new ArraySeq<>(newElements);
  }

  /**
   * Appends the given sequence to the sequence. Since it is an immutable data structure, it returns
   * a new sequence with the elements appended.
   *
   * @param other the sequence to append
   * @return a new sequence with the elements appended
   */
  public ArraySeq<T> append(ArraySeq<T> other) {
    return append(other.elements);
  }

  /**
   * Prepends the given elements to the sequence. Since it is an immutable data structure, it
   * returns a new sequence with the elements prepended.
   *
   * @param elementsToPrepend the elements to prepend
   * @return a new sequence with the elements prepended
   */
  @SuppressWarnings("unchecked")
  public ArraySeq<T> prepend(T... elementsToPrepend) {
    var newElements = (T[]) new Object[length + elementsToPrepend.length];
    System.arraycopy(elementsToPrepend, 0, newElements, 0, elementsToPrepend.length);
    System.arraycopy(this.elements, 0, newElements, elementsToPrepend.length, length);
    return new ArraySeq<>(newElements);
  }

  /**
   * Updates the element at the given index with the updated value. Since it is an immutable data
   * structure, it returns a new sequence with the element updated.
   *
   * @param index        the index of the element to update
   * @param updatedValue the updated value
   * @return a new sequence with the element updated
   * @throws IndexOutOfBoundsException if the index is out of bounds
   */
  @SuppressWarnings("unchecked")
  public ArraySeq<T> update(int index, T updatedValue) {
    checkBounds(index);
    var newElements = (T[]) new Object[length];
    System.arraycopy(elements, 0, newElements, 0, length);
    newElements[index] = updatedValue;
    return new ArraySeq<>(newElements);
  }

  /**
   * Updates the element at the given index with the evaluation of the computation represented as an
   * {@link  Supplier}. Since it is an immutable data structure, it returns a new sequence with the
   * element updated.
   *
   * @param index                      the index of the element to update
   * @param computationForUpdatedValue the computation to evaluate for the updated value
   * @return a new sequence with the element updated
   * @throws IndexOutOfBoundsException if the index is out of bounds
   */
  @SuppressWarnings("unchecked")
  public ArraySeq<T> update(int index, Supplier<T> computationForUpdatedValue) {
    checkBounds(index);
    var newElements = (T[]) new Object[length];
    System.arraycopy(elements, 0, newElements, 0, length);
    newElements[index] = computationForUpdatedValue.get();
    return new ArraySeq<>(newElements);
  }

  /**
   * Returns the element at the given index.
   *
   * @param index the index of the element to get
   * @return the element at the given index
   * @throws IndexOutOfBoundsException if the index is out of bounds
   */
  public T get(int index) {
    checkBounds(index);
    return elements[index];
  }

  /**
   * Deletes the element at the given index. Since it is an immutable data structure, it returns a
   * new sequence with the element deleted.
   *
   * @param index the index of the element to delete
   * @return a new sequence with the element deleted
   * @throws IndexOutOfBoundsException if the index is out of bounds
   */
  @SuppressWarnings("unchecked")
  public ArraySeq<T> delete(int index) {
    checkBounds(index);
    var newElements = (T[]) new Object[length - 1];
    System.arraycopy(elements, 0, newElements, 0, index);
    System.arraycopy(elements, index + 1, newElements, index, length - index - 1);
    return new ArraySeq<>(newElements);
  }

  /**
   * Maps the elements of the sequence to a new sequence using the given function. The complexity of
   * this method is O(n) where n is the number of elements.
   *
   * @param fn  the function to map the elements
   * @param <U> the type of the elements of the new sequence
   * @return a new sequence with the elements mapped
   */
  @SuppressWarnings("unchecked")
  public <U> ArraySeq<U> map(Function<? super T, ? extends U> fn) {
    var newElements = (U[]) new Object[length];
    for (int i = 0; i < length; i++) {
      newElements[i] = fn.apply(elements[i]);
    }
    return new ArraySeq<>(newElements);
  }

  /**
   * Applies a flat map function to the elements of the sequence. The complexity of this method is
   * O(n) where n is the number of elements.
   *
   * @param fn  the flat map function
   * @param <U> the type of the elements of the new sequence
   * @return a new sequence with the elements flat mapped
   */
  public <U> ArraySeq<U> flatMap(Function<? super T, ? extends ArraySeq<U>> fn) {
    List<U> temp = new ArrayList<>();

    for (int i = 0; i < length; i++) {
      ArraySeq<U> mapped = fn.apply(elements[i]);
      for (int j = 0; j < mapped.length; j++) {
        temp.add(mapped.get(j));
      }
    }

    @SuppressWarnings("unchecked")
    U[] finalArray = temp.toArray((U[]) new Object[0]);

    return new ArraySeq<>(finalArray);
  }

  /**
   * Filters the elements of the sequence using the given predicate. The complexity of this method
   * is O(n) where n is the number of elements.
   *
   * @param predicate the predicate to filter the elements
   * @return a new sequence with the elements filtered
   */
  public ArraySeq<T> filter(Predicate<T> predicate) {
    List<T> temp = new ArrayList<>();

    for (int i = 0; i < length; i++) {
      var element = elements[i];
      if (predicate.test(element)) {
        temp.add(element);
      }
    }

    @SuppressWarnings("unchecked")
    T[] finalArray = temp.toArray((T[]) new Object[0]);

    return new ArraySeq<>(finalArray);
  }

  /**
   * Converts the sequence to a {@link List}.
   * <p>
   * <b>NOTE: This method might break the immutability if the underlying elements of the generated
   * ArrayList are mutable</b>
   *
   * @return a {@link List} with the elements of the sequence
   */
  public List<T> toJavaArrayList() {
    return Arrays.asList(Arrays.copyOf(elements, length));
  }

  /**
   * Converts the sequence to a {@link Stream}.
   *
   * <p>
   * <b>NOTE: This method might break the immutability if the underlying elements of the generated
   * Stream are mutable</b>
   * </p>
   *
   * @return a {@link Stream} with the elements of the sequence
   */
  public Stream<T> stream() {
    return Arrays.stream(elements);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ArraySeq<?> arraySeq = (ArraySeq<?>) o;
    return length == arraySeq.length && Objects.deepEquals(elements, arraySeq.elements);
  }

  @Override
  public int hashCode() {
    return Objects.hash(Arrays.hashCode(elements), length);
  }

  private void checkBounds(int index) {
    if (index < 0 || index >= length) {
      throw new IndexOutOfBoundsException("Index out of bounds: " + index);
    }
  }

}
