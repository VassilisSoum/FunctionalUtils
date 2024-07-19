package com.soumakis.control;

public record Valid<E, A>(A value) implements Validated<E, A> {
  
}
