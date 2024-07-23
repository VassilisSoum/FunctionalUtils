package com.soumakis.collection;

import org.junit.jupiter.api.Test;

public class SeqListTest {

  @Test
  void testEmpty() {
    SeqList<Integer> empty = new Empty<>();
    assert empty.isEmpty();
  }

  @Test
  void testComplex() {
    SeqList<Integer> list = SeqList.of(1, 2, 3, 4, 5);
    assert !list.isEmpty();
    assert list.headOption().isPresent();
    assert list.headOption().get() == 1;
    assert list.last().isPresent();
    assert list.last().get() == 5;

    SeqList<Integer> tail = list.tail();
    assert !tail.isEmpty();
    assert tail.headOption().isPresent();
    assert tail.headOption().get() == 2;
    assert tail.last().isPresent();
    assert tail.last().get() == 5;
  }

  @Test
  void testMap() {
    SeqList<Integer> list = SeqList.of(1, 2, 3, 4, 5);
    SeqList<Integer> mapped = list.map(x -> x * 2);
    assert mapped.headOption().get() == 2;
    assert mapped.tail().headOption().get() == 4;
    assert mapped.tail().tail().headOption().get() == 6;
    assert mapped.tail().tail().tail().headOption().get() == 8;
    assert mapped.tail().tail().tail().tail().headOption().get() == 10;
  }

  @Test
  void testFilter() {
    SeqList<Integer> list = SeqList.of(1, 2, 3, 4, 5);
    SeqList<Integer> filtered = list.filter(x -> x % 2 == 0);
    assert filtered.headOption().get() == 2;
    assert filtered.tail().headOption().get() == 4;
  }

  @Test
  void testFlatMap() {
    SeqList<Integer> list = SeqList.of(1, 2, 3, 4, 5);
    SeqList<Integer> flatMapped = list.flatMap(x -> SeqList.of(x, x * 2));
    assert flatMapped.headOption().get() == 1;
    assert flatMapped.tail().headOption().get() == 2;
    assert flatMapped.tail().tail().headOption().get() == 2;
    assert flatMapped.tail().tail().tail().headOption().get() == 4;
    assert flatMapped.tail().tail().tail().tail().headOption().get() == 3;
    assert flatMapped.tail().tail().tail().tail().tail().headOption().get() == 6;
    assert flatMapped.tail().tail().tail().tail().tail().tail().headOption().get() == 4;
    assert flatMapped.tail().tail().tail().tail().tail().tail().tail().headOption().get() == 8;
    assert
        flatMapped.tail().tail().tail().tail().tail().tail().tail().tail().headOption().get() == 5;
    assert
        flatMapped.tail().tail().tail().tail().tail().tail().tail().tail().tail().headOption().get()
            == 10;
  }

  @Test
  void testTake() {
    SeqList<Integer> list = SeqList.of(1, 2, 3, 4, 5);
    SeqList<Integer> taken = list.take(3);
    assert taken.headOption().get() == 1;
    assert taken.tail().headOption().get() == 2;
    assert taken.tail().tail().headOption().get() == 3;
  }

  @Test
  void testFilterOut() {
    SeqList<Integer> list = SeqList.of(1, 2, 3, 4, 5);
    SeqList<Integer> filteredOut = list.filterOut(x -> x % 2 == 0);
    assert filteredOut.headOption().get() == 1;
    assert filteredOut.tail().headOption().get() == 3;
    assert filteredOut.tail().tail().headOption().get() == 5;
  }

  @Test
  void testGet() {
    SeqList<Integer> list = SeqList.of(1, 2, 3, 4, 5);
    assert list.get(1).isPresent();
  }

  @Test
  void testExists() {
    SeqList<Integer> list = SeqList.of(1, 2, 3, 4, 5);
    assert list.exists(x -> x == 3);
  }

  @Test
  void testJavaList() {
    SeqList<Integer> list = SeqList.of(1, 2, 3, 4, 5);
    java.util.List<Integer> javaList = list.toJavaList();
    assert javaList.get(0) == 1;
    assert javaList.get(1) == 2;
    assert javaList.get(2) == 3;
    assert javaList.get(3) == 4;
    assert javaList.get(4) == 5;
  }

  @Test
  void testRemove() {
    SeqList<Integer> list = SeqList.of(1, 2, 3, 4, 5);
    SeqList<Integer> removed = list.remove(3);
    assert removed.headOption().get() == 1;
    assert removed.tail().headOption().get() == 2;
    assert removed.tail().tail().headOption().get() == 4;
    assert removed.tail().tail().tail().headOption().get() == 5;
  }

  @Test
  void testAdd() {
    SeqList<Integer> list = SeqList.of(1, 2, 3, 4, 5);
    SeqList<Integer> added = list.add(6);
    assert added.headOption().get() == 6;
    assert added.tail().headOption().get() == 1;
    assert added.tail().tail().headOption().get() == 2;
    assert added.tail().tail().tail().headOption().get() == 3;
    assert added.tail().tail().tail().tail().headOption().get() == 4;
    assert added.tail().tail().tail().tail().tail().headOption().get() == 5;
  }

  @Test
  void testMathComplex() {
    SeqList<Integer> list = SeqList.of(1, 2, 3, 4, 5, 6);
    SeqList<Double> updatedList = list.filterOut(number -> number % 2 == 0)
        .map(number -> Math.pow(number, 2));

    assert updatedList.headOption().get() == 1;
    assert updatedList.tail().headOption().get() == 9;
    assert updatedList.tail().tail().headOption().get() == 25;

  }

  @Test
  void testFlatten() {
    SeqList<SeqList<Integer>> list = SeqList.of(SeqList.of(1, 2), SeqList.of(3, 4),
        SeqList.of(5, 6));
    SeqList<Integer> flattened = list.flatMap(x -> x);
    assert flattened.headOption().get() == 1;
    assert flattened.tail().headOption().get() == 2;
    assert flattened.tail().tail().headOption().get() == 3;
    assert flattened.tail().tail().tail().headOption().get() == 4;
    assert flattened.tail().tail().tail().tail().headOption().get() == 5;
    assert flattened.tail().tail().tail().tail().tail().headOption().get() == 6;
  }

  @Test
  void testPatternMatch() {
    SeqList<Integer> list = SeqList.of(1, 2, 3, 4, 5, 6);
    switch (list) {
      case Empty() -> {
        assert false;
      }
      case Cons<Integer> cons -> {
        assert cons.head() == 1;
        assert cons.tail().headOption().get() == 2;
        assert cons.tail().tail().headOption().get() == 3;
        assert cons.tail().tail().tail().headOption().get() == 4;
        assert cons.tail().tail().tail().tail().headOption().get() == 5;
        assert cons.tail().tail().tail().tail().tail().headOption().get() == 6;
      }
    }
  }
}
