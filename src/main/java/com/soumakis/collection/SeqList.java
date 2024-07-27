package com.soumakis.collection;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * A simple immutable linked list implementation with partial structural sharing.
 *
 * @param <T> the type of the elements
 */
public sealed interface SeqList<T> permits Empty, Cons {

  /**
   * Creates an empty list.
   *
   * @param <T> the type of the elements
   * @return an empty list
   */
  static <T> SeqList<T> empty() {
    return new Empty<>();
  }

  /**
   * Creates a new list with the given elements. The complexity of this method is O(n) where n is
   * the number of elements.
   *
   * @param elements the elements to add
   * @param <T>      the type of the elements
   * @return a new list with the elements added
   */
  @SafeVarargs
  static <T> SeqList<T> of(T... elements) {
    SeqList<T> list = empty();
    for (int i = elements.length - 1; i >= 0; i--) {
      list = list.add(elements[i]);
    }
    return list;
  }

  /**
   * Concatenates two lists. The complexity of this method is O(n) where n is the number of
   * elements.
   *
   * @param list1 the first list
   * @param list2 the second list
   * @param <T>   the type of the elements
   * @return a new list with the elements of the two lists concatenated
   * @throws StackOverflowError for infinite lists
   */
  static <T> SeqList<T> concat(SeqList<T> list1, SeqList<T> list2) {
    if (list1.isEmpty()) {
      return list2;
    }
    return new Cons<>(list1.head(), concat(list1.tail(), list2));
  }

  /**
   * Prepends the element to the list. The complexity of this method is O(1).
   *
   * @param element the element to add
   * @return a new list with the element prepended
   */
  default SeqList<T> add(T element) {
    return new Cons<>(element, this);
  }

  /**
   * Adds the element at the specified index. If the index is negative, the list is returned as is.
   * The complexity of this method is O(n) where n is the index. It uses structural sharing up to
   * until the index to add the element. If the index is at the end of the list, the structural
   * sharing is not utilized.
   *
   * @param index   the index to add the element
   * @param element the element to add
   * @return a new list with the element added at the specified index
   * @throws StackOverflowError for infinite lists
   */
  default SeqList<T> add(int index, T element) {
    if (index < 0) {
      return this;
    }
    if (index == 0) {
      return add(element);
    }
    return new Cons<>(head(), tail().add(index - 1, element));
  }

  /**
   * Removes the first occurrence of the element from the list. If the element is not found, the
   * list is returned as is. The complexity of this method is O(n) where n is the number of
   * elements. It uses structural sharing up to the element to remove. If the element is not found
   * the structural sharing is not utilized.
   *
   * @param element the element to remove
   * @return a new list with the element removed
   * @throws StackOverflowError for infinite lists
   */
  default SeqList<T> remove(T element) {
    if (isEmpty()) {
      return this;
    }
    if (head().equals(element)) {
      return tail();
    }
    return new Cons<>(head(), tail().remove(element));
  }

  /**
   * Applies a map function to the elements of the list. The complexity of this method is O(n) where
   * n is the number of elements.
   * <b>It does not use structural sharing</b> as it requires advanced data structures to achieve
   * it.
   *
   * @param fn  the map function
   * @param <U> the type of the elements of the new list
   * @return a new list with the elements mapped
   * @throws StackOverflowError for infinite lists
   */
  default <U> SeqList<U> map(Function<? super T, ? extends U> fn) {
    if (isEmpty()) {
      return empty();
    }
    return new Cons<>(fn.apply(head()), tail().map(fn));
  }

  /**
   * Applies a flat map function to the elements of the list. The complexity of this method is O(n)
   * where n is the number of elements.
   * <b>It does not use structural sharing</b> as it requires advanced data structures to achieve
   * it.
   *
   * @param fn  the flat map function
   * @param <U> the type of the elements of the new list
   * @return a new list with the elements flat mapped
   * @throws StackOverflowError for infinite lists
   */
  default <U> SeqList<U> flatMap(Function<? super T, ? extends SeqList<U>> fn) {
    if (isEmpty()) {
      return empty();
    }
    SeqList<U> mappedHead = fn.apply(head());
    SeqList<U> newTail = tail().flatMap(fn);
    return concat(mappedHead, newTail);
  }

  /**
   * Filters the elements of the list based on a predicate. The complexity of this method is O(n)
   * where n is the number of elements.
   * <p>
   * <b>It does not use structural sharing</b> as it requires advanced data structures to achieve
   * it.
   *
   * @param predicate the predicate to filter the elements
   * @return a new list with the elements filtered
   */
  default SeqList<T> filter(Predicate<T> predicate) {
    if (isEmpty()) {
      return empty();
    }
    if (predicate.test(head())) {
      return new Cons<>(head(), tail().filter(predicate));
    }
    return tail().filter(predicate);
  }

