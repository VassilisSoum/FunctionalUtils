package com.soumakis.control;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * A monad that represents a value that can be either valid or invalid. Very useful for validation
 * and accumulating errors instead of short-circuiting.
 *
 * <p>Example usage:</p>
 *
 * <pre>{@code
 * public class ValidatedExample {
 *
 *   public static void main(String[] args) {
 *     Validated<String, String> validName = validateName("John");
 *     Validated<String, Integer> validAge = validateAge(25);
 *     Validated<String, String> validCity = validateCity("New York");
 *
 *     List<Validated<String, ?>> validatedList = Arrays.asList(validName, validAge, validCity);
 *
 *     Validated<List<String>, Person> validatedPerson = Validated.mapN(values -> {
 *       String name = (String) values.get(0);
 *       int age = (int) values.get(1);
 *       String city = (String) values.get(2);
 *       return new Person(name, age, city);
 *     }, validatedList);
 *
 *     if (validatedPerson instanceof Valid) {
 *       Person person = ((Valid<List<String>, Person>) validatedPerson).value();
 *       System.out.println("Valid person: " + person);
 *     } else {
 *       List<String> errors = ((Invalid<List<String>, Person>) validatedPerson).error();
 *       System.out.println("Errors: " + errors);
 *     }
 *   }
 *
 *   static class Person {
 *     final String name;
 *     final int age;
 *     final String city;
 *
 *     Person(String name, int age, String city) {
 *       this.name = name;
 *       this.age = age;
 *       this.city = city;
 *     }
 *
 *     @Override
 *     public String toString() {
 *       return "Person{name='" + name + "', age=" + age + ", city='" + city + "'}";
 *     }
 *   }
 *
 *   static Validated<String, String> validateName(String name) {
 *     if (name != null && !name.trim().isEmpty()) {
 *       return Validated.valid(name);
 *     } else {
 *       return Validated.invalid("Name cannot be empty");
 *     }
 *   }
 *
 *   static Validated<String, Integer> validateAge(int age) {
 *     if (age >= 18) {
 *       return Validated.valid(age);
 *     } else {
 *       return Validated.invalid("Age must be at least 18");
 *     }
 *   }
 *
 *   static Validated<String, String> validateCity(String city) {
 *     if (city != null && !city.trim().isEmpty()) {
 *       return Validated.valid(city);
 *     } else {
 *       return Validated.invalid("City cannot be empty");
 *     }
 *   }
 * }
 * }</pre>
 *
 * @param <E> The type of the error
 * @param <A> The type of the value
 */

public sealed interface Validated<E, A> permits Valid, Invalid {

  /**
   * Create a new Valid instance.
   *
   * @param value The value to wrap
   * @param <E>   The type of the error
   * @param <A>   The type of the value
   * @return A new Valid instance
   */
  static <E, A> Validated<E, A> valid(A value) {
    return new Valid<>(value);
  }

  /**
   * Create a new Invalid instance.
   *
   * @param error The error to wrap
   * @param <E>   The type of the error
   * @param <A>   The type of the value
   * @return A new Invalid instance
   */
  static <E, A> Validated<E, A> invalid(E error) {
    return new Invalid<>(error);
  }

  /**
   * Combines two or more {@code Validated} instances into a single instance. If all instances are
   * valid, the result is a valid instance with a list of values. If any instance is invalid, the
   * result is an invalid instance with a list of errors.
   *
   * @param validatedList a list of {@code Validated} instances
   * @param fn            a function that takes a list of values and returns a new value
   * @param <E>           the type of the error
   * @param <B>           the new type of the value
   * @return a new {@code Validated} instance
   */
  @SuppressWarnings("unchecked")
  static <E, B> Validated<List<E>, B> mapN(
      Function<List<Object>, B> fn, List<Validated<E, ?>> validatedList) {
    List<Object> values = new ArrayList<>();
    List<E> errors = new ArrayList<>();

    for (Validated<E, ?> validated : validatedList) {
      if (validated instanceof Valid) {
        values.add(((Valid<E, ?>) validated).value());
      } else {
        errors.add(((Invalid<E, ?>) validated).error());
      }
    }

    if (errors.isEmpty()) {
      return Validated.valid(fn.apply(values));
    } else {
      return Validated.invalid(errors);
    }
  }

