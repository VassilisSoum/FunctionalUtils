package com.soumakis.control;

import java.util.function.Function;

/**
 * Reader monad implementation. This is a simple implementation of the Reader monad. It is used to
 * pass a context to a computation. The computation is a function that takes the context as an
 * argument and returns a value. The Reader monad allows us to chain computations that depend on the
 * context without passing the context explicitly.
 *
 * @param <C> the context type
 * @param <T> the result type
 */
public class Reader<C, T> {

  private final Function<C, T> computation;

  private Reader(Function<C, T> computation) {
    this.computation = computation;
  }

  /**
   * Runs the computation with the given context.
   *
   * @param context the context
   * @return the result of the computation
   */
  public T run(C context) {
    return computation.apply(context);
  }

  public static <C, T> Reader<C, T> of(Function<C, T> computation) {
    return new Reader<>(computation);
  }

}
