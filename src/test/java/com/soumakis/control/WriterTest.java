package com.soumakis.control;

import com.soumakis.collection.ArraySeq;
import java.util.List;
import org.junit.jupiter.api.Test;

public class WriterTest {

  @Test
  void testMap() {
    Writer<Integer, String> writer = Writer.of(1, "Hello");
    Writer<Integer, String> mapped = writer.map(n -> n + 4);
    assert(mapped.getValue().equals(5));
    assert(mapped.getContexts().get(0).equals("Hello"));
  }

  @Test
  void testFlatMap() {
    Writer<Integer, String> writer = Writer.of(1, "Hello");
    Writer<Integer, String> mapped = writer.flatMap(n -> Writer.of(n + 4, "World"));
    assert(mapped.getValue().equals(5));
    assert(mapped.getContexts().get(0).equals("Hello"));
    assert(mapped.getContexts().get(1).equals("World"));
  }

  @Test
  void testOf() {
    Writer<Integer, String> writer = Writer.of(1, List.of("Hello", "World"));
    assert(writer.getValue().equals(1));
    assert(writer.getContexts().get(0).equals("Hello"));
    assert(writer.getContexts().get(1).equals("World"));
  }

}
