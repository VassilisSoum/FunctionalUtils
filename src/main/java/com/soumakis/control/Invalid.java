package com.soumakis.control;

public record Invalid<E, A>(E error) implements Validated<E, A> {

}
