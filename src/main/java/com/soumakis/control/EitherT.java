package com.soumakis.control;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * The {@code EitherT} monad transformer class encapsulates an {@code Either} monad inside a
 * {@code CompletableFuture}. This allows chaining and composing asynchronous computations that may
 * fail, using functional programming principles.
 *
 * @param <A> the type of the left value
 * @param <B> the type of the right value
 */
public class EitherT<A, B> {

  private final CompletableFuture<Either<A, B>> future;

  private EitherT(CompletableFuture<Either<A, B>> future) {
    this.future = Objects.requireNonNull(future, "future must not be null");
  }

  /**
   * Constructs an {@code EitherT} instance with a right value.
   *
   * @param value the right value
   * @param <A>   the type of the left value
   * @param <B>   the type of the right value
   * @return an {@code EitherT} instance containing the right value
   */
  public static <A, B> EitherT<A, B> right(B value) {
    return new EitherT<>(CompletableFuture.completedFuture(Either.right(value)));
  }

  /**
   * Creates an {@code EitherT} instance representing a failure.
   *
   * @param exception the exception to be wrapped
   * @param <A>       the type of the left value
   * @param <B>       the type of the right value
   * @return an {@code EitherT} instance containing the failure
   */
  public static <A, B> EitherT<A, B> left(A exception) {
    return new EitherT<>(CompletableFuture.completedFuture(Either.left(exception)));
  }

  /**
   * Creates an {@code EitherT} instance from a {@code CompletableFuture} of {@code Either}.
   *
   * @param future the {@code CompletableFuture} of {@code Either} to be wrapped
   * @param <A>    the type of the left value
   * @param <B>    the type of the right value
   * @return an {@code EitherT} instance wrapping the given {@code CompletableFuture}
   */
  public static <A, B> EitherT<A, B> fromFuture(CompletableFuture<Either<A, B>> future) {
    return new EitherT<>(future);
  }

  /**
   * Maps the right value of the {@code Either} monad.
   *
   * @param mapper the function to apply to the right value
   * @param <C>    the type of the new right value
   * @return a new {@code EitherT} instance with the mapped value
   */
  public <C> EitherT<A, C> map(Function<B, C> mapper) {
    Objects.requireNonNull(mapper, "mapper must not be null");
    return new EitherT<>(future.thenApply(either -> either.map(mapper)));
  }

  /**
   * Flat maps the right value of the {@code Either} monad.
   *
   * @param mapper the function to apply to the right value
   * @param <C>    the type of the new right value
   * @return a new {@code EitherT} instance with the mapped value
   */
  public <C> EitherT<A, C> flatMap(Function<B, EitherT<A, C>> mapper) {
    Objects.requireNonNull(mapper, "mapper must not be null");
    return new EitherT<>(
        future.thenCompose(
            either ->
                either.fold(
                    left -> CompletableFuture.completedFuture(Either.left(left)),
                    right -> mapper.apply(right).future)));
  }

  /**
   * Maps both the left and right values of the {@code Either} monad.
   *
   * @param leftMapper  the function to apply to the left value
   * @param rightMapper the function to apply to the right value
   * @param <C>         the type of the new left value
   * @param <D>         the type of the new right value
   * @return a new {@code EitherT} instance with the mapped values
   */
  public <C, D> EitherT<C, D> biFlatMap(
      Function<A, EitherT<C, D>> leftMapper, Function<B, EitherT<C, D>> rightMapper) {
    Objects.requireNonNull(leftMapper, "leftMapper must not be null");
    Objects.requireNonNull(rightMapper, "rightMapper must not be null");
    return new EitherT<>(
        future.thenCompose(
            either ->
                either.fold(
                    left -> leftMapper.apply(left).future,
                    right -> rightMapper.apply(right).future)));
  }

  /**
   * Maps either the left or right values of the {@code Either} monad.
   *
   * @param leftMapper  the function to apply to the left value
   * @param rightMapper the function to apply to the right value
   * @param <C>         the type of the new left value
   * @param <D>         the type of the new right value
   * @return a new {@code EitherT} instance with the mapped values
   */
  public <C, D> EitherT<C, D> biMap(Function<A, C> leftMapper, Function<B, D> rightMapper) {
    Objects.requireNonNull(leftMapper, "leftMapper must not be null");
    Objects.requireNonNull(rightMapper, "rightMapper must not be null");
    return new EitherT<>(future.thenApply(either -> {
      if (either.isLeft()) {
        return Either.left(leftMapper.apply(either.getLeft()));
      }
      return Either.right(rightMapper.apply(either.getRight()));
    }));
  }

  /**
   * Maps the left value of the {@code Either} monad.
   *
   * @param leftMapper the function to apply to the left value
   * @param <C>        the type of the new left value
   * @return a new {@code EitherT} instance with the mapped value
   */
  public <C> EitherT<C, B> leftMap(Function<A, C> leftMapper) {
    Objects.requireNonNull(leftMapper, "leftMapper must not be null");
    return new EitherT<>(future.thenApply(either -> either.leftMap(leftMapper)));
  }

  /**
   * Returns the underlying {@code CompletableFuture} of {@code Either}.
   *
   * @return the {@code CompletableFuture<Either<A, B>>} representing the asynchronous computation
   */
  public CompletableFuture<Either<A, B>> toCompletableFuture() {
    return future;
  }

  /**
   * Recovers from a failure by applying a function that returns a new {@code EitherT} instance.
   *
   * @param recover the function to apply to the left value
   * @return a new {@code EitherT} instance with the recovered value
   */
  public EitherT<A, B> recoverWith(Function<A, EitherT<A, B>> recover) {
    Objects.requireNonNull(recover, "recover must not be null");
    return new EitherT<>(
        future.thenCompose(
            either ->
                either.fold(
                    left -> recover.apply(left).toCompletableFuture(),
                    right -> CompletableFuture.completedFuture(Either.right(right)))));
  }

  /**
   * Converts to {@code TryT} by wrapping the {@code CompletableFuture} of {@code Either} in a
   * {@code TryT}. If the {@code Either} is a {@code Left}, the {@code Try} will be a failure.
   *
   * @return a {@code TryT} instance wrapping the {@code CompletableFuture} of {@code Either} with a
   * {@code Try} inside it.
   */
  public TryT<B> toTryT() {
    return TryT.fromFuture(future.thenApply(either -> switch (either) {
      case Left<A, B> ignored -> Try.failure(new RuntimeException());
      case Right<A, B> right -> Try.success(right.value());
    }));
  }
}