package com.soumakis.safetype;

import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * A {@link Supplier} that allows invocation of code that throws a checked exception.
 *
 * @param <T> the type of results supplied by this supplier
 */
public interface ThrowingSupplier<T> extends Supplier<T> {

  /**
   * Gets a result, possibly throwing a checked exception.
   *
   * @return a result
   * @throws Exception on error
   */
  T getWithException() throws Exception;

  /**
   * Default {@link Supplier#get()} that wraps any thrown checked exceptions (by default in a
   * {@link RuntimeException}).
   *
   * @see java.util.function.Supplier#get()
   */
  @Override
  default T get() {
    return get(RuntimeException::new);
  }

  /**
   * Gets a result, wrapping any thrown checked exceptions using the given
   * {@code exceptionWrapper}.
   *
   * @param exceptionWrapper {@link BiFunction} that wraps the given message and checked exception
   *                         into a runtime exception
   * @return a result
   */
  default T get(BiFunction<String, Exception, RuntimeException> exceptionWrapper) {
    try {
      return getWithException();
    } catch (RuntimeException ex) {
      throw ex;
    } catch (Exception ex) {
      throw exceptionWrapper.apply(ex.getMessage(), ex);
    }
  }
}
