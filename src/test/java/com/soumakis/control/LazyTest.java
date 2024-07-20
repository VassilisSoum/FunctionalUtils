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

  private int generateRandomNumber() {
    return (int) (Math.random() * 100);
  }
}
