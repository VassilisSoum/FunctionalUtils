package com.soumakis;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public record Success<T>(T value) implements Try<T> {

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