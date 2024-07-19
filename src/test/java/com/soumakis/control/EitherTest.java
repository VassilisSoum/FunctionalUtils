package com.soumakis.control;

import org.junit.jupiter.api.Test;

public class EitherTest {

  @Test
  void testLeft() {
    Either<String, Integer> either = Either.left("error");
    assert (either.isLeft());
    assert (!either.isRight());
  }

  @Test
  void testRight() {
    Either<String, Integer> either = Either.right(42);
    assert (!either.isLeft());
    assert (either.isRight());
  }

  @Test
  void testGetLeft() {
    Either<String, Integer> either = Either.left("error");
    assert (either.getLeft().equals("error"));
  }

  @Test
  void testGetRight() {
    Either<String, Integer> either = Either.right(42);
    assert (either.getRight().equals(42));
  }

  @Test
  void testMap() {
    Either<String, Integer> either = Either.right(1);
    Either<String, Integer> mapped = either.map(n -> n + 1);
    assert (mapped.isRight());
    assert (mapped.getRight() == 2);
  }

  @Test
  void testFlatMap() {
    Either<String, Integer> either = Either.right(1);
    Either<String, Integer> mapped = either.flatMap(n -> Either.right(n + 1));
    assert (mapped.isRight());
    assert (mapped.getRight() == 2);
  }

  @Test
  void testFoldWithLeft() {
    Either<String, Integer> left = Either.left("error");
    String result = left.fold(
        error -> "new error",
        Object::toString
    );

    assert (result.equals("new error"));
  }

  @Test
  void testFoldWithRight() {
    Either<String, Integer> right = Either.right(42);
    String result = right.fold(
        error -> "new error",
        Object::toString
    );

    assert (result.equals("42"));
  }

  @Test
  void testSwap() {
    Either<String, Integer> left = Either.left("error");
    Either<Integer, String> swapped = left.swap();
    assert (swapped.isRight());
    assert (swapped.getRight().equals("error"));
  }

  @Test
  void testGetOrElse() {
    Either<String, Integer> left = Either.left("error");
    Integer result = left.getOrElse(42);
    assert (result == 42);
  }

  @Test
  void testGetOrElseGet() {
    Either<String, Integer> left = Either.left("error");
    Integer result = left.getOrElseGet(error -> 42);
    assert (result == 42);
  }

  @Test
  void testAndThen() {
    Either<String, Integer> left = Either.left("error");
    Either<String, Integer> right = Either.right(42);
    Either<String, Integer> result = left.andThen(right);
    assert (result.isLeft());
    assert (result.getLeft().equals("error"));
  }

  @Test
  void testGetOrElseThrow() {
    Either<String, Integer> left = Either.left("error");
    try {
      left.getOrElseThrow(RuntimeException::new);
    } catch (RuntimeException e) {
      assert (e.getMessage().equals("error"));
    }
  }

  @Test
  void testToOptional() {
    Either<String, Integer> left = Either.left("error");
    assert (left.toOptional().isEmpty());
    Either<String, Integer> right = Either.right(42);
    assert (right.toOptional().isPresent());
    assert (right.toOptional().get() == 42);
  }

  @Test
  void testLeftMap() {
    Either<String, Integer> left = Either.left("error");
    Either<Integer, Integer> mapped = left.leftMap(String::length);
    assert (mapped.isLeft());
    assert (mapped.getLeft() == 5);
  }

  @Test
  void testFromTryCatch() {
    Either<IllegalStateException, Integer> either = Either.fromTryCatch(
        () -> Integer.parseInt("number"), ex -> new IllegalStateException());

    assert (either.isLeft());
    assert (either.getLeft() != null);
  }

}
