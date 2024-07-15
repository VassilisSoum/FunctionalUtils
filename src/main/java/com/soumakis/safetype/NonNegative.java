package com.soumakis.safetype;

import com.soumakis.control.Try;

/**
 * Represents an non-negative numeric value. This class ensures that the numerical value it
 * encapsulates is strictly non-negative.
 *
 * <p>Example usage:</p>
 * <ul>
 *   <li>
 *     Safely handling results from libraries or remote calls you don't own:
 *     <pre>{@code
 *     public NonNegative doSomething() {
 *       NonNegative result = NonNegative.of(externalLibraryReturningPotentialNegativeNumber());
 *       // Complex things here
 *       return result;
 *     }
 *     }</pre>
 *   </li>
 *   <li>
 *     Modeling domain objects with strict constraints:
 *     <pre>{@code
 *     public record PaymentAmount(NonNegative amount) {
 *     }
 *     }</pre>
 *   </li>
 * </ul>
 */
public final class NonNegative<T extends Number & Comparable<T>> {

  private final T value;

  /**
   * Private constructor to prevent instantiation with negative values directly.
   *
   * @param value The numeric value to encapsulate. Must be non-negative.
   * @throws IllegalArgumentException if the provided value is negative.
   */
  private NonNegative(T value) {
    this.value = validateNonNegative(value);
  }

  /**
   * Factory method to create a {@link NonNegative} instance safely. This method returns a
   * {@link Try} instance, encapsulating either a successful creation of a {@link NonNegative} or an
   * exception if the input is not positive.
   *
   * @param value The numeric value to encapsulate. Can be negative, but will result in a failure.
   * @return A {@link Try} instance containing either a {@link NonNegative} or an exception.
   */
  public static <T extends Number & Comparable<T>> Try<NonNegative<T>> of(T value) {
    return Try.of(() -> new NonNegative<>(value));
  }

  /**
   * Factory method to create a {@link NonNegative} instance unsafely. This method throws an
   * IllegalArgumentException if the input is negative.
   *
   * @param value The numeric value to encapsulate. Must be non-negative.
   * @return A {@link NonNegative} instance.
   * @throws IllegalArgumentException if the provided value is negative.
   */
  public static <T extends Number & Comparable<T>> NonNegative<T> unsafeOf(T value) {
    return new NonNegative<>(value);
  }

  /**
   * Retrieves the encapsulated numeric value.
   *
   * @return The non-negative numeric value.
   */
  public T getValue() {
    return value;
  }

  /**
   * Returns the string representation of the encapsulated numeric value.
   *
   * @return The string representation of the non-negative numeric value.
   */
  @Override
  public String toString() {
    return String.valueOf(value);
  }

  private static <T extends Number & Comparable<T>> T validateNonNegative(T value) {
    switch (value) {
      case Byte b -> {
        if (b < 0) {
          throw new IllegalArgumentException("Value must be non negative");
        }
      }
      case Short s -> {
        if (s < 0) {
          throw new IllegalArgumentException("Value must be non negative");
        }
      }
      case Integer i -> {
        if (i < 0) {
          throw new IllegalArgumentException("Value must be non negative");
        }
      }
      case Long l -> {
        if (l < 0) {
          throw new IllegalArgumentException("Value must be non negative");
        }
      }
      case Float f -> {
        if (f < 0) {
          throw new IllegalArgumentException("Value must be non negative");
        }
      }
      case Double d -> {
        if (d < 0) {
          throw new IllegalArgumentException("Value must be non negative");
        }
      }
      default -> throw new IllegalArgumentException("Unsupported number type");
    }
    return value;
  }
}
