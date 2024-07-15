package com.soumakis.control;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Represents a value of one of two possible types (a disjoint union). Instances of {@code Either}
 * are either an instance of {@code Left} or {@code Right}. {@code Either} is useful for error
 * handling, allowing you to return either a result or an error.
 *
 * <p>Example usage:
 * <ul>
 *   <li>Returning a result or an error from a method and logging either case:
 *   <pre>{@code
 *     return someOperation()
 *         .peek(System.err::println, System.out::println)
 *         .fold(
 *             error -> sendErrorToMonitoringTool("An error occurred: " + error),
 *             value -> saveResult(value)
 *         );
 *   }</pre>
 *   versus an imperative code style of:
 *   <pre>{@code
 *     try {
 *         String result = someOperation();
 *         System.out.println(result);
 *         saveResult(result);
 *     } catch (RuntimeException e) {
 *         System.err.println(e.getMessage());
 *         sendErrorToMonitoringTool("An error occurred: " + e.getMessage());
 *     }
 *   }</pre>
 *   It allows you to handle the error and success cases in a more functional way, avoiding deeply nested try-catch statements which disrupt the flow
 *   and make the code harder to read.
 *   </li>
 *
 *   <li>Chaining operations that may fail:
 *   <pre>{@code
 *     Either<Error, String> result = someOperation()
 *         .flatMap(this::anotherOperation)
 *         .flatMap(this::yetAnotherOperation)
 *         .leftMap(error -> handleFailure(error));
 *   }</pre>
 *   versus an imperative code style of:
 *   <pre>{@code
 *     try {
 *         String result = someOperation();
 *         result = anotherOperation(result);
 *         result = yetAnotherOperation(result);
 *     } catch (RuntimeException e) {
 *         handleFailure(e);
 *     }
 *   }</pre>
 *   It allows you to chain operations that may fail and handle the error cases in a more functional way.
 *   Especially when there are a lot of operations that may fail, the code becomes more readable and maintainable.
 *   </li>
 *   <li>Easier pattern matching:
 *   <pre>{@code
 *     Either<Error, String> result = someOperation();
 *     switch (result) {
 *         case Left<Error, String> error -> handleFailure(error);
 *         case Right<Error, String> value -> saveResult(value);
 *     }
 *   }</pre>
 *   versus an imperative code style of:
 *   <pre>{@code
 *     try {
 *         String result = someOperation();
 *         saveResult(result);
 *     } catch (RuntimeException e) {
 *         handleFailure(e);
 *     }
 *   }</pre>
 *   </li>
 *   <li>Returning the result or another value if the result is an error:
 *   <pre>{@code
 *     Either<String, Integer> result = someOperation();
 *     int value = result.getOrElse(0);
 *   }</pre>
 *   versus an imperative code style that mutates the value in a try-catch block:
 *   <pre>{@code
 *     int value;
 *     try {
 *         value = someOperation();
 *     } catch (RuntimeException e) {
 *         value = 0;
 *     }
 *   }</pre>
 *   </li>
 * </ul>
 *
 * @param <L> the type of the {@code Left} value
 * @param <R> the type of the {@code Right} value
 */
public sealed interface Either<L, R> permits Left, Right {

  /**
   * Creates a {@code Left} type of {@code Either}.
   *
   * @param left the value to be stored in {@code Left}
   * @param <L>  the type of the left value
   * @param <R>  the type of the right value
   * @return an {@code Either} instance containing a {@code Left} value
   */
  static <L, R> Either<L, R> left(L left) {
    return new Left<>(left);
  }

  /**
   * Creates a {@code Right} type of {@code Either}.
   *
   * @param right the value to be stored in {@code Right}
   * @param <L>   the type of the left value
   * @param <R>   the type of the right value
   * @return an {@code Either} instance containing a {@code Right} value
   */
  static <L, R> Either<L, R> right(R right) {
    return new Right<>(right);
  }

  /**
   * Checks if the instance is of type {@code Left}.
   *
   * @return {@code true} if the instance is a {@code Left}, otherwise {@code false}
   */
  default boolean isLeft() {
    return this instanceof Left;
  }

  /**
   * Checks if the instance is of type {@code Right}.
   *
   * @return {@code true} if the instance is a {@code Right}, otherwise {@code false}
   */
  default boolean isRight() {
    return this instanceof Right;
  }

  /**
   * Gets the value from {@code Left}. This method should only be called if {@code isLeft()} returns
   * {@code true}.
   *
   * @return the value of {@code Left}
   * @throws ClassCastException if the instance is not a {@code Left}
   */
  default L getLeft() {
    return ((Left<L, R>) this).value();
  }

  /**
   * Gets the value from {@code Right}. This method should only be called if {@code isRight()}
   * returns {@code true}.
   *
   * @return the value of {@code Right}
   * @throws ClassCastException if the instance is not a {@code Right}
   */
  default R getRight() {
    return ((Right<L, R>) this).value();
  }

