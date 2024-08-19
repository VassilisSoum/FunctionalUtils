package com.soumakis.control;

import java.util.Optional;
import org.junit.jupiter.api.Test;

public class OptionTest {

  @Test
  void testOptionOf() {
    Option<String> some = Option.of("Hello");
    assert(some instanceof Some);
    assert(some.getOrElse("World").equals("Hello"));

    Option<String> none = Option.of(null);
    assert(none instanceof None);
    assert(none.getOrElse("World").equals("World"));
  }

  @Test
  void testOptionNone() {
    Option<String> none = Option.none();
    assert(none instanceof None);
    assert(none.getOrElse("World").equals("World"));
  }

  @Test
  void testOptionIsEmpty() {
    Option<String> some = Option.of("Hello");
    assert(!some.isEmpty());
    assert(some.isDefined());

    Option<String> none = Option.of(null);
    assert(none.isEmpty());
    assert(!none.isDefined());
  }

  @Test
  void testOptionIsDefined() {
    Option<String> some = Option.of("Hello");
    assert(some.isDefined());
    assert(!some.isEmpty());

    Option<String> none = Option.of(null);
    assert(!none.isDefined());
    assert(none.isEmpty());
  }

  @Test
  void testOptionGetOrElse() {
    Option<String> some = Option.of("Hello");
    assert(some.getOrElse("World").equals("Hello"));

    Option<String> none = Option.of(null);
    assert(none.getOrElse("World").equals("World"));
  }

  @Test
  void testMap() {
    Option<String> some = Option.of("Hello");
    Option<Integer> length = some.map(String::length);
    assert(length.isDefined());
    assert(length.getOrElse(0) == 5);

    Option<String> none = Option.of(null);
    Option<Integer> noneLength = none.map(String::length);
    assert(noneLength.isEmpty());
    assert(noneLength.getOrElse(0) == 0);
  }

  @Test
  void testFilter() {
    Option<String> some = Option.of("Hello");
    Option<String> filtered = some.filter(s -> s.startsWith("H"));
    assert(filtered.isDefined());
    assert(filtered.getOrElse("").equals("Hello"));

    Option<String> none = Option.of(null);
    Option<String> noneFiltered = none.filter(s -> s.startsWith("H"));
    assert(noneFiltered.isEmpty());
    assert(noneFiltered.getOrElse("").isEmpty());
  }

  @Test
  void testFlatMap() {
    Option<String> some = Option.of("Hello");
    Option<Integer> length = some.flatMap(s -> Option.of(s.length()));
    assert(length.isDefined());
    assert(length.getOrElse(0) == 5);

    Option<String> none = Option.of(null);
    Option<Integer> noneLength = none.flatMap(s -> Option.of(s.length()));
    assert(noneLength.isEmpty());
    assert(noneLength.getOrElse(0) == 0);
  }

  @Test
  void testOr() {
    Option<String> some = Option.of("Hello");
    Option<String> none = Option.of(null);
    assert(some.or(() -> Option.of("World")).getOrElse("").equals("Hello"));
    assert(none.or(() -> Option.of("World")).getOrElse("").equals("World"));
  }

  @Test
  void testFold() {
    Option<String> some = Option.of("Hello");
    Option<String> none = Option.of(null);
    assert(some.fold(() -> "World", s -> s).equals("Hello"));
    assert(none.fold(() -> "World", s -> s).equals("World"));
  }

  @Test
  void testToRight() {
    Option<String> some = Option.of("Hello");
    Option<String> none = Option.of(null);
    assert(some.toRight(() -> "World").isRight());
    assert(some.toRight(() -> "World").getOrElse("").equals("Hello"));
    assert(none.toRight(() -> "World").isLeft());
    assert(none.toRight(() -> "World").getLeft().equals("World"));
  }

  @Test
  void testToLeft() {
    Option<String> some = Option.of("Hello");
    Option<String> none = Option.of(null);
    assert(some.toLeft(() -> "World").isLeft());
    assert(some.toLeft(() -> "World").getLeft().equals("Hello"));
    assert(none.toLeft(() -> "World").isRight());
    assert(none.toLeft(() -> "World").getOrElse("").equals("World"));
  }

  @Test
  void testToOptional() {
    Option<String> some = Option.of("Hello");
    Option<String> none = Option.of(null);
    assert(some.toJavaOptional().isPresent());
    assert(some.toJavaOptional().get().equals("Hello"));
    assert(none.toJavaOptional().isEmpty());
  }

  @Test
  void testToTry() {
    Option<String> some = Option.of("Hello");
    Option<String> none = Option.of(null);
    assert(some.toTry(RuntimeException::new).isSuccess());
    assert(some.toTry(RuntimeException::new).get().equals("Hello"));
    assert(none.toTry(RuntimeException::new).isFailure());
  }

  @Test
  void testFromOptional() {
    Option<String> some = Option.fromOptional(Optional.of("Hello"));
    assert(some.isDefined());
    assert(some.getOrElse("").equals("Hello"));

    Option<String> none = Option.fromOptional(Optional.empty());
    assert(none.isEmpty());
    assert(none.getOrElse("World").equals("World"));
  }

  @Test
  void testToEither() {
    Option<String> some = Option.of("Hello");
    Option<String> none = Option.of(null);
    assert(some.toEither().isRight());
    assert(none.toEither().isLeft());
  }
}
