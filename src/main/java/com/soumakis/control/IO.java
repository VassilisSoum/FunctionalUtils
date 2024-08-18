package com.soumakis.control;

import com.soumakis.safetype.ThrowingSupplier;
import java.util.function.Function;

/**
 * The {@code IO} class represents a computation that can be performed later, which may produce side
 * effects. It encapsulates the computation and delays its execution until explicitly invoked using
 * the {@code unsafeRun()} method. This allows you to manage side effects in a controlled manner,
 * making your code more predictable and easier to reason about.
 *
 * <p>The {@code IO} monad provides methods for composing and transforming computations
 * (`map`, `flatMap`), handling errors (`handleErrorWith`, `redeem`, `redeemWith`), and attempting
 * computations (`attempt`). These features enable the creation of complex computation pipelines
 * that can safely and consistently manage side effects and errors.
 *
 * <h2>Why is IO Considered Referentially Transparent and Pure?</h2>
 * <p>In functional programming, a function is considered <em>referentially transparent</em> if it
 * can be replaced with its corresponding value without changing the program's behavior. This
 * property allows functions to be predictable and composable. The {@code IO} class is referentially
 * transparent because it doesn't actually perform its computation when the {@code IO} instance is
 * created. Instead, it simply describes the computation. This description can be safely passed
 * around, composed, and transformed without side effects occurring.
 *
 * <p>The {@code IO} class is also considered <em>pure</em> because it separates the description of
 * a computation from its execution. The computation is only executed when you explicitly call the
 * {@code unsafeRun()} method, allowing you to control when and how side effects are performed. This
 * design ensures that side effects do not occur during the construction or transformation of
 * {@code IO} instances, which aligns with the principles of pure functional programming.
 *
 * <p>By deferring side effects and providing a controlled execution model, {@code IO} enables you
 * to write code that is easier to reason about, test, and maintain. This separation of
 * concerns—where the description of the computation is pure and referentially transparent, and the
 * execution is explicitly controlled—underpins the power and utility of the {@code IO} monad in
 * functional programming.
 *
 * <b>An important note is that the current implementation does not support asynchronous effects
 * nor
 * does it support stack safety for the flatMap operation</b>
 *
 * @param <A> The type of the value produced by the computation.
 */
public class IO<A> {

  private final ThrowingSupplier<A> computation;

  /**
   * Private constructor to create an {@code IO} instance.
   *
   * @param computation A supplier function that performs the computation and may throw an
   *                    exception.
   */
  private IO(ThrowingSupplier<A> computation) {
    this.computation = computation;
  }

  /**
   * Creates an {@code IO} instance from a computation that produces a value of type {@code A}.
   *
   * @param computation A supplier function that performs the computation and may throw an
   *                    exception.
   * @param <A>         The type of the value produced by the computation.
   * @return An {@code IO} instance encapsulating the computation.
   */
  public static <A> IO<A> of(ThrowingSupplier<A> computation) {
    return new IO<>(computation);
  }

  /**
   * Creates an {@code IO} instance that immediately returns a pure value of type {@code A}.
   *
   * @param value The value to be wrapped in the {@code IO}.
   * @param <A>   The type of the value.
   * @return An {@code IO} instance that returns the provided value without any computation.
   */
  public static <A> IO<A> pure(A value) {
    return new IO<>(() -> value);
  }

  /**
   * Transforms the result of this {@code IO} by applying the given function to it. This allows you
   * to map the output of the computation to a new type {@code B}.
   *
   * @param mapperFn A function that takes the result of the computation and produces a new value of
   *                 type {@code B}.
   * @param <B>      The type of the result after applying the mapping function.
   * @return A new {@code IO} instance containing the transformed result.
   */
  public <B> IO<B> map(Function<A, B> mapperFn) {
    return new IO<>(() -> mapperFn.apply(unsafeRun()));
  }

