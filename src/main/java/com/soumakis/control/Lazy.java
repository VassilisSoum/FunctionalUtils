package com.soumakis.control;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A monad representing a lazy evaluated computation which is memoized.
 *
 * <p>Example usage:</p>
 *
 * <pre>
 *   {@code
 *   Lazy<T> lazy = Lazy.of(() -> expensiveComputation());
 *
 *   T value = lazy.get(); //Result is memoized.
 *   T value2 = lazy.get(); //Result is not recomputed.
 *   value == value2; //true
 *
 *   }
 * </pre>
 * <p>
 * Another example that could be use is to defer the execution of a CompletableFuture and memoize it
 * as well.
 *
 * <pre>
 *   {@code
 *   Lazy<CompletableFuture<T>> lazy = Lazy.of(() -> CompletableFuture.supplyAsync(() -> expensiveComputation()));
 *
 *   CompletableFuture<T> future = lazy.get(); //Result is memoized.
 *   CompletableFuture<T> future2 = lazy.get(); //Result is not recomputed.
 *   future == future2; //true
 *   }
 * </pre>
 *
 * @param <T> the type of the value
 */
public final class Lazy<T> {

  // Using a sentinel value to represent uninitialized state because null is a valid value.
  private static final Object UNINITIALIZED = new Object();

  private final Supplier<T> lazyValue;
  private final Object lock = new Object();
  private volatile Object evaluatedValue = UNINITIALIZED;

  private Lazy(Supplier<T> lazyValue) {
    this.lazyValue = lazyValue;
  }

  private Lazy(T value) {
    this.lazyValue = () -> value;
    this.evaluatedValue = value;
  }

  /**
   * Creates a new lazy value.
   *
   * @param supplier the supplier that provides the value
   * @param <T>      the type of the value
   * @return a new lazy value
   * @throws NullPointerException if the supplier is null
   */
  public static <T> Lazy<T> of(Supplier<T> supplier) {
    return new Lazy<>(Objects.requireNonNull(supplier));
  }

  /**
   * Creates a new lazy value with an already evaluated value. Useful when you want to memoize a
   * value that is already computed.
   *
   * @param value the value
   * @param <T>   the type of the value
   * @return a new lazy value
   */
  public static <T> Lazy<T> evaluated(T value) {
    return new Lazy<>(value);
  }

  /**
   * Gets the value. If the value has not been evaluated yet, it evaluates it and caches it.
   *
   * @return the value
   */
  @SuppressWarnings("unchecked")
  public T get() {
    Object value = evaluatedValue;
    if (value == UNINITIALIZED) {
      synchronized (lock) {
        value = evaluatedValue;
        if (value == UNINITIALIZED) {
          value = lazyValue.get();
          evaluatedValue = value;
        }
      }
    }
    return (T) value;
  }

  /**
   * Maps the value of this lazy instance to a new value.
   *
   * @param mapper the mapping function
   * @param <R>    the new type of the value
   * @return a new lazy instance with the mapped value
   */
  public <R> Lazy<R> map(Function<? super T, ? extends R> mapper) {
    return Lazy.of(() -> mapper.apply(this.get()));
  }

  /**
   * Flat maps the value of this lazy instance to a new lazy instance.
   * <b>It is susceptible to stack overflow if the function passed to it is not tail recursive.</b>
   *
   * @param mapper the mapping function
   * @param <R>    the new type of the value
   * @return a new lazy instance with the mapped value
   * @throws StackOverflowError if the function passed to it is not tail recursive
   */
  public <R> Lazy<R> flatMap(Function<? super T, Lazy<R>> mapper) {
    return Lazy.of(() -> mapper.apply(this.get()).get());
  }

}