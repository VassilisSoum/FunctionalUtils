package com.soumakis;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * The {@code TryT} monad transformer class encapsulates a {@code Try} monad inside a
 * {@code CompletableFuture}. This allows chaining and composing asynchronous computations that may
 * fail, using functional programming principles.
 *
 * <p>This class provides methods to transform and compose the inner {@code Try} monad values
 * through asynchronous operations. It supports operations such as {@link #map(Function)} and
 * {@link #flatMap(Function)}, which enable you to perform transformations and handle the potential
 * failures of asynchronous computations in a clean and expressive manner.
 *
 * @param <T> the type of the value contained within the {@code TryT} monad
 */
public class TryT<T> {

  private final CompletableFuture<Try<T>> future;

  /**
   * Constructs a {@code TryT} instance with a {@code CompletableFuture} of {@code Try}.
   *
   * @param future the {@code CompletableFuture<Try<T>>} representing the asynchronous computation
   */
  private TryT(CompletableFuture<Try<T>> future) {
    this.future = future;
  }

  /**
   * Creates a {@code TryT} instance representing a successful value.
   *
   * @param value the value to be wrapped
   * @param <T>   the type of the value
   * @return a {@code TryT} instance containing the successful value
   */
  public static <T> TryT<T> of(T value) {
    return new TryT<>(CompletableFuture.completedFuture(Try.success(value)));
  }

  /**
   * Creates a {@code TryT} instance representing a failure.
   *
   * @param exception the exception to be wrapped
   * @param <T>       the type of the value
   * @return a {@code TryT} instance containing the failure
   */
  public static <T> TryT<T> ofFailure(Throwable exception) {
    return new TryT<>(CompletableFuture.completedFuture(Try.failure(exception)));
  }

  /**
   * Creates a {@code TryT} instance from a {@code CompletableFuture} of {@code Try}.
   *
   * @param future the {@code CompletableFuture} of {@code Try} to be wrapped
   * @param <T>    the type of the value
   * @return a {@code TryT} instance wrapping the given {@code CompletableFuture}
   */
  public static <T> TryT<T> fromFuture(CompletableFuture<Try<T>> future) {
    return new TryT<>(future);
  }

  /**
   * Returns the underlying {@code CompletableFuture} of {@code Try}.
   *
   * @return the {@code CompletableFuture<Try<T>>} representing the asynchronous computation
   */
  public CompletableFuture<Try<T>> toCompletableFuture() {
    return future;
  }

  /**
   * Transforms the value contained within this {@code TryT} instance using the given mapping
   * function.
   *
   * <p>If this {@code TryT} contains a failure, the failure is propagated without applying the
   * mapping function.
   *
   * @param mapper the function to apply to the contained value
   * @param <U>    the type of the new value
   * @return a new {@code TryT} instance containing the transformed value
   */
  public <U> TryT<U> map(Function<? super T, ? extends U> mapper) {
    return new TryT<>(future.thenApply(t -> t.map(mapper)));
  }

  /**
   * Flat maps the value contained within this {@code TryT} instance using the given mapping
   * function that returns a new {@code TryT}.
   *
   * <p>If this {@code TryT} contains a failure, the failure is propagated without applying the
   * mapping function.
   *
   * @param mapper the function to apply to the contained value, returning a new {@code TryT}
   * @param <U>    the type of the new value
   * @return a new {@code TryT} instance containing the transformed value
   */
  public <U> TryT<U> flatMap(Function<? super T, TryT<U>> mapper) {
    return new TryT<>(
        future.thenCompose(
            t -> switch (t) {
              case Success<T> success -> mapper.apply(success.get()).toCompletableFuture();
              case Failure<T> failure ->
                  CompletableFuture.completedFuture(Try.failure(failure.throwable()));
            }
        ));
  }

  /**
   * Recovers from a failure by applying the given function to the exception.
   *
   * @param recoverFunction the function to apply to the exception
   * @return a new {@code TryT} instance with the recovered value
   */
  public TryT<T> recover(Function<Throwable, T> recoverFunction) {
    return new TryT<>(future.thenApply(t -> t.recover(recoverFunction)));
  }
}