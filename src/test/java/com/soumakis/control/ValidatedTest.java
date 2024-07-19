package com.soumakis.control;

import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

public class ValidatedTest {

  @Test
  void testValid() {
    Validated<List<String>, String> valid = Validated.valid("John");
    assert valid instanceof Valid;
    assert ((Valid<List<String>, String>) valid).value().equals("John");
  }

  @Test
  void testInvalid() {
    Validated<List<String>, String> invalid = Validated.invalid(List.of("Name cannot be empty"));
    assert invalid instanceof Invalid;
    assert ((Invalid<List<String>, String>) invalid).error()
        .equals(List.of("Name cannot be empty"));
  }

  @Test
  void testMapN() {
    Validated<String, String> validName = Validated.valid("John");
    Validated<String, Integer> validAge = Validated.valid(25);
    Validated<String, String> validCity = Validated.valid("New York");

    List<Validated<String, ?>> validatedList = List.of(validName, validAge, validCity);

    Validated<List<String>, Person> validatedPerson = Validated.mapN(values -> {
      String name = (String) values.get(0);
      int age = (int) values.get(1);
      String city = (String) values.get(2);
      return new Person(name, age, city);
    }, validatedList);

    assert validatedPerson instanceof Valid;
    Person person = ((Valid<List<String>, Person>) validatedPerson).value();
    assert person.age() == 25;
    assert person.name().equals("John");
    assert person.city().equals("New York");
  }

  @Test
  void testMapNWithErrors() {
    Validated<String, String> validName = Validated.valid("John");
    Validated<String, Integer> validAge = Validated.invalid("Age must be at least 18");
    Validated<String, String> validCity = Validated.valid("New York");

    List<Validated<String, ?>> validatedList = List.of(validName, validAge, validCity);

    Validated<List<String>, Person> validatedPerson = Validated.mapN(values -> {
      String name = (String) values.get(0);
      int age = (int) values.get(1);
      String city = (String) values.get(2);
      return new Person(name, age, city);
    }, validatedList);

    assert validatedPerson instanceof Invalid;
    List<String> errors = ((Invalid<List<String>, Person>) validatedPerson).error();
    assert(errors.getFirst().equals("Age must be at least 18"));
  }

  @Test
  void testMap() {
    Validated<String, Integer> valid = Validated.valid(42);

    Validated<String, String> mapped = valid.map(Object::toString);

    assert mapped instanceof Valid;
    assert ((Valid<String, String>) mapped).value().equals("42");
  }

  @Test
  void testFlatMap() {
    Validated<String, Integer> valid = Validated.valid(42);

    Validated<String, String> mapped = valid.flatMap(n -> Validated.valid(n.toString()));

    assert mapped instanceof Valid;
    assert ((Valid<String, String>) mapped).value().equals("42");
  }

  @Test
  void testFold() {
    Validated<String, Integer> valid = Validated.valid(42);
    String result = valid.fold(
        errors -> "Error: " + errors,
        value -> "Value: " + value);
    assert result.equals("Value: 42");
  }

  @Test
  void testFoldInvalid() {
    Validated<String, Integer> invalid = Validated.invalid("Invalid");
    String result = invalid.fold(
        errors -> "Error: " + errors,
        value -> "Value: " + value);
    assert result.equals("Error: Invalid");
  }

  @Test
  void testSwap() {
    Validated<String, Integer> valid = Validated.invalid("42");
    Validated<Integer, String> swapped = valid.swap();
    assert swapped instanceof Valid;
    assert ((Valid<Integer, String>) swapped).value().equals("42");
  }

}
