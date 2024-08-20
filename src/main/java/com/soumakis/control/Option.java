package com.soumakis.control;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * A type that represents optional values. Instances of {@code Option} are either an instance of
 * {@link Some} or the object {@link None}.
 * <p>
 * The major differences between {@link Option} and {@link java.util.Optional} are:
 * <ul>
 *   <li>Optional allows the program to throw NoSuchElementException because it exposes a method `get`
 *   in the Optional itself. Whereas Option does not but only in the Some instance</li>
 *   <li>Optional throws NullPointerException if the value in the `of` method is null, whereas Option creates an instance of None in this case</li>
 *   <li>Optional is a final class, whereas Option is a sealed interface forming an ADT and is especially suitable for pattern matching</li>
 *   <li>Optional exposes a lot more utility methods but focuses on methods such as `ifPresent` which manipulates mutable code with side effects.
 *   Whereas, Option does not expose such method and enforces the immutability and functional proramming paradigm.</li>
 * </ul>
 *
 * @param <T>
 */
public sealed interface Option<T> permits Some, None {

  /**
   * Returns an {@code Option} instance that wraps the given value. If the value is {@code null}
   * then an instance of {@link None} is returned.
   *
   * @param value the value to wrap
   * @param <T>   the type of the value
   * @return an {@code Option} instance
   */
  static <T> Option<T> of(T value) {
    return value == null ? new None<>() : new Some<>(value);
  }

  /**
   * Returns a {@code None} instance.
   *
   * @param <T> the type of the value
   * @return a {@code None} instance
   */
  static <T> Option<T> none() {
    return new None<>();
  }

  /**
   * Returns an {@link Option} instance from the given {@link Optional} instance.
   *
   * @param optional the optional to convert
   * @return an {@link Option} instance
   */
  static <T> Option<T> fromOptional(Optional<T> optional) {
    return optional.map(Option::of).orElseGet(Option::none);
  }

  /**
   * Returns the value if it exists, otherwise returns the given default value.
   *
   * @param defaultValue the default value to return if the value does not exist
   * @return the value if it exists, otherwise the default value
   */
  default T getOrElse(T defaultValue) {
    return switch (this) {
      case Some<T>(T value) -> value;
      case None<T> ignored -> defaultValue;
    };
  }

  /**
   * Returns whether the value exists.
   *
   * @return {@code true} if the value does not exist, otherwise {@code false}
   */
  default boolean isEmpty() {
    return this instanceof None;
  }

  /**
   * Returns whether the value exists.
   *
   * @return {@code true} if the value exists, otherwise {@code false}
   */
  default boolean isDefined() {
    return this instanceof Some;
  }

  /**
   * Applies the given predicate to the value if it the instance is a {@link Some} and returns a the
   * instance of {@link Some} if the predicate is satisfied, otherwise returns an instance of
   * {@link None}.
   *
   * @param predicate the predicate to apply to the value
   * @return an {@code Option} instance
   */
  default Option<T> filter(Predicate<T> predicate) {
    return switch (this) {
      case Some<T>(T value) -> predicate.test(value) ? this : new None<>();
      case None<T> ignored -> this;
    };
  }

  /**
   * Applies the first function if the instance is a {@link None} and the second function if the
   * instance is a {@link Some}.
   *
   * @param notFoundFunction the function to apply if the instance is a {@link None}
   * @param foundFunction    the function to apply if the instance is a {@link Some}
   * @param <U>              the type of the result
   * @return the result of the applied function
   */
  default <U> U fold(Supplier<U> notFoundFunction,
      Function<? super T, ? extends U> foundFunction) {
    Objects.requireNonNull(notFoundFunction);
    Objects.requireNonNull(foundFunction);
    return switch (this) {
      case Some<T>(T value) -> foundFunction.apply(value);
      case None<T> ignored -> notFoundFunction.get();
    };
  }

