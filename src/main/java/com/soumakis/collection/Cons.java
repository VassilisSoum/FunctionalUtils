package com.soumakis.collection;

import java.util.Optional;

public record Cons<T>(T head, SeqList<T> tail) implements SeqList<T> {

  @Override
  public boolean isEmpty() {
    return false;
  }

  @Override
  public Optional<T> headOption() {
    return Optional.ofNullable(head);
  }

  @Override
  public Optional<T> last() {
    if (tail.isEmpty()) {
      return Optional.ofNullable(head);
    }
    return tail.last();
  }
}
