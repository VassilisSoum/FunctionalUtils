package com.soumakis.control;

import com.soumakis.collection.ArraySeq;
import java.util.List;
import java.util.function.Function;

public class Writer<T, L> {

  private final T value;
  private final ArraySeq<L> contexts;

  private Writer(T value, ArraySeq<L> contexts) {
    this.value = value;
    this.contexts = contexts;
  }

  public T getValue() {
    return value;
  }

  public ArraySeq<L> getContexts() {
    return contexts;
  }

  public <U> Writer<U, L> map(Function<T, U> f) {
    return new Writer<>(f.apply(value), contexts);
  }

  public <U> Writer<U, L> flatMap(Function<T, Writer<U, L>> f) {
    Writer<U, L> result = f.apply(value);
    return new Writer<>(result.getValue(), contexts.append(result.contexts));
  }

  public static <T, L> Writer<T, L> of(T value, L context) {
    return new Writer<>(value, ArraySeq.of(context));
  }

  public static <T, L> Writer<T, L> of(T value, List<L> contexts) {
    return new Writer<>(value, ArraySeq.of(contexts));
  }
}
