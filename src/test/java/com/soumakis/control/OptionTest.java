package com.soumakis.control;

import com.soumakis.collection.Tuple2;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class OptionTest {

    @Test
    void testOptionOf() {
        Option<String> some = Option.of("Hello");
        assert (some instanceof Some);
        assert (some.getOrElse("World").equals("Hello"));

        Option<String> none = Option.of(null);
        assert (none instanceof None);
        assert (none.getOrElse("World").equals("World"));
    }

    @Test
    void testOptionNone() {
        Option<String> none = Option.none();
        assert (none instanceof None);
        assert (none.getOrElse("World").equals("World"));
    }

    @Test
    void testOptionIsEmpty() {
        Option<String> some = Option.of("Hello");
        assert (!some.isEmpty());
        assert (some.isDefined());

        Option<String> none = Option.of(null);
        assert (none.isEmpty());
        assert (!none.isDefined());
    }

    @Test
    void testOptionIsDefined() {
        Option<String> some = Option.of("Hello");
        assert (some.isDefined());
        assert (!some.isEmpty());

        Option<String> none = Option.of(null);
        assert (!none.isDefined());
        assert (none.isEmpty());
    }

    @Test
    void testOptionGetOrElse() {
        Option<String> some = Option.of("Hello");
        assert (some.getOrElse("World").equals("Hello"));

        Option<String> none = Option.of(null);
        assert (none.getOrElse("World").equals("World"));
    }

    @Test
    void testMap() {
        Option<String> some = Option.of("Hello");
        Option<Integer> length = some.map(String::length);
        assert (length.isDefined());
        assert (length.getOrElse(0) == 5);

        Option<String> none = Option.of(null);
        Option<Integer> noneLength = none.map(String::length);
        assert (noneLength.isEmpty());
        assert (noneLength.getOrElse(0) == 0);
    }

    @Test
    void testFilter() {
        Option<String> some = Option.of("Hello");
        Option<String> filtered = some.filter(s -> s.startsWith("H"));
        assert (filtered.isDefined());
        assert (filtered.getOrElse("").equals("Hello"));

        Option<String> none = Option.of(null);
        Option<String> noneFiltered = none.filter(s -> s.startsWith("H"));
        assert (noneFiltered.isEmpty());
        assert (noneFiltered.getOrElse("").isEmpty());
    }

    @Test
    void testFlatMap() {
        Option<String> some = Option.of("Hello");
        Option<Integer> length = some.flatMap(s -> Option.of(s.length()));
        assert (length.isDefined());
        assert (length.getOrElse(0) == 5);

        Option<String> none = Option.of(null);
        Option<Integer> noneLength = none.flatMap(s -> Option.of(s.length()));
        assert (noneLength.isEmpty());
        assert (noneLength.getOrElse(0) == 0);
    }

    @Test
    void testOr() {
        Option<String> some = Option.of("Hello");
        Option<String> none = Option.of(null);
        assert (some.or(() -> Option.of("World")).getOrElse("").equals("Hello"));
        assert (none.or(() -> Option.of("World")).getOrElse("").equals("World"));
    }

    @Test
    void testFold() {
        Option<String> some = Option.of("Hello");
        Option<String> none = Option.of(null);
        assert (some.fold(() -> "World", s -> s).equals("Hello"));
        assert (none.fold(() -> "World", s -> s).equals("World"));
    }

    @Test
    void testToRight() {
        Option<String> some = Option.of("Hello");
        Option<String> none = Option.of(null);
        assert (some.toRight(() -> "World").isRight());
        assert (some.toRight(() -> "World").getOrElse("").equals("Hello"));
        assert (none.toRight(() -> "World").isLeft());
        assert (none.toRight(() -> "World").getLeft().equals("World"));
    }

    @Test
    void testToLeft() {
        Option<String> some = Option.of("Hello");
        Option<String> none = Option.of(null);
        assert (some.toLeft(() -> "World").isLeft());
        assert (some.toLeft(() -> "World").getLeft().equals("Hello"));
        assert (none.toLeft(() -> "World").isRight());
        assert (none.toLeft(() -> "World").getOrElse("").equals("World"));
    }

    @Test
    void testToOptional() {
        Option<String> some = Option.of("Hello");
        Option<String> none = Option.of(null);
        assert (some.toJavaOptional().isPresent());
        assert (some.toJavaOptional().get().equals("Hello"));
        assert (none.toJavaOptional().isEmpty());
    }

    @Test
    void testToTry() {
        Option<String> some = Option.of("Hello");
        Option<String> none = Option.of(null);
        assert (some.toTry(RuntimeException::new).isSuccess());
        assert (some.toTry(RuntimeException::new).get().equals("Hello"));
        assert (none.toTry(RuntimeException::new).isFailure());
    }

    @Test
    void testFromOptional() {
        Option<String> some = Option.fromOptional(Optional.of("Hello"));
        assert (some.isDefined());
        assert (some.getOrElse("").equals("Hello"));

        Option<String> none = Option.fromOptional(Optional.empty());
        assert (none.isEmpty());
        assert (none.getOrElse("World").equals("World"));
    }

    @Test
    void testToEither() {
        Option<String> some = Option.of("Hello");
        Option<String> none = Option.of(null);
        assert (some.toEither().isRight());
        assert (none.toEither().isLeft());
    }

    @Test
    void testZip() {
        Option<String> some = Option.of("Hello");
        Option<Integer> none = Option.of(null);

        Option<Tuple2<String, Integer>> zippedSome = some.zip(Option.of(42));
        assert (zippedSome.isDefined());
        assert (zippedSome.getOrElse(new Tuple2<>("", 0)).first().equals("Hello"));
        assert (zippedSome.getOrElse(new Tuple2<>("", 0)).second() == 42);

        Option<Tuple2<String, Integer>> zippedNone = some.zip(none);
        assert (zippedNone.isEmpty());

        Option<Tuple2<Integer, Integer>> zippedBothNone = none.zip(none);
        assert (zippedBothNone.isEmpty());
    }

    @Test
    void testZipWith() {
        Option<String> some = Option.of("Hello");
        Option<Integer> none = Option.of(null);

        Option<String> resultSome = some.zipWith(Option.of(42), (s, i) -> s + " " + i);
        assert (resultSome.isDefined());
        assert (resultSome.getOrElse("").equals("Hello 42"));

        Option<String> resultNone = some.zipWith(none, (s, i) -> s + " " + i);
        assert (resultNone.isEmpty());

        Option<String> resultBothNone = none.zipWith(none, (s, i) -> s + " " + i);
        assert (resultBothNone.isEmpty());
    }

    @Test
    void testContains() {
        Option<String> some = Option.of("Hello");
        assert (some.contains("Hello"));
        assert (!some.contains("World"));

        Option<String> none = Option.of(null);
        assert (!none.contains("Hello"));
        assert (!none.contains("World"));
    }

    @Test
    void testTap() {
        Option<String> some = Option.of("Hello");
        StringBuilder sb = new StringBuilder();

        some.tap(sb::append);
        assert (sb.toString().equals("Hello"));

        Option<String> none = Option.of(null);
        sb.setLength(0);
        none.tap(sb::append);
        assert (sb.toString().isEmpty());
    }

    @Test
    void testExists() {
        Option<String> some = Option.of("Hello");
        assert (some.exists(s -> s.startsWith("H")));
        assert (!some.exists(s -> s.startsWith("W")));

        Option<String> none = Option.of(null);
        assert (!none.exists(s -> s.startsWith("H")));
    }

    @Test
    void flatten_onNestedSome_shouldUnwrapInner() {
        Option<Option<String>> nested = Option.of(Option.of("inner"));
        Option<String> flattened = Option.flatten(nested);

        assertInstanceOf(Some.class, flattened);
        assertEquals("inner", flattened.getOrElse("fallback"));
    }

    @Test
    void flatten_onNestedNone_shouldReturnNone() {
        Option<Option<String>> nested = Option.of(Option.none());
        Option<String> flattened = Option.flatten(nested);

        assertInstanceOf(None.class, flattened);
        assertTrue(flattened.isEmpty());
    }
}
