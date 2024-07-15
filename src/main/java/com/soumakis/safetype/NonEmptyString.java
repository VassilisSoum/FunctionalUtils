package com.soumakis.safetype;

import com.soumakis.control.Try;

/**
 * Represents a non-empty string. This class ensures that the string value it encapsulates is not
 * null and not empty.
 *
 * <p>Example usage:</p>
 * <ul>
 *   <li>
 *     Safely handling results from libraries or remote calls you don't own:
 *     <pre>{@code
 *     public NonEmptyString doSomething() {
 *       NonEmptyString result = NonEmptyString.of(externalLibraryReturningPotentialNullOrEmptyString());
 *       // Complex things here
 *       return result;
 *     }
 *     }</pre>
 *   </li>
 *   <li>
 *     Modeling domain objects with strict constraints:
 *     <pre>{@code
 *     public record User(NonEmptyString username, NonEmptyString password) {
 *     }
 *     }</pre>
 *   </li>
 * </ul>
 */
public final class NonEmptyString {

  private final String value;

  /**
   * Private constructor to prevent instantiation with invalid strings directly.
   *
   * @param value The string value to encapsulate. Must not be null or empty.
   * @throws IllegalArgumentException if the provided string is null or empty.
   */
  private NonEmptyString(String value) {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException("String must not be empty");
    }
    this.value = value;
  }

  /**
   * Factory method to create a {@link NonEmptyString} instance safely. This method returns a
   * {@link Try} instance, encapsulating either a successful creation of a {@link NonEmptyString} or
   * an exception if the input is invalid.
   *
   * @param value The string value to encapsulate.
   * @return A {@link Try} instance containing either a {@link NonEmptyString} or an exception.
   */
  public static Try<NonEmptyString> of(String value) {
    return Try.of(() -> new NonEmptyString(value));
  }

  /**
   * Factory method to create a {@link NonEmptyString} instance unsafely especially when the value
   * is known at compile time. This method throws an IllegalArgumentException if the input is
   * invalid.
   *
   * @param value The string value to encapsulate. Must not be null or empty.
   * @return A {@link NonEmptyString} instance.
   * @throws IllegalArgumentException if the provided string is null or empty.
   */
  public static NonEmptyString unsafeOf(String value) {
    return new NonEmptyString(value);
  }

  /**
   * Concatenates the encapsulated string value with another non-empty string.
   *
   * @param other The non-empty string to concatenate with.
   * @return A new {@link NonEmptyString} instance containing the concatenated value.
   */
  public NonEmptyString concat(NonEmptyString other) {
    return new NonEmptyString(value + other.value);
  }


  /**
   * Retrieves the encapsulated string value.
   *
   * @return The non-null, non-empty string value.
   */
  public String getValue() {
    return value;
  }

  /**
   * Returns the string representation of the encapsulated value.
   *
   * @return The non-null, non-empty string value.
   */
  @Override
  public String toString() {
    return value;
  }
}