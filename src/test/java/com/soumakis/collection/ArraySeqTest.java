package com.soumakis.collection;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class ArraySeqTest {

  @Test
  void testOf() {
    ArraySeq<Integer> seq = ArraySeq.of(1, 2, 3);
    assertEquals(3, seq.length());
  }

  @Test
  void testEmpty() {
    ArraySeq<Integer> seq = ArraySeq.empty();
    assertEquals(0, seq.length());
  }

  @Test
  void testLength() {
    ArraySeq<Integer> seq = ArraySeq.of(1, 2, 3);
    assertEquals(3, seq.length());
  }

  @Test
  void testAppend() {
    ArraySeq<Integer> seq = ArraySeq.of(1, 2, 3);
    ArraySeq<Integer> seq2 = seq.append(4);
    assertEquals(4, seq2.length());
  }

  @Test
  void testAppendSeq() {
    ArraySeq<Integer> seq = ArraySeq.of(1, 2, 3);
    ArraySeq<Integer> seq2 = ArraySeq.of(4, 5, 6);
    ArraySeq<Integer> seq3 = seq.append(seq2);
    assertEquals(6, seq3.length());
  }

  @Test
  void testAppendEmpty() {
    ArraySeq<Integer> seq = ArraySeq.of(1, 2, 3);
    ArraySeq<Integer> seq2 = ArraySeq.empty();
    ArraySeq<Integer> seq3 = seq.append(seq2);
    assertEquals(3, seq3.length());
  }

  @Test
  void testAppendEmptySeq() {
    ArraySeq<Integer> seq = ArraySeq.empty();
    ArraySeq<Integer> seq2 = ArraySeq.of(4, 5, 6);
    ArraySeq<Integer> seq3 = seq.append(seq2);
    assertEquals(3, seq3.length());
  }

  @Test
  void testPrepend() {
    ArraySeq<Integer> seq = ArraySeq.of(1, 2, 3);
    ArraySeq<Integer> seq2 = seq.prepend(0);
    assertEquals(4, seq2.length());
    assert(seq2.equals(ArraySeq.of(0, 1, 2, 3)));
  }

  @Test
  void testMap() {
    ArraySeq<Integer> seq = ArraySeq.of(1, 2, 3);
    ArraySeq<Integer> seq2 = seq.map(x -> x * 2);
    assertEquals(3, seq2.length());
    assert(seq2.equals(ArraySeq.of(2, 4, 6)));
  }

  @Test
  void testFlatMap() {
    ArraySeq<Integer> seq = ArraySeq.of(1, 2, 3);
    ArraySeq<Integer> seq2 = seq.flatMap(x -> ArraySeq.of(x, x * 2));
    assertEquals(6, seq2.length());
    assert(seq2.equals(ArraySeq.of(1, 2, 2, 4, 3, 6)));
  }

  @Test
  void testUpdate() {
    ArraySeq<Integer> seq = ArraySeq.of(1, 2, 3);
    ArraySeq<Integer> seq2 = seq.update(1, 4);
    assertEquals(3, seq2.length());
    assert(seq2.equals(ArraySeq.of(1, 4, 3)));
  }

  @Test
  void testDelete() {
    ArraySeq<Integer> seq = ArraySeq.of(1, 2, 3);
    ArraySeq<Integer> seq2 = seq.delete(2);
    assertEquals(2, seq2.length());
    assert(seq2.equals(ArraySeq.of(1, 2)));
  }

  @Test
  void testFilter() {
    ArraySeq<Integer> seq = ArraySeq.of(1, 2, 3, 4, 5);
    ArraySeq<Integer> seq2 = seq.filter(x -> x % 2 == 0);
    assertEquals(2, seq2.length());
    assert(seq2.equals(ArraySeq.of(2, 4)));
  }

  @Test
  void testToArrayList() {
    ArraySeq<Integer> seq = ArraySeq.of(1, 2, 3);
    var list = seq.toJavaArrayList();
    assertEquals(3, list.size());
    assertEquals(1, list.get(0));
    assertEquals(2, list.get(1));
    assertEquals(3, list.get(2));
  }

}