  /**
   * Chains another {@code IO} computation to this one. The result of the first computation is
   * passed to the given function, which produces a new {@code IO} instance. This is useful for
   * sequencing operations that depend on each other.
   *
   * @param mapperFn A function that takes the result of the first computation and returns a new
   *                 {@code IO}.
   * @param <B>      The type of the result of the subsequent computation.
   * @return A new {@code IO} instance representing the combined computation.
   */
  public <B> IO<B> flatMap(Function<A, IO<B>> mapperFn) {
    return new IO<>(() -> mapperFn.apply(unsafeRun()).unsafeRun());
  }

  /**
   * Provides a way to handle errors that may occur during the computation. If an error occurs, the
   * provided handler function is used to recover and produce a new {@code IO} instance.
   *
   * @param handler A function that takes the thrown exception and returns a new {@code IO}
   *                instance.
   * @return A new {@code IO} instance that handles errors using the provided handler.
   */
  public IO<A> handleErrorWith(Function<Throwable, IO<A>> handler) {
    return new IO<>(() -> {
      try {
        return unsafeRun();
      } catch (Throwable t) {
        return handler.apply(t).unsafeRun();
      }
    });
  }

  /**
   * Attempts to run the computation and captures any error that occurs, returning it in a
   * {@code Try} wrapper. This allows you to safely inspect whether the computation succeeded or
   * failed.
   *
   * @return A new {@code IO} instance containing the result wrapped in a {@code Try}, indicating
   * success or failure.
   */
  public IO<Try<A>> attempt() {
    return new IO<>(() -> Try.of(this::unsafeRun));
  }

  /**
   * Transforms the result of this {@code IO} based on whether it succeeds or fails. If the
   * computation succeeds, the success handler is applied; if it fails, the error handler is
   * applied.
   *
   * @param errorHandlerFn   A function that handles the error and produces a fallback value.
   * @param successHandlerFn A function that processes the successful result.
   * @return A new {@code IO} instance with the transformed result.
   */
  public IO<A> redeem(Function<Throwable, A> errorHandlerFn, Function<A, A> successHandlerFn) {
    return new IO<>(() -> {
      try {
        return successHandlerFn.apply(unsafeRun());
      } catch (Throwable t) {
        return errorHandlerFn.apply(t);
      }
    });
  }

  /**
   * Similar to {@code redeem}, but the error and success handlers return new {@code IO} instances,
   * allowing for more complex or effectful transformations based on success or failure.
   *
   * @param errorHandlerFn   A function that handles the error and returns a new {@code IO}
   *                         instance.
   * @param successHandlerFn A function that processes the successful result and returns a new
   *                         {@code IO} instance.
   * @return A new {@code IO} instance that applies the appropriate handler based on the outcome of
   * the computation.
   */
  public IO<A> redeemWith(Function<Throwable, IO<A>> errorHandlerFn,
      Function<A, IO<A>> successHandlerFn) {
    return new IO<>(() -> {
      try {
        return successHandlerFn.apply(unsafeRun()).unsafeRun();
      } catch (Throwable t) {
        return errorHandlerFn.apply(t).unsafeRun();
      }
    });
  }

  /**
   * Repeats the computation a specified number of times, returning the result of the last
   * iteration. This is useful for performing a side effect multiple times.
   *
   * @param times The number of times to repeat the computation.
   * @return A new {@code IO} instance that repeats the computation the specified number of times.
   * @throws StackOverflowError if the number of repetitions exceeds the stack size.
   */
  public IO<A> repeat(int times) {
    return new IO<>(() -> {
      A result = null;
      for (int i = 0; i < times; i++) {
        result = unsafeRun();
      }
      return result;
    });
  }

  /**
   * Creates an {@code IO} instance from a {@code Try} value. If the {@code Try} is a success, the
   * computation will return the value; if it is a failure, the computation will throw the
   * exception.
   * @param tryValue The {@code Try} value to convert to an {@code IO}.
   * @return An {@code IO} instance that encapsulates the {@code Try} value.
   */
  public IO<A> fromTry(Try<A> tryValue) {
    return new IO<>(tryValue::get);
  }

  /**
   * Executes the encapsulated computation and returns the result.
   * <b>Warning:</b> This method performs the side effects and should be used cautiously,
   * as it breaks the functional purity and referential transparency of the code.
   *
   * @return The result of the computation.
   */
  public A unsafeRun() {
    return computation.get();
  }
}
