package com.soumakis;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class TryTest {

  @Test
  void testSuccess() {
    Try<Integer> success = Try.of(() -> 42);
    assert (success.isSuccess());
  }

  @Test
  void testFailure() {
    Try<Integer> failure = Try.of(() -> {
      throw new RuntimeException("error");
    });
    assert (failure.isFailure());
  }

  @Test
  void testGet() {
    Try<Integer> success = Try.of(() -> 42);
    assert (success.get().equals(42));
  }

  @Test
  void testGetFailure() {
    Try<Integer> failure = Try.of(() -> {
      throw new RuntimeException("error");
    });
    assertThrows(RuntimeException.class, failure::get);
  }

  @Test
  void testMap() {
    Try<Integer> success = Try.of(() -> 1);
    Try<Integer> mapped = success.map(n -> n + 1);
    assert (mapped.isSuccess());
    assert (mapped.get() == 2);
  }

  @Test
  void testFlatMap() {
    Try<Integer> success = Try.of(() -> 1);
    Try<Integer> mapped = success.flatMap(n -> Try.of(() -> n + 1));
    assert (mapped.isSuccess());
    assert (mapped.get() == 2);
  }

  @Test
  void testRecover() {
    Try<Integer> failure = Try.of(() -> {
      throw new RuntimeException("error");
    });
    Try<Integer> recovered = failure.recover(t -> 42);
    assert (recovered.isSuccess());
    assert (recovered.get() == 42);
  }

  @Test
  void testRecoverWith() {
    Try<Integer> failure = Try.of(() -> {
      throw new RuntimeException("error");
    });
    Try<Integer> recovered = failure.recoverWith(t -> Try.of(() -> 42));
    assert (recovered.isSuccess());
    assert (recovered.get() == 42);
  }

  @Test
  void testToOptional() {
    Try<Integer> success = Try.of(() -> 42);
    assert (success.toOptional().isPresent());
    assert (success.toOptional().get().equals(42));
  }

  @Test
  void testToOptionalFailure() {
    Try<Integer> failure = Try.of(() -> {
      throw new RuntimeException("error");
    });
    assert (failure.toOptional().isEmpty());
  }

  @Test
  void testGetOrElse() {
    Try<Integer> success = Try.of(() -> 42);
    assert (success.getOrElse(() -> 50).equals(42));
  }

  @Test
  void testGetOrElseFailure() {
    Try<Integer> failure = Try.of(() -> {
      throw new RuntimeException("error");
    });
    assert (failure.getOrElse(() -> 50).equals(50));
  }

  @Test
  void testFold() {
    Try<Integer> success = Try.of(() -> 42);
    String result = success.fold(
        error -> "new error",
        Object::toString
    );

    assert (result.equals("42"));
  }

  @Test
  void testFoldWithFailure() {
    Try<Integer> failure = Try.of(() -> {
      throw new RuntimeException("error");
    });
    String result = failure.fold(
        error -> "new error",
        Object::toString
    );

    assert (result.equals("new error"));
  }

  @Test
  void testOnFailure() {
    Try<Integer> failure = Try.of(() -> {
      throw new RuntimeException("error");
    });
    failure.onFailure(t -> {
      assert (t.getMessage().equals("error"));
    });
  }

  @Test
  void testPeek() {
    Try<Integer> success = Try.of(() -> 42);
    success.peek(n -> {
      assert (n == 42);
    });
  }
}
