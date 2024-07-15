package com.soumakis.safetype;

import com.soumakis.control.Try;
import org.junit.jupiter.api.Test;

public class NonNegativeTest {

  @Test
  void testNonNegative() {
    Try<NonNegative<Integer>> nonNegative = NonNegative.of(42);
    assert (nonNegative.get().getValue() == 42);
  }

  @Test
  void testNonNegativeFailure() {
    Try<NonNegative<Integer>> nonNegative = NonNegative.of(-42);
    assert (nonNegative.isFailure());
  }

  @Test
  void testNonNegativeFlatMap() {
    Try<NonNegative<Integer>> nonNegative = NonNegative.of(42);
    Try<NonNegative<Double>> mapped = nonNegative.flatMap(
        n -> NonNegative.of(n.getValue().doubleValue() + 0.5));
    assert (mapped.isSuccess());
    assert (mapped.get().getValue() == 42.5);
  }
}
