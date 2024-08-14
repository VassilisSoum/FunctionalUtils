package com.soumakis.control;

import org.junit.jupiter.api.Test;

public class ReaderTest {

  @Test
  void testRun() {
    Reader<Integer, Integer> reader = Reader.of((Integer context) -> context + 1);

    assert (reader.run(1) == 2);
  }

}