  /**
   * Maps the value to a new value using the given function if the instance is a {@link Some},
   * otherwise returns an instance of {@link None}.
   *
   * @param fn  the function to apply to the value
   * @param <U> the type of the new value
   * @return an {@code Option} instance
   */
  default <U> Option<U> map(Function<? super T, ? extends U> fn) {
    Objects.requireNonNull(fn);
    return switch (this) {
      case Some<T>(T value) -> Option.of(fn.apply(value));
      case None<T> ignored -> Option.none();
    };
  }

  /**
   * Flat Maps the value to a new value using the given function if the instance is a {@link Some},
   * otherwise returns an instance of {@link None}.
   *
   * @param fn  the function to apply to the value
   * @param <U> the type of the new value
   * @return an {@code Option} instance
   */
  default <U> Option<U> flatMap(Function<? super T, ? extends Option<U>> fn) {
    Objects.requireNonNull(fn);
    return switch (this) {
      case Some<T>(T value) -> fn.apply(value);
      case None<T> ignored -> Option.none();
    };
  }

  /**
   * Returns an instance of {@link Right} if the instance is a {@link Some}, otherwise returns an
   * instance of {@link Left}.
   *
   * @param supplier the supplier to provide the value for the left side
   * @param <X>      the type of the left side
   * @return an instance of {@link Either}
   */
  default <X> Either<X, T> toRight(Supplier<X> supplier) {
    Objects.requireNonNull(supplier);
    return switch (this) {
      case Some<T>(T value) -> Either.right(value);
      case None<T> ignored -> Either.left(supplier.get());
    };
  }

  /**
   * Returns an instance of {@link Left} if the instance is a {@link Some}, otherwise returns an
   * instance of {@link Right}.
   *
   * @param supplier the supplier to provide the value for the right side
   * @param <X>      the type of the right side
   * @return an instance of {@link Either}
   */
  default <X> Either<T, X> toLeft(Supplier<X> supplier) {
    Objects.requireNonNull(supplier);
    return switch (this) {
      case Some<T>(T value) -> Either.left(value);
      case None<T> ignored -> Either.right(supplier.get());
    };
  }

  /**
   * Returns a {@link Try} instance that wraps the value if the instance is a {@link Some},
   * otherwise returns a {@link Try} instance that wraps the exception provided by the supplier.
   *
   * @param exceptionSupplier the supplier to provide the exception
   * @return a {@link Try} instance
   */
  default Try<T> toTry(Supplier<Exception> exceptionSupplier) {
    Objects.requireNonNull(exceptionSupplier);
    return switch (this) {
      case Some<T>(T value) -> Try.success(value);
      case None<T> ignored -> Try.failure(exceptionSupplier.get());
    };
  }

  /**
   * Returns an {@link Optional} instance that wraps the value if the instance is a {@link Some},
   * otherwise returns an empty {@link Optional}.
   *
   * @return an {@link Optional} instance
   */
  default Optional<T> toJavaOptional() {
    return switch (this) {
      case Some<T>(T value) -> Optional.of(value);
      case None<T> ignored -> Optional.empty();
    };
  }

  /**
   * Returns the original Option if it is a {@link Some}, otherwise returns the Option provided by
   * the supplier.
   *
   * @param supplier the supplier to provide the Option
   * @return an {@code Option} instance
   */
  @SuppressWarnings("unchecked")
  default Option<T> or(Supplier<? extends Option<? extends T>> supplier) {
    Objects.requireNonNull(supplier);
    return switch (this) {
      case Some<T> ignored -> this;
      case None<T> ignored -> (Option<T>) supplier.get();
    };
  }

  default Either<Void, T> toEither() {
    return switch (this) {
      case Some<T> some -> Either.right(some.value());
      case None<T> ignored -> Either.left(null);
    };
  }

  default <A> Either<A, T> toEither(Supplier<A> supplier) {
    return switch (this) {
      case Some<T>(T value) -> Either.right(value);
      case None<T> ignored -> Either.left(supplier.get());
    };
  }

}
