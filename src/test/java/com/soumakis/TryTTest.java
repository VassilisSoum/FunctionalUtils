package com.soumakis;

import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.Test;

public class TryTTest {

  @Test
  void testFailure() {
    TryT<Integer> tryT = TryT.ofFailure(new RuntimeException());
    assert(tryT.toCompletableFuture().join().isFailure());
  }

  @Test
  void testSuccess() {
    TryT<Integer> tryT = TryT.of(42);
    assert(tryT.toCompletableFuture().join().isSuccess());
    assert(tryT.toCompletableFuture().join().get() == 42);
  }

  @Test
  void testFromFuture() {
    Try<Integer> tryValue = Try.success(42);
    TryT<Integer> tryT = TryT.fromFuture(CompletableFuture.completedFuture(tryValue));
    assert(tryT.toCompletableFuture().join().isSuccess());
    assert(tryT.toCompletableFuture().join().get() == 42);
  }

  @Test
  void testMap() {
    TryT<Integer> tryT = TryT.of(42);
    TryT<String> mapped = tryT.map(Object::toString);
    assert(mapped.toCompletableFuture().join().isSuccess());
    assert(mapped.toCompletableFuture().join().get().equalsIgnoreCase("42"));
  }

  @Test
  void testFlatMap() {
    TryT<Integer> tryT = TryT.of(42);
    TryT<String> mapped = tryT.flatMap(i -> TryT.of(i.toString()));
    assert(mapped.toCompletableFuture().join().isSuccess());
    assert(mapped.toCompletableFuture().join().get().equalsIgnoreCase("42"));
  }

  @Test
  void testRecover() {
    TryT<Integer> tryT = TryT.ofFailure(new RuntimeException());
    TryT<Integer> recovered = tryT.recover(e -> 42);
    assert(recovered.toCompletableFuture().join().isSuccess());
    assert(recovered.toCompletableFuture().join().get() == 42);
  }

}
