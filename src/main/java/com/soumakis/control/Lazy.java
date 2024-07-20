package com.soumakis.control;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

/**
 * A lazy value or computation that is evaluated only once.
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
 *
 * Another example that could be use is to defer the execution of a CompletableFuture and memoize it as well.
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

  private final Supplier<T> lazyValue;
  private final AtomicReference<T> evaluatedValue = new AtomicReference<>(null);
  private final Object lock = new Object();

  private Lazy(Supplier<T> lazyValue) {
    this.lazyValue = lazyValue;
  }

  /**
   * Creates a new lazy value.
   *
   * @param supplier the supplier that provides the value
   * @param <T>      the type of the value
   * @return a new lazy value
   */
  public static <T> Lazy<T> of(Supplier<T> supplier) {
    return new Lazy<>(supplier);
  }

  /**
   * Gets the value. If the value has not been evaluated yet, it evaluates it and caches it.
   *
   * @return the value
   */
  public T get() {
    T value = evaluatedValue.get();
    if (value == null) {
      synchronized (lock) {
        value = evaluatedValue.get();
        if (value == null) {
          value = lazyValue.get();
          evaluatedValue.set(value);
        }
      }
    }
    return value;
  }
}