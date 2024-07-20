package com.soumakis.control.util;

import static com.soumakis.control.util.ForComprehension.For2;

import com.soumakis.control.Either;
import com.soumakis.control.Try;
import org.junit.jupiter.api.Test;

public class ForComprehensionTest {

  @Test
  void testFor2() {
    Either<String, Integer> e1 = Either.right(1);
    Either<String, Double> e2 = Either.right(2.0d);

    Either<String, Double> result = For2(e1, e2, (i, d) -> i + d);

    assert (result.getRight() == 3.0d);
  }

  @Test
  void testFor2Left() {
    Either<String, Integer> e1 = Either.left("error");
    Either<String, Double> e2 = Either.right(2.0d);

    Either<String, Double> result = For2(e1, e2, (i, d) -> i + d);

    assert (result.getLeft().equals("error"));
  }

  @Test
  void testFor3() {
    Either<String, Integer> e1 = Either.right(1);
    Either<String, Double> e2 = Either.right(2.0d);
    Either<String, String> e3 = Either.right("3");

    Either<String, String> result = ForComprehension.For3(e1, e2, e3, (i, d, s) -> i + d + s);

    assert (result.getRight().equals("3.03"));
  }

  @Test
  void testFor3Left() {
    Either<String, Integer> e1 = Either.left("error");
    Either<String, Double> e2 = Either.right(2.0d);
    Either<String, String> e3 = Either.right("3");

    Either<String, String> result = ForComprehension.For3(e1, e2, e3, (i, d, s) -> i + d + s);

    assert (result.getLeft().equals("error"));
  }

  @Test
  void testFor2TrySuccess() {
    Try<Integer> t1 = Try.success(1);
    Try<Double> t2 = Try.success(2.0d);
    Try<Double> result = ForComprehension.For2(t1, t2, (i, d) -> i + d);
    assert (result.get() == 3.0d);
  }

  @Test
  void testFor2TryFailure() {
    Try<Integer> t1 = Try.failure(new RuntimeException("error"));
    Try<Double> t2 = Try.success(2.0d);
    Try<Double> result = ForComprehension.For2(t1, t2, (i, d) -> i + d);
    assert (result.isFailure());
  }

}
