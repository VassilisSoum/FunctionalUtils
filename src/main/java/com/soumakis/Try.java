package com.soumakis;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Represents a computation that may either result in an exception, or return a successfully
 * computed value. It's a functional programming concept used to handle exceptions in a more
 * functional way.
 *
 * @param <T> the type of the value computed in case of success
 */
public sealed interface Try<T> permits Success, Failure {

  /**
   * Attempts to create a {@code Try} instance by executing a {@code Supplier} function. If the
   * function throws an exception, a {@code Failure} instance is returned, otherwise, a
   * {@code Success} instance.
   *
   * @param fn  the computation to execute
   * @param <T> the type of the result of the computation
   * @return a {@code Try} instance representing the outcome of the computation
   */
  static <T> Try<T> of(Supplier<T> fn) {
    try {
      return new Success<>(fn.get());
    } catch (Throwable throwable) {
      if (isFatalException(throwable)) {
        throw new RuntimeException(throwable);
      }
      return new Failure<>(throwable);
    }
  }

  static <T> Try<T> success(T value) {
    return new Success<>(value);
  }

  static <T> Try<T> failure(Throwable throwable) {
    return new Failure<>(throwable);
  }

  /**
   * Checks if the given throwable is considered a fatal exception. Fatal exceptions are not
   * recoverable and typically indicate a severe problem that should not be caught.
   *
   * @param throwable the exception to check
   * @return {@code true} if the exception is fatal, {@code false} otherwise
   */
  static boolean isFatalException(Throwable throwable) {
    return throwable instanceof VirtualMachineError || throwable instanceof InterruptedException
        || throwable instanceof LinkageError;
  }

  /**
   * Checks if this {@code Try} instance represents a successful computation.
   *
   * @return {@code true} if this is a {@code Success} instance, {@code false} otherwise
   */
  default boolean isSuccess() {
    return this instanceof Success;
  }

  /**
   * Checks if this {@code Try} instance represents a failed computation.
   *
   * @return {@code true} if this is a {@code Failure} instance, {@code false} otherwise
   */
  default boolean isFailure() {
    return this instanceof Failure;
  }

  /**
   * Executes the given consumer if this is a {@code Failure} instance.
   *
   * @param action the consumer to execute
   * @return this {@code Try} instance
   */
  default Try<T> onFailure(Consumer<? super Throwable> action) {
    if (isFailure()) {
      action.accept(((Failure<T>) this).getCause());
    }
    return this;
  }

  /**
   * Executes the given consumer if this is a {@code Success} instance. The method is primarily used
   * for logging side effects
   *
   * @param action the consumer to execute
   * @return this {@code Try} instance
   */
  default Try<T> peek(Consumer<? super T> action) {
    Objects.requireNonNull(action);
    if (isSuccess()) {
      action.accept(((Success<T>) this).value());
    }
    return this;
  }

  /**
   * Executes the failure consumer if this is a {@code Failure} instance, or the success consumer if
   * it is a {@code Success} instance.
   *
   * @param failureAction the consumer to execute if this is a {@code Failure}
   * @param successAction the consumer to execute if this is a {@code Success}
   * @return this {@code Try} instance
   */
  default Try<T> peek(Consumer<? super Throwable> failureAction,
      Consumer<? super T> successAction) {
    Objects.requireNonNull(failureAction);
    Objects.requireNonNull(successAction);

    if (isSuccess()) {
      successAction.accept(((Success<T>) this).value());
    } else {
      failureAction.accept(((Failure<T>) this).getCause());
    }
    return this;
  }

  /**
   * Applies a function to the value if this is a {@code Success}, or a different function if this
   * is a {@code Failure}.
   *
   * @param failureFn the function to apply if this is a {@code Failure}
   * @param successFn the function to apply if this is a {@code Success}
   * @param <U>       the type of the result of the function
   * @return the result of applying the appropriate function
   */
  <U> U fold(Function<Throwable, U> failureFn, Function<T, U> successFn);

  /**
   * Returns the value if this is a {@code Success}, or throws a {@code RuntimeException} if this is
   * a {@code Failure}.
   *
   * @return the value if this is a {@code Success}
   * @throws RuntimeException if this is a {@code Failure}
   */
  T get();

  /**
   * Returns the value if this is a {@code Success}, or the result of invoking a {@code Supplier} if
   * this is a {@code Failure}.
   *
   * @param elseFn the supplier to invoke if this is a {@code Failure}
   * @return the value if this is a {@code Success}, or the result of the supplier if this is a
   * {@code Failure}
   */
  T getOrElse(Supplier<? extends T> elseFn);

