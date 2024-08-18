package com.soumakis.control;

import org.junit.jupiter.api.Test;

public class IOTest {

  @Test
  void testPure() {
    IO<String> io = IO.pure("Hello, World!");
    assert (io.unsafeRun().equals("Hello, World!"));
  }

  @Test
  void testMap() {
    IO<String> io = IO.pure("Hello, World!");
    IO<Integer> mapped = io.map(String::length);
    assert (mapped.unsafeRun() == 13);
  }

  @Test
  void testFlatMap() {
    IO<String> io = IO.pure("Hello, World!");
    IO<Integer> mapped = io.flatMap(s -> IO.pure(s.length()));
    assert (mapped.unsafeRun() == 13);
  }

  @Test
  void handleErrorWith() {
    IO<String> io = IO.of(() -> {
      throw new RuntimeException("Error");
    });
    IO<String> handled = io.handleErrorWith(e -> IO.pure("Handled"));
    assert (handled.unsafeRun().equals("Handled"));
  }

  @Test
  void testAttempt() {
    IO<String> io = IO.of(() -> {
      throw new RuntimeException("Error");
    });
    IO<Try<String>> attempted = io.attempt();
    assert (attempted.unsafeRun().isFailure());
  }

  @Test
  void testRedeem() {
    IO<String> io = IO.of(() -> {
      throw new RuntimeException("Error");
    });
    IO<String> recovered = io.redeem(e -> "Recovered", s -> s);
    assert (recovered.unsafeRun().equals("Recovered"));
  }

  @Test
  void testRedeemWith() {
    IO<String> io = IO.of(() -> {
      throw new RuntimeException("Error");
    });
    IO<String> recovered = io.redeemWith(e -> IO.pure("Recovered"), IO::pure);
    assert (recovered.unsafeRun().equals("Recovered"));
  }

  @Test
  void testRepeat() {
    IO<Integer> io = IO.pure(1 + 1);
    IO<Integer> repeated = io.repeat(3);
    assert (repeated.unsafeRun() == 2);
  }

}
