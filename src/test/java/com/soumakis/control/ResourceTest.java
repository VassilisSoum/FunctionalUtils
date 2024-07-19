package com.soumakis.control;

import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.Test;

public class ResourceTest {

  @Test
  void testUse() {
    Resource<String> resource = Resource.make(
        () -> Try.success("resource"),
        r -> null
    );

    assert (resource.use(r -> 42).get() == 42);
  }

  @Test
  void testUseWithFailureAndNoReleaseFnCalled() {
    AtomicBoolean releaseUsed = new AtomicBoolean(false);
    Resource<String> resource = Resource.make(
        () -> Try.failure(new RuntimeException("error")),
        r -> {
          releaseUsed.set(true);
          return null;
        }
    );

    assert (resource.use(r -> 42).isFailure());
    assert (!releaseUsed.get());
  }

  @Test
  void testUseWithFailureAndReleaseFnCalled() {
    AtomicBoolean releaseUsed = new AtomicBoolean(false);
    Resource<String> resource = Resource.make(
        () -> Try.success("something"),
        r -> {
          releaseUsed.set(true);
          return null;
        }
    );

    assert (resource.use(r -> {
      throw new IllegalStateException();
    }).isFailure());
    assert (releaseUsed.get());
  }


}
