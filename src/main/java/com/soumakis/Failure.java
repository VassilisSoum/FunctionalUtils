package com.soumakis;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public record Failure<T>(Throwable throwable) implements Try<T> {

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