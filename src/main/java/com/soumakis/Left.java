package com.soumakis;

/**
 * Represents the left side of the {@code Either} type which contains a value of type {@code A}.
 *
 * @param <L> the type of the value contained in this {@code Left}
 * @param <R> the type of the value contained in the corresponding {@code Right}
 */
public record Left<L, R>(L value) implements Either<L, R> {

}