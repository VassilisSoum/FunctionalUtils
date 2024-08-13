package com.soumakis.safetype;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class ThrowingSupplierTest {

   @Test
   void testGet() {
     ThrowingSupplier<Integer> supplier = () -> 1;
     assertEquals(1, supplier.get());
   }

   @Test
   void testGetWithException() {
     ThrowingSupplier<Integer> supplier = () -> {
       throw new Exception("Test exception");
     };
     assertThrows(RuntimeException.class, supplier::get);
   }

   @Test
   void testGetWithExceptionCustomWrapper() {
     ThrowingSupplier<Integer> supplier = () -> {
       throw new Exception("Test exception");
     };
     assertThrows(IllegalStateException.class, () -> supplier.get((msg, ex) -> new IllegalStateException(msg, ex)));
   }
}