  /**
   * Check if the instance is valid.
   *
   * @return true if the instance is valid, false otherwise
   */
  default boolean isValid() {
    return this instanceof Valid;
  }

  /**
   * Check if the instance is invalid.
   *
   * @return true if the instance is invalid, false otherwise
   */
  default boolean isInvalid() {
    return this instanceof Invalid;
  }

  /**
   * Get the error if the instance is invalid.
   *
   * @return The error
   */
  default E getInvalid() {
    return ((Invalid<E, A>) this).error();
  }

  /**
   * Get the value if the instance is valid.
   *
   * @return The value
   */
  default A getValid() {
    return ((Valid<E, A>) this).value();
  }

  /**
   * Map the value if the instance is valid.
   *
   * @param fn  The function to apply
   * @param <B> The new type of the value
   * @return A new Valid instance with the new value
   */
  default <B> Validated<E, B> map(Function<? super A, ? extends B> fn) {
    if (isInvalid()) {
      return Validated.invalid(getInvalid());
    }
    return Validated.valid(fn.apply(getValid()));
  }

  /**
   * FlatMap the error if the instance is invalid.
   *
   * @param fn  The function to apply
   * @param <B> The new type of the error
   * @return A new Invalid instance with the new error
   */
  default <B> Validated<E, B> flatMap(Function<? super A, ? extends Validated<E, B>> fn) {
    if (isInvalid()) {
      return Validated.invalid(getInvalid());
    }
    return fn.apply(getValid());
  }

  /**
   * Apply the first function if the instance is invalid, the second function if the instance is
   * valid.
   *
   * @param invalidFn the function to apply if the instance is invalid.
   * @param validFn   the function to apply if the instance is valid.
   * @param <L>       The new type of the error
   * @param <B>       The new type of the value
   * @return the result of applying the appropriate function.
   */
  default <L, B> Validated<L, B> bimap(Function<? super E, ? extends L> invalidFn,
      Function<? super A, ? extends B> validFn) {
    if (isInvalid()) {
      return Validated.invalid(invalidFn.apply(getInvalid()));
    }
    return Validated.valid(validFn.apply(getValid()));
  }

  /**
   * Applies a function to the value inside {@code Validated}, depending on whether the instance is
   * {@code Invalid} or {@code Valid}.
   *
   * @param invalidFn the function to apply if the instance is invalid.
   * @param validFn   the function to apply if the instance is valid.
   * @param <B>       The new type of the value
   * @return the result of applying the appropriate function.
   */
  default <B> B fold(Function<? super E, ? extends B> invalidFn,
      Function<? super A, ? extends B> validFn) {
    if (isInvalid()) {
      return invalidFn.apply(getInvalid());
    }
    return validFn.apply(getValid());
  }

  /**
   * Get the value if the instance is valid, or the other value if the instance is invalid.
   *
   * @param other The value to return if the instance is invalid.
   * @return The value if the instance is valid, or the other value if the instance is invalid.
   */
  default A getOrElse(A other) {
    if (isInvalid()) {
      return other;
    }
    return getValid();
  }

  /**
   * Swaps the sides of this {@code Validated}. If this is {@code Valid}, then the returned instance
   * will be {@code Invalid} with the same value and vice versa.
   *
   * @return A new instance with the value and the error swapped.
   */
  default Validated<A, E> swap() {
    if (isInvalid()) {
      return Validated.valid(getInvalid());
    }
    return Validated.invalid(getValid());
  }

  /**
   * Converts the instance to an {@code Either}.
   *
   * @return An {@code Either} instance.
   */
  default Either<E, A> toEither() {
    if (isInvalid()) {
      return Either.left(getInvalid());
    }
    return Either.right(getValid());
  }

  /**
   * Converts the instance to an {@code Optional}.
   *
   * @return An {@code Optional} instance.
   */
  default Optional<A> toOptional() {
    if (isInvalid()) {
      return Optional.empty();
    }
    return Optional.of(getValid());
  }
}

