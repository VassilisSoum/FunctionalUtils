package com.soumakis.safetype;

import com.soumakis.control.Try;

/**
 * Represents a positive numeric value. This class ensures that the numerical value it encapsulates
 * is strictly positive.
 *
 * <p>Example usage:</p>
 * <ul>
 *   <li>
 *     Safely handling results from libraries or remote calls you don't own:
 *     <pre>{@code
 *     public Positive doSomething() {
 *       Positive result = Positive.of(externalLibraryReturningPotentialNegativeNumber());
 *       // Complex things here
 *       return result;
 *     }
 *     }</pre>
 *   </li>
 *   <li>
 *     Modeling domain objects with strict constraints:
 *     <pre>{@code
 *     public record Balance(Positive amount) {
 *     }
 *     }</pre>
 *   </li>
 *   </ul>
 */
public final class Positive<T extends Number & Comparable<T>> {

  private final T value;

  /**
   * Private constructor to prevent instantiation with non-positive values directly.
   *
   * @param value The numeric value to encapsulate. Must be positive.
   * @throws IllegalArgumentException if the provided value is not positive.
   */
  private Positive(T value) {
    this.value = validatePositive(value);
  }

  /**
   * Factory method to create a {@link Positive} instance safely. This method returns a {@link Try}
   * instance, encapsulating either a successful creation of a {@link Positive} or an exception if
   * the input is not positive.
   *
   * @param value The numeric value to encapsulate. Can be non-positive, but will result in a
   *              failure.
   * @return A {@link Try} instance containing either a {@link Positive} or an exception.
   */
  public static <T extends Number & Comparable<T>> Try<Positive<T>> of(T value) {
    return Try.of(() -> new Positive<>(value));
  }

  /**
   * Factory method to create a {@link Positive} instance unsafely. This method throws an
   * IllegalArgumentException if the input is not positive.
   *
   * @param value The numeric value to encapsulate. Must be positive.
   * @return A {@link Positive} instance.
   * @throws IllegalArgumentException if the provided value is not positive.
   */
  public static <T extends Number & Comparable<T>> Positive<T> unsafeOf(T value) {
    return new Positive<>(value);
  }

  /**
   * Retrieves the encapsulated numeric value.
   *
   * @return The positive numeric value.
   */
  public T getValue() {
    return value;
  }

  /**
   * Returns the string representation of the encapsulated numeric value.
   *
   * @return The string representation of the positive numeric value.
   */
  @Override
  public String toString() {
    return String.valueOf(value);
  }

  private static <T extends Number & Comparable<T>> T validatePositive(T value) {
    switch (value) {
      case Byte b -> {
        if (b <= 0) {
          throw new IllegalArgumentException("Value must be positive");
        }
      }
      case Short s -> {
        if (s <= 0) {
          throw new IllegalArgumentException("Value must be positive");
        }
      }
      case Integer i -> {
        if (i <= 0) {
          throw new IllegalArgumentException("Value must be positive");
        }
      }
      case Long l -> {
        if (l <= 0) {
          throw new IllegalArgumentException("Value must be positive");
        }
      }
      case Float f -> {
        if (f <= 0) {
          throw new IllegalArgumentException("Value must be positive");
        }
      }
      case Double d -> {
        if (d <= 0) {
          throw new IllegalArgumentException("Value must be positive");
        }
      }
      default -> throw new IllegalArgumentException("Unsupported number type");
    }
    return value;
  }
}