  /**
   * Applies a function to the value inside {@code Either}, depending on whether it is a
   * {@code Left} or a {@code Right}.
   *
   * @param leftFn  the function to apply if the value is a {@code Left}
   * @param rightFn the function to apply if the value is a {@code Right}
   * @param <C>     the return type of the function
   * @return the result of applying the function
   */
  default <C> C fold(Function<? super L, ? extends C> leftFn,
      Function<? super R, ? extends C> rightFn) {
    if (isLeft()) {
      return leftFn.apply(getLeft());
    }
    return rightFn.apply(getRight());
  }

  /**
   * Transforms the {@code Right} value of this {@code Either} if it is {@code Right}, otherwise
   * returns itself.
   *
   * @param fn  the mapping function to apply to a {@code Right} value
   * @param <C> the type of the result of the mapping function
   * @return a new {@code Either} instance with the transformed value if this is a {@code Right},
   * otherwise this
   */
  default <C> Either<L, C> map(Function<? super R, ? extends C> fn) {
    if (isLeft()) {
      return Either.left(getLeft());
    }
    return Either.right(fn.apply(getRight()));
  }

  default <C> Either<C, R> leftMap(Function<? super L, ? extends C> fn) {
    if (isRight()) {
      return Either.right(getRight());
    }
    return Either.left(fn.apply(getLeft()));
  }

  /**
   * Applies a function to the {@code Right} value of this {@code Either} that returns an
   * {@code Either}, effectively chaining multiple {@code Either} operations.
   *
   * @param fn  the function to apply to a {@code Right} value
   * @param <C> the type of the right value of the returned {@code Either}
   * @return the result of applying the function if this is a {@code Right}, otherwise this
   */
  default <C> Either<L, C> flatMap(Function<? super R, ? extends Either<L, C>> fn) {
    if (isLeft()) {
      return Either.left(getLeft());
    }
    return fn.apply(getRight());
  }

  /**
   * Returns the given {@code either} if this is a {@code Right}, otherwise returns itself. This can
   * be seen as an {@code Either} version of a logical AND operation.
   *
   * @param either the {@code Either} to return if this is a {@code Right}
   * @param <C>    the type of the right value of the given {@code Either}
   * @return the given {@code either} if this is a {@code Right}, otherwise this
   */
  default <C> Either<L, C> andThen(Either<L, C> either) {
    return flatMap(x -> either);
  }

  /**
   * Swaps the sides of this {@code Either}. {@code Left} becomes {@code Right} and vice versa.
   *
   * @return a new {@code Either} instance with sides swapped
   */
  default Either<R, L> swap() {
    if (isLeft()) {
      return Either.right(getLeft());
    }
    return Either.left(getRight());
  }

  /**
   * Returns the value of this {@code Either} if it is a {@code Right}, otherwise returns the given
   * value.
   *
   * @param other the value to return if this is a {@code Left}
   * @return the value of this {@code Either} if it is a {@code Right}, otherwise the given value
   */
  default R getOrElse(R other) {
    if (isLeft()) {
      return other;
    }
    return getRight();
  }

  /**
   * Returns the value of this {@code Either} if it is a {@code Right}, otherwise returns the result
   * of the given supplier.
   *
   * @param supplier the supplier to return if this is a {@code Left}
   * @return the value of this {@code Either} if it is a {@code Right}, otherwise the result of the
   * supplier
   */
  default R getOrElseGet(Function<? super L, ? extends R> supplier) {
    if (isLeft()) {
      return supplier.apply(getLeft());
    }
    return getRight();
  }

  /**
   * Returns the value of this {@code Either} if it is a {@code Right}, otherwise throws an
   * exception.
   *
   * @param fn the function to apply if this is a {@code Left}
   * @return the value of this {@code Either} if it is a {@code Right}
   */
  default <X extends Throwable> R getOrElseThrow(Function<? super L, ? extends X> fn) throws X {
    if (isLeft()) {
      throw fn.apply(getLeft());
    }
    return getRight();
  }

  /**
   * Converts this {@code Either} to an {@code Optional}. If this is a {@code Left}, returns an
   * empty {@code Optional}.
   *
   * @return an {@code Optional} containing the value of this {@code Either} if it is a
   * {@code Right}, otherwise an empty {@code Optional}
   */
  default Optional<R> toOptional() {
    return fold(l -> Optional.empty(), Optional::of);
  }

  /**
   * Applies the given consumers to the value of this {@code Either} depending on whether it is a
   * {@code Left} or a {@code Right}.
   *
   * @param leftConsumer  the consumer to apply if the value is a {@code Left}
   * @param rightConsumer the consumer to apply if the value is a {@code Right}
   * @return this {@code Either} instance
   */
  default Either<L, R> peek(Consumer<? super L> leftConsumer, Consumer<? super R> rightConsumer) {
    if (isLeft()) {
      leftConsumer.accept(getLeft());
    } else {
      rightConsumer.accept(getRight());
    }
    return this;
  }
}
