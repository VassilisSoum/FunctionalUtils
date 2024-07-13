package com.soumakis;

/**
 * Represents the right side of the {@code Either} type which contains a value of type {@code B}.
 *
 * @param <L> the type of the value contained in the corresponding {@code Left}
 * @param <R> the type of the value contained in this {@code Right}
 */
public record Right<L, R>(R value) implements Either<L, R> {

}