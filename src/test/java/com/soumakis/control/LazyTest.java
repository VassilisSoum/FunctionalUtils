package com.soumakis.control;

import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;

public class LazyTest {

  @Test
  void testLazy() {
    Lazy<Integer> lazy = Lazy.of(this::generateRandomNumber);
    int value = lazy.get();

    IntStream.range(0, 100).forEach(i -> {
      assert (lazy.get() == value);
    });
  }

  @Test
  void testMap() {
    Lazy<Integer> lazy = Lazy.of(this::generateRandomNumber);
    Lazy<Integer> mapped = lazy.map(i -> i * 2);
    int value = mapped.get();

    IntStream.range(0, 100).forEach(i -> {
      assert (mapped.get() == value);
    });
  }

  @Test
  void testFlatMap() {
    Lazy<Integer> lazy = Lazy.of(this::generateRandomNumber);
    Lazy<Integer> flatMapped = lazy.flatMap(i -> Lazy.of(() -> i * 2));
    int value = flatMapped.get();

    IntStream.range(0, 100).forEach(i -> {
      assert (flatMapped.get() == value);
    });
  }

  @Test
  void testEvaluated() {
    var randomNumber = generateRandomNumber();
    Lazy<Integer> lazy = Lazy.evaluated(randomNumber);
    assert (lazy.get() == randomNumber);
  }

  private int generateRandomNumber() {
    return (int) (Math.random() * 100);
  }
}