  /**
   * Transforms the value if this is a {@code Success}, or returns this if this is a
   * {@code Failure}.
   *
   * @param fn  the function to apply to the value if this is a {@code Success}
   * @param <U> the type of the result of the transformation
   * @return a {@code Try} instance representing the transformed value or this if it's a
   * {@code Failure}
   */
  <U> Try<U> map(Function<? super T, ? extends U> fn);

  /**
   * Transforms the value if this is a {@code Success}, or returns this if this is a
   * {@code Failure}.
   *
   * @param fn  the function to apply to the value if this is a {@code Success}
   * @param <U> the type of the result of the transformation
   * @return a {@code Try} instance representing the transformed value or this if it's a
   * {@code Failure}
   */
  <U> Try<U> flatMap(Function<? super T, ? extends Try<U>> fn);

  /**
   * Recovers from a failed computation by applying a function to the exception if this is a
   * {@code Failure}, or returns this if this is a {@code Success}.
   *
   * @param fn the function to apply to the exception if this is a {@code Failure}
   * @return a {@code Try} instance representing the recovered value or this if it's a
   * {@code Success}
   */
  Try<T> recover(Function<? super Throwable, ? extends T> fn);

  /**
   * Recovers from a failed computation by applying a function to the exception if this is a
   * {@code Failure}, or returns this if this is a {@code Success}.
   *
   * @param fn  the function to apply to the exception if this is a {@code Failure}
   * @param <U> the type of the recovered value
   * @return a {@code Try} instance representing the recovered value or this if it's a
   * {@code Success}
   */
  <U> Try<U> recoverWith(Function<? super Throwable, ? extends Try<? extends U>> fn);

  /**
   * Converts this {@code Try} instance to an {@code Optional} instance.
   *
   * @return an {@code Optional} instance containing the value if this is a {@code Success}, or
   * empty if this is a {@code Failure}
   */
  Optional<T> toOptional();
}

record Success<T>(T value) implements Try<T> {

  @Override
  public <U> U fold(Function<Throwable, U> failureFn, Function<T, U> successFn) {
    try {
      return successFn.apply(value);
    } catch (Throwable t) {
      if (Try.isFatalException(t)) {
        throw t;
      }
      return failureFn.apply(t);
    }
  }

  @Override
  public T get() {
    return value;
  }

  @Override
  public T getOrElse(Supplier<? extends T> elseFn) {
    return get();
  }

  @Override
  public <U> Try<U> map(Function<? super T, ? extends U> fn) {
    return Try.of(() -> fn.apply(value));
  }

  @Override
  public <U> Try<U> flatMap(Function<? super T, ? extends Try<U>> fn) {
    try {
      return fn.apply(value);
    } catch (Throwable t) {
      if (Try.isFatalException(t)) {
        throw t;
      }
      return new Failure<>(t);
    }
  }

  @Override
  public Try<T> recover(Function<? super Throwable, ? extends T> fn) {
    return this;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <U> Try<U> recoverWith(Function<? super Throwable, ? extends Try<? extends U>> fn) {
    return (Try<U>) this;
  }

  @Override
  public Optional<T> toOptional() {
    return Optional.of(value);
  }

}

record Failure<T>(Throwable throwable) implements Try<T> {

  @Override
  public <U> U fold(Function<Throwable, U> failureFn, Function<T, U> successFn) {
    return failureFn.apply(throwable);
  }

  @Override
  public T get() {
    throw new RuntimeException(throwable);
  }

  public Throwable getCause() {
    return throwable;
  }

  @Override
  public T getOrElse(Supplier<? extends T> elseFn) {
    return elseFn.get();
  }

  @Override
  @SuppressWarnings("unchecked")
  public <U> Try<U> map(Function<? super T, ? extends U> fn) {
    return (Try<U>) this;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <U> Try<U> flatMap(Function<? super T, ? extends Try<U>> fn) {
    return (Try<U>) this;
  }

  @Override
  public Try<T> recover(Function<? super Throwable, ? extends T> fn) {
    try {
      return new Success<>(fn.apply(throwable));
    } catch (Throwable t) {
      if (Try.isFatalException(t)) {
        throw t;
      }
      return new Failure<>(t);
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  public <U> Try<U> recoverWith(Function<? super Throwable, ? extends Try<? extends U>> fn) {
    try {
      return (Try<U>) fn.apply(throwable);
    } catch (Throwable t) {
      if (Try.isFatalException(t)) {
        throw t;
      }
      return new Failure<>(t);
    }
  }

  @Override
  public Optional<T> toOptional() {
    return Optional.empty();
  }

}
