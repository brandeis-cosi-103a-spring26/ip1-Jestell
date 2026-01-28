package edu.brandeis.cosi103a.ip1;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.util.Random;
import java.util.Scanner;
import java.io.ByteArrayInputStream;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue()
    {
        assertTrue( true );
    }

    @Test
    public void testRollDieReturnsValueBetween1And6() {
        Random random = new Random(42); // Fixed seed for consistent testing
        for (int i = 0; i < 100; i++) {
            int result = ATG.rollDie(random);
            assertTrue("Roll should be between 1 and 6", result >= 1 && result <= 6);
        }
    }

    @Test
    public void testHandleReRollsNoReRoll() {
        String input = "n\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        Random random = new Random(42);
        int currentValue = 3;
        int result = ATG.handleReRolls(scanner, random, currentValue);
        assertEquals("Should return original value when no re-roll", 3, result);
    }

    @Test
    public void testHandleReRollsWithOneReRoll() {
        String input = "y\nn\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        Random random = new Random(42);
        int currentValue = 1;
        int result = ATG.handleReRolls(scanner, random, currentValue);
        assertTrue("Result should be between 1 and 6 after re-roll", result >= 1 && result <= 6);
        // Since it re-rolled once, result should be the new roll value
    }

    @Test
    public void testHandleReRollsWithTwoReRolls() {
        String input = "y\ny\nn\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        Random random = new Random(42);
        int currentValue = 1;
        int result = ATG.handleReRolls(scanner, random, currentValue);
        assertTrue("Result should be between 1 and 6 after two re-rolls", result >= 1 && result <= 6);
    }

    @Test
    public void testHandleReRollsInvalidInputThenNo() {
        String input = "invalid\nn\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        Random random = new Random(42);
        int currentValue = 2;
        int result = ATG.handleReRolls(scanner, random, currentValue);
        assertEquals("Should return original value after invalid input and no re-roll", 2, result);
    }
}
