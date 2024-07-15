package com.soumakis.safetype;

import com.soumakis.control.Try;
import org.junit.jupiter.api.Test;

public class PositiveTest {

  @Test
  void testPositive() {
    Try<Positive<Integer>> positive = Positive.of(42);
    assert (positive.get().getValue() == 42);
  }

  @Test
  void testPositiveFailure() {
    Try<Positive<Integer>> positive = Positive.of(-42);
    assert (positive.isFailure());
  }

  @Test
  void testPositiveFlatMap() {
    Try<Positive<Integer>> positive = Positive.of(42);
    Try<Positive<Double>> mapped = positive.flatMap(
        n -> Positive.of(n.getValue().doubleValue() + 0.5));
    assert (mapped.isSuccess());
    assert (mapped.get().getValue() == 42.5);
  }
}
