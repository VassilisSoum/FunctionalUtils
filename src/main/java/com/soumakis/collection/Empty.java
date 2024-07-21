package com.soumakis.collection;

import java.util.Optional;

public record Empty<T>() implements SeqList<T> {

  @Override
  public boolean isEmpty() {
    return true;
  }

  @Override
  public T head() {
    throw new UnsupportedOperationException("head() called on empty list");
  }

  @Override
  public Optional<T> headOption() {
    return Optional.empty();
  }

  @Override
  public SeqList<T> tail() {
    throw new UnsupportedOperationException("tail() called on empty list");
  }

  @Override
  public Optional<T> last() {
    return Optional.empty();
  }
}