  /**
   * Filters out the elements of the list based on a predicate. The complexity of this method is
   * O(n) where n is the number of elements.
   * <p>
   * <b>It does not use structural sharing</b> as it requires advanced data structures to achieve
   * it.
   *
   * @param predicate the predicate to filter out the elements
   * @return a new list with the elements filtered out
   */
  default SeqList<T> filterOut(Predicate<T> predicate) {
    return filter(predicate.negate());
  }

  /**
   * Finds the first element of the list that satisfies the predicate. The complexity of this method
   * is O(n).
   * <b>It might not terminate for infinite size List</b>
   *
   * @param predicate the predicate to find the element
   * @return an empty {@link Optional} if the element is not found, the element otherwise
   * @throws StackOverflowError for infinite lists
   */
  default Optional<T> findFirst(Predicate<T> predicate) {
    if (isEmpty()) {
      return Optional.empty();
    }
    if (predicate.test(head())) {
      return Optional.of(head());
    }
    return tail().findFirst(predicate);
  }

  /**
   * Returns the element if it exists in the list.
   *
   * @param element the element to find
   * @return an empty {@link Optional} if the element is not found, the element otherwise
   * @throws StackOverflowError for infinite lists
   */
  default Optional<T> get(T element) {
    return findFirst(e -> e.equals(element));
  }

  /**
   * Folds left the elements of the list. The complexity of this method is O(n).
   *
   * @param seed the initial value
   * @param fn   the function to fold the elements
   * @return the result of the fold
   */
  default SeqList<T> foldLeft(T seed, Function<T, Function<T, T>> fn) {
    if (isEmpty()) {
      return empty();
    }
    return tail().foldLeft(fn.apply(seed).apply(head()), fn);
  }

  /**
   * Creates a sublist of the list until the index (exclusive). If the index is negative, an empty
   * list is returned.
   *
   * @param n the index to take until
   * @return a new list with the elements until the index
   * @throws StackOverflowError for infinite lists
   */
  default SeqList<T> take(int n) {
    if (n <= 0 || isEmpty()) {
      return empty();
    }
    return new Cons<>(head(), tail().take(n - 1));
  }

  /**
   * Creates a sublist of the list while the predicate is satisfied. The complexity of this method
   * is O(n) where n is the number of elements.
   *
   * @param predicate the predicate to take while
   * @return a new list with the elements while the predicate is satisfied
   * @throws StackOverflowError for infinite lists
   */
  default SeqList<T> takeWhile(Predicate<T> predicate) {
    if (isEmpty() || !predicate.test(head())) {
      return empty();
    }
    return new Cons<>(head(), tail().takeWhile(predicate));
  }

  /**
   * Confirms if the list satisfies the predicate.
   *
   * @param predicate the predicate to confirm
   * @return true if the list satisfies the predicate, false otherwise
   * @throws StackOverflowError for infinite lists
   */
  default boolean exists(Predicate<T> predicate) {
    return findFirst(predicate).isPresent();
  }

  /**
   * Returns the elements of the list as a Java {@link List}.
   * <p>
   * <b>It may not terminate for infinite size lists</b>
   * <p>
   * <b>NOTE: This method may break immutability if the elements of the list are mutable.</b>
   *
   * @return the elements of the list as a Java {@link List}
   */
  default List<T> toJavaList() {
    List<T> list = new ArrayList<>();
    SeqList<T> current = this;
    while (!current.isEmpty()) {
      list.add(current.head());
      current = current.tail();
    }
    return list;
  }

  /**
   * Returns if the list is empty.
   *
   * @return true if the list is empty, false otherwise
   */
  boolean isEmpty();

  /**
   * Returns the first element of the list.
   *
   * @return the first element of the list
   * @throws UnsupportedOperationException if the list is {@link Empty}
   */
  T head();

  /**
   * Returns the first element of the list as an {@link Optional}.
   *
   * @return the first element of the list as an {@link Optional}
   */
  Optional<T> headOption();

  /**
   * Returns the list without the head element.
   *
   * @return the list without the head element
   * @throws UnsupportedOperationException if the list is {@link Empty}
   */
  SeqList<T> tail();

  /**
   * Returns the last element of the list.
   *
   * @return an empty {@link Optional} if the list is empty, the last element otherwise
   * @throws StackOverflowError for infinite lists
   */
  Optional<T> last();
}
