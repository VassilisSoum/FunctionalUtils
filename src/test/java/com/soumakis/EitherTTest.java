package com.soumakis;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.Test;

public class EitherTTest {

  @Test
  void testLeft() throws ExecutionException, InterruptedException {
    EitherT<String, Integer> eitherT = EitherT.left("error");
    assert (eitherT.toCompletableFuture().get().isLeft());
    assert (eitherT.toCompletableFuture().get().getLeft().equalsIgnoreCase("error"));
  }

  @Test
  void testRight() throws ExecutionException, InterruptedException {
    EitherT<String, Integer> eitherT = EitherT.right(42);
    assert (eitherT.toCompletableFuture().get().isRight());
    assert (eitherT.toCompletableFuture().get().getRight() == 42);
  }

  @Test
  void testFromFuture() throws ExecutionException, InterruptedException {
    Either<String, Integer> either = Either.right(42);
    EitherT<String, Integer> eitherT = EitherT.fromFuture(
        CompletableFuture.completedFuture(either));
    assert (eitherT.toCompletableFuture().get().isRight());
    assert (eitherT.toCompletableFuture().get().getRight() == 42);
  }

  @Test
  void testMap() throws ExecutionException, InterruptedException {
    EitherT<String, Integer> eitherT = EitherT.right(42);
    EitherT<String, String> mapped = eitherT.map(Object::toString);
    assert (mapped.toCompletableFuture().get().isRight());
    assert (mapped.toCompletableFuture().get().getRight().equalsIgnoreCase("42"));
  }

  @Test
  void testFlatMap() throws ExecutionException, InterruptedException {
    EitherT<String, Integer> eitherT = EitherT.right(42);
    EitherT<String, String> mapped = eitherT.flatMap(i -> EitherT.right(i.toString()));
    assert (mapped.toCompletableFuture().get().isRight());
    assert (mapped.toCompletableFuture().get().getRight().equalsIgnoreCase("42"));
  }

  @Test
  void testBiFlatMap() throws ExecutionException, InterruptedException {
    EitherT<String, Integer> eitherT = EitherT.right(42);
    EitherT<String, String> mapped = eitherT.biFlatMap(
        left -> EitherT.left("error"),
        right -> EitherT.right(right.toString()));
    assert (mapped.toCompletableFuture().get().isRight());
    assert (mapped.toCompletableFuture().get().getRight().equalsIgnoreCase("42"));
  }

  @Test
  void testBiMap() throws ExecutionException, InterruptedException {
    EitherT<String, Integer> eitherT = EitherT.right(42);
    EitherT<String, String> mapped = eitherT.biMap(
        left -> "error",
        Object::toString);
    assert (mapped.toCompletableFuture().get().isRight());
    assert (mapped.toCompletableFuture().get().getRight().equalsIgnoreCase("42"));
  }

  @Test
  void testBiMapLeft() throws ExecutionException, InterruptedException {
    EitherT<String, Integer> eitherT = EitherT.left("error");
    EitherT<String, String> mapped = eitherT.biMap(
        left -> "error",
        Object::toString);
    assert (mapped.toCompletableFuture().get().isLeft());
    assert (mapped.toCompletableFuture().get().getLeft().equalsIgnoreCase("error"));
  }

  @Test
  void testLeftMap() throws ExecutionException, InterruptedException {
    EitherT<String, Integer> eitherT = EitherT.left("error");
    EitherT<Integer, Integer> mapped = eitherT.leftMap(value -> 10);
    assert (mapped.toCompletableFuture().get().isLeft());
    assert (mapped.toCompletableFuture().get().getLeft() == 10);
  }

  @Test
  void testLeftMapRight() throws ExecutionException, InterruptedException {
    EitherT<String, Integer> eitherT = EitherT.right(42);
    EitherT<Integer, Integer> mapped = eitherT.leftMap(value -> 10);
    assert (mapped.toCompletableFuture().get().isRight());
    assert (mapped.toCompletableFuture().get().getRight() == 42);
  }

  @Test
  void testRecover() {
    EitherT<String, Integer> eitherT = EitherT.left("error");
    EitherT<String, Integer> recovered = eitherT.recoverWith(error -> EitherT.right(42));
    assert (recovered.toCompletableFuture().join().isRight());
    assert (recovered.toCompletableFuture().join().getRight() == 42);
  }

  @Test
  void testToTryT() {
    EitherT<String, Integer> eitherT = EitherT.right(42);
    TryT<Integer> tryT = eitherT.toTryT();
    assert (tryT.toCompletableFuture().join().isSuccess());
    assert (tryT.toCompletableFuture().join().get() == 42);
  }

  @Test
  void testToTryTLeft() {
    EitherT<String, Integer> eitherT = EitherT.left("error");
    TryT<Integer> tryT = eitherT.toTryT();
    assert (tryT.toCompletableFuture().join().isFailure());
  }

}
