package com.example.cse3masassignment;

import org.junit.Test;

import static org.junit.Assert.*;

import com.google.errorprone.annotations.DoNotMock;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void subtraction_isCorrect() {
        assertNotEquals(1, 2 - 2);
    }

    @Test
    public void object_isNull() {
        Object obj = null;
        assertNull(obj);
    }

    @Test
    public void division_byZero_throwsException() {
        assertThrows(ArithmeticException.class, () -> {
            int result = 10 / 0;
        });
    }
}