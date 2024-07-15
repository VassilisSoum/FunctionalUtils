package com.soumakis.safetype;

import com.soumakis.control.Try;
import org.junit.jupiter.api.Test;

public class NonEmptyStringTest {

  @Test
  void testNonEmptyString() {
    Try<NonEmptyString> nonEmptyString = NonEmptyString.of("test");
    assert (nonEmptyString.isSuccess());
    assert (nonEmptyString.get().getValue().equals("test"));
  }

  @Test
  void testEmptyString() {
    Try<NonEmptyString> emptyString = NonEmptyString.of("");
    assert (emptyString.isFailure());
  }

  @Test
  void testNullString() {
    Try<NonEmptyString> nullString = NonEmptyString.of(null);
    assert (nullString.isFailure());
  }

  @Test
  void testConcat() {
    Try<NonEmptyString> nonEmptyString = NonEmptyString.of("test");
    Try<NonEmptyString> mapped = nonEmptyString.map(s -> s.concat(NonEmptyString.unsafeOf("ing")));
    assert (mapped.isSuccess());
    assert (mapped.get().getValue().equals("testing"));
  }

}
