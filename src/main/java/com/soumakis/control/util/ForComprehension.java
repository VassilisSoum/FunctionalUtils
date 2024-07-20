package com.soumakis.control.util;

import com.soumakis.control.Either;
import com.soumakis.control.Failure;
import com.soumakis.control.Try;
import java.util.function.BiFunction;

/**
 * A utility class that provides a for-comprehension like syntax for working with monads.
 *
 * <p>Example usage:</p>
 *
 * <pre>
 *   {@code
 *   Either<String, Integer> e1 = computation1();
 *   Either<String, Double> e2 = computation2();
 *   Either<String, Integer> e3 = computation3();
 *
 *   Either<String, Double> result = ForComprehension.For3(e1, e2, e3, (i1, d, i2) -> i1 + d + i2);
 *   }
 * </pre>
 *
 * instead of doing
 *
 * <pre>
 *   {@code
 *   Either<String, Integer> e1 = computation1();
 *   Either<String, Double> e2 = computation2();
 *   Either<String, Integer> e3 = computation3();
 *
 *   Either<String, Double> result = e1.flatMap(i1 -> e2.flatMap(d -> e3.map(i2 -> i1 + d + i2)));
 *   }
 * </pre>
 */
public class ForComprehension {

  /**
   * A for-comprehension like syntax for working with {@link Either} monads. It short-circuits if
   * any of the {@link Either} instances is a left.
   *
   * @param either1 the first {@link Either} instance
   * @param either2 the second {@link Either} instance
   * @param fn      a function that takes two values and returns a new value
   * @param <L>     The type of the left value
   * @param <R1>    The type of the right value of the first {@link Either} instance
   * @param <R2>    The type of the right value of the second {@link Either} instance
   * @param <A>     The new type of the value
   * @return a new {@link Either} instance
   */
  public static <L, R1, R2, A> Either<L, A> For2(Either<L, R1> either1,
      Either<L, R2> either2, BiFunction<R1, R2, A> fn) {
    if (either1.isLeft()) {
      return Either.left(either1.getLeft());
    }
    if (either2.isLeft()) {
      return Either.left(either2.getLeft());
    }
    return Either.right(fn.apply(either1.getRight(), either2.getRight()));
  }

  public static <L, R1, R2, R3, A> Either<L, A> For3(Either<L, R1> either1,
      Either<L, R2> either2, Either<L, R3> either3, TriFunction<R1, R2, R3, A> fn) {
    if (either1.isLeft()) {
      return Either.left(either1.getLeft());
    }
    if (either2.isLeft()) {
      return Either.left(either2.getLeft());
    }
    if (either3.isLeft()) {
      return Either.left(either3.getLeft());
    }
    return Either.right(fn.apply(either1.getRight(), either2.getRight(), either3.getRight()));
  }

  public static <L, R1, R2, R3, R4, A> Either<L, A> For4(Either<L, R1> either1,
      Either<L, R2> either2, Either<L, R3> either3, Either<L, R4> either4,
      QuadFunction<R1, R2, R3, R4, A> fn) {
    if (either1.isLeft()) {
      return Either.left(either1.getLeft());
    }
    if (either2.isLeft()) {
      return Either.left(either2.getLeft());
    }
    if (either3.isLeft()) {
      return Either.left(either3.getLeft());
    }
    if (either4.isLeft()) {
      return Either.left(either4.getLeft());
    }
    return Either.right(
        fn.apply(either1.getRight(), either2.getRight(), either3.getRight(), either4.getRight()));
  }

  public static <L, R1, R2, R3, R4, R5, A> Either<L, A> For5(Either<L, R1> either1,
      Either<L, R2> either2, Either<L, R3> either3, Either<L, R4> either4, Either<L, R5> either5,
      PentaFunction<R1, R2, R3, R4, R5, A> fn) {
    if (either1.isLeft()) {
      return Either.left(either1.getLeft());
    }
    if (either2.isLeft()) {
      return Either.left(either2.getLeft());
    }
    if (either3.isLeft()) {
      return Either.left(either3.getLeft());
    }
    if (either4.isLeft()) {
      return Either.left(either4.getLeft());
    }
    if (either5.isLeft()) {
      return Either.left(either5.getLeft());
    }
    return Either.right(
        fn.apply(either1.getRight(), either2.getRight(), either3.getRight(), either4.getRight(),
            either5.getRight()));
  }

  /**
   * A for-comprehension like syntax for working with {@link Try} monads. It short-circuits if any
   * of the {@link Try} instances is a failure.
   *
   * @param try1 the first {@link Try} instance
   * @param try2 the second {@link Try} instance
   * @param fn   a function that takes two values and returns a new value
   * @param <T1> The type of the value of the first {@link Try} instance
   * @param <T2> The type of the value of the second {@link Try} instance
   * @param <U>  The new type of the value
   * @return a new {@link Try} instance
   */
  public static <T1, T2, U> Try<U> For2(Try<T1> try1,
      Try<T2> try2, BiFunction<T1, T2, U> fn) {
    if (try1.isFailure()) {
      return Try.failure(((Failure<T1>) try1).throwable());
    }
    if (try2.isFailure()) {
      return Try.failure(((Failure<T2>) try2).throwable());
    }
    return Try.success(fn.apply(try1.get(), try2.get()));
  }

  public static <T1, T2, T3, U> Try<U> For3(Try<T1> try1,
      Try<T2> try2, Try<T3> try3, TriFunction<T1, T2, T3, U> fn) {
    if (try1.isFailure()) {
      return Try.failure(((Failure<T1>) try1).throwable());
    }
    if (try2.isFailure()) {
      return Try.failure(((Failure<T2>) try2).throwable());
    }
    if (try3.isFailure()) {
      return Try.failure(((Failure<T3>) try3).throwable());
    }
    return Try.success(fn.apply(try1.get(), try2.get(), try3.get()));
  }

  public static <T1, T2, T3, T4, U> Try<U> For4(Try<T1> try1,
      Try<T2> try2, Try<T3> try3, Try<T4> try4, QuadFunction<T1, T2, T3, T4, U> fn) {
    if (try1.isFailure()) {
      return Try.failure(((Failure<T1>) try1).throwable());
    }
    if (try2.isFailure()) {
      return Try.failure(((Failure<T2>) try2).throwable());
    }
    if (try3.isFailure()) {
      return Try.failure(((Failure<T3>) try3).throwable());
    }
    if (try4.isFailure()) {
      return Try.failure(((Failure<T4>) try4).throwable());
    }
    return Try.success(fn.apply(try1.get(), try2.get(), try3.get(), try4.get()));
  }

  public static <T1, T2, T3, T4, T5, U> Try<U> For5(Try<T1> try1,
      Try<T2> try2, Try<T3> try3, Try<T4> try4, Try<T5> try5,
      PentaFunction<T1, T2, T3, T4, T5, U> fn) {
    if (try1.isFailure()) {
      return Try.failure(((Failure<T1>) try1).throwable());
    }
    if (try2.isFailure()) {
      return Try.failure(((Failure<T2>) try2).throwable());
    }
    if (try3.isFailure()) {
      return Try.failure(((Failure<T3>) try3).throwable());
    }
    if (try4.isFailure()) {
      return Try.failure(((Failure<T4>) try4).throwable());
    }
    if (try5.isFailure()) {
      return Try.failure(((Failure<T5>) try5).throwable());
    }
    return Try.success(fn.apply(try1.get(), try2.get(), try3.get(), try4.get(), try5.get()));
  }

}
