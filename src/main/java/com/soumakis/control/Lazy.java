package com.soumakis.control;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

/**
 * A lazy value or computation that is evaluated only once.
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