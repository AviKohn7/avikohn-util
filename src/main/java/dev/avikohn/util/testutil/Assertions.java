package dev.avikohn.util.testutil;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.avikohn.util.DifferenceFinder;
import dev.avikohn.util.testutil.comparestructures.*;
import org.opentest4j.AssertionFailedError;

import java.lang.reflect.Executable;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Assertions{

    /**
     * An equivalent to assertTrue, but the message inputted is the exact message printed
     * (assertTrue also prints other information about the expected and actual inputs).
     * @param passed true if this test passed, false if it did not
     * @param message the message to be printed if this test fails
     */
    public static void assertTrueCustomMessage(boolean passed, String message){
        if(!passed){
            throw new AssertionFailedError(message);
        }
    }

    public static <S,U> void assertStructuresIdentical(Class<S> leftClass, Class<U> rightClass){
        assertStructuresIdentical(leftClass, rightClass, ContentComparer.DEFAULT_ACCESS_FLAG);
    }
    public static <S,U> void assertStructuresIdentical(Class<S> leftClass, Class<U> rightClass, Flag accessFlag){
        ClassComparison<S,U> comparison = new ClassComparison<>(leftClass, rightClass);
        comparison.setAccessFlag(accessFlag);
        comparison.compare();
        if(!comparison.isIdentical()){
            String results = comparison.getResultsString();
            throw new AssertionFailedError(String.format("Structures of %s and %s are different. Difference: \n%s", leftClass.getName(), rightClass.getName(), results));

        }
    }
    /**
     * Assert that expected equals actual, but if not, a comparison of expected and actual is done with
     * the differences clearly and exactly shown (\n converted to \\n\n and \r to \\r\r)
     * <br/>The error message contains a string with (by default) <~XXXXXX~> containing parts deleted from expected
     * and <**XXXXXX**> containing parts inserted by actual
     * @param expected the expected input
     * @param actual the actual input
     */
    public static void assertEqualsWithDiff(String expected, String actual){
        assertEqualsWithDiff(expected, actual, "");
    }
    /**
     * Assert that expected equals actual, but if not, a comparison of expected and actual is done with
     * the differences clearly and exactly shown (\n converted to \\n\n and \r to \\r\r)
     * <br/>The error message contains a string with (by default) <~XXXXXX~> containing parts deleted from expected
     * and <**XXXXXX**> containing parts inserted by actual.
     * @param expected the expected input
     * @param actual the actual input
     * @param distance the distance above and below a difference to include
     */
    public static void assertEqualsWithDiff(String expected, String actual, int distance){
        assertEqualsWithDiff(expected, actual, "", distance);
    }
    /**
     * Assert that expected equals actual, but if not, a comparison of expected and actual is done with
     * the differences clearly and exactly shown (\n converted to \\n\n and \r to \\r\r)
     * <br/>The error message contains a string with (by default) <~XXXXXX~> containing parts deleted from expected
     * and <**XXXXXX**> containing parts inserted by actual
     * @param expected the expected input
     * @param actual the actual input
     * @param message a message to show on an error (before the default message)
     */
    public static void assertEqualsWithDiff(String expected, String actual, String message){
        String differenceString = DifferenceFinder.getDifference(expected, actual).getResult();
        assertTrueCustomMessage(differenceString.equals("Strings are identical"), message+"Difference found (~~ means in expected, ** means in actual):\n"+differenceString);
    }

    /**
     * Assert that expected equals actual, but if not, a comparison of expected and actual is done with
     * the differences clearly and exactly shown (\n converted to \\n\n and \r to \\r\r)
     * <br/>The error message contains a string with (by default) <~XXXXXX~> containing parts deleted from expected
     * and <**XXXXXX**> containing parts inserted by actual
     * @param expected the expected input
     * @param actual the actual input
     * @param message a message to show on an error (before the default message)
     * @param distance the distance above and below a difference to include
     */
    public static void assertEqualsWithDiff(String expected, String actual, String message, int distance){
        String differenceString = DifferenceFinder.getDifference(expected, actual).getResultShortened(distance);
        assertTrueCustomMessage(differenceString.equals("Strings are identical"), message+"Difference found (~~ means in expected, ** means in actual):\n"+differenceString);
    }

    //PrintInterceptor assertions
    /**
     * Assert that the intercepted System.out/System.err output (consumed) matches expectedOutput,
     * assuming a println call.
     * <br/>If used, the using class must @ExtendWith(PrintInterceptor.class)
     * @param expectedOutput the output expected (not including the newline from println)
     * @throws UnsupportedOperationException if PrintInterceptor was not intercepted
     */
    public static void assertEqualsPrintln(String expectedOutput){
        assertEqualsPrintln(expectedOutput, false);
    }
    /**
     * Assert that the intercepted System.out/System.err output matches expectedOutput,
     * assuming a println call.
     * <br/>If used, the using class must @ExtendWith(PrintInterceptor.class)
     * @param expectedOutput the output expected (not including the newline from println)
     * @param peek if true, this method will not consume the print output (can be used my another method)
     * @throws UnsupportedOperationException if PrintInterceptor was not intercepted
     */
    public static void assertEqualsPrintln(String expectedOutput, boolean peek){
        assertEqualsPrint(expectedOutput+System.lineSeparator(), peek);
    }
    /**
     * Assert that the intercepted System.out/System.err output matches expectedOutput.
     * <br/>If used, the using class should @ExtendWith(PrintInterceptor.class)
     * @param expectedOutput the output expected
     * @param peek if true, this method will not consume the print output (can be used my another method)
     * @throws UnsupportedOperationException if PrintInterceptor was not intercepted
     */
    public static void assertEqualsPrint(String expectedOutput, boolean peek){
        if(!PrintInterceptor.isIntercepted()){
            throw new UnsupportedOperationException("The print output must be intercepted before calling any print assertions. " +
                    "Add @ExtendWith(PrintInterceptor.class) above any class using these assertions " +
                    "(this extension also adds independence for print tests)");
        }
        if(peek) {
            assertEquals(expectedOutput,PrintInterceptor.peekPrintOutput());
        } else {
            assertEquals(expectedOutput,PrintInterceptor.getPrintOutput());
        }
    }
    /**
     * Assert that the intercepted System.out/System.err output (consumed) matches expectedOutput.
     * <br/>If used, the using class must @ExtendWith(PrintInterceptor.class)
     * @param expectedOutput the output expected
     * @throws UnsupportedOperationException if PrintInterceptor was not intercepted
     */
    public static void assertEqualsPrint(String expectedOutput){
        assertEqualsPrint(expectedOutput, false);
    }
    /**
     * Assert that there is no output from the intercepted System.out/System.err.
     * <br/>If used, the using class must @ExtendWith(PrintInterceptor.class)
     * @param peek if true, this method will not consume the print output (can be used my another method)
     * @throws UnsupportedOperationException if PrintInterceptor was not intercepted
     */
    public static void assertNoOutput(boolean peek){
        assertEqualsPrint("", peek);
    }
    /**
     * Assert that the intercepted System.out/System.err output (consumed) matches expectedOutput.
     * <br/>If used, the using class must @ExtendWith(PrintInterceptor.class)
     * @throws UnsupportedOperationException if PrintInterceptor was not intercepted
     */
    public static void assertNoOutput(){
        assertEqualsPrint("");
    }
    /**
     * Assert that the 2 arrays are equivalent and that the "actual" array is sorted by a comparator.
     * Equivalency means that the arrays have the same length and every element in the "actual" array has a corresponding
     * element in the "expected" array.
     * @param <T>        the type parameter
     * @param expected   the expected values to be in actual
     * @param actual     the actual, sorted array
     * @param comparator the comparator
     */
    public static <T> void assertArraysEquivalentAndSorted(T[] expected, T[] actual, Comparator<T> comparator){
        assertArraysEquivalentAndSorted(Arrays.stream(expected), actual, comparator);
    }
    /**
     * Assert that the 2 arrays are equivalent and that the "actual" array is sorted by a comparator.
     * Equivalency means that the arrays have the same length and every element in the "actual" array has a corresponding
     * element in the "expected" array.
     * @param <T>        the type parameter
     * @param expected   the expected values to be in actual
     * @param actual     the actual, sorted array
     * @param comparator the comparator
     */
    public static <T> void assertArraysEquivalentAndSorted(Collection<T> expected, T[] actual, Comparator<T> comparator){
        assertArraysEquivalentAndSorted(expected.stream(), actual, comparator);
    }
    private static <T> void assertArraysEquivalentAndSorted(Stream<T> expected, T[] actual, Comparator<T> comparator){
        Map<T, Integer> keysMap = expected
                .collect(Collectors.groupingBy(Function.identity(), Collectors.summingInt(e->1)));
        for(int i = 0; i < actual.length; i++){
            T element = actual[i];
            if(keysMap.containsKey(element)){
                int count = keysMap.get(element);
                if(count > 1) keysMap.put(element, keysMap.get(element) - 1);
                else keysMap.remove(element);
            } else {
                throw new AssertionFailedError("Can't find " + i + "th element " + element + " in expected");
            }
        }
        if(!keysMap.isEmpty()){
            throw new AssertionFailedError("Can't find elements in actual. Elements: " + Arrays.toString(keysMap.keySet().toArray()));
        }
        for(int i = 0; i < actual.length - 1; i++){
            assertTrue(comparator.compare(actual[i], actual[i+1]) <= 0, "Element at index " + i + " is greater than the element after it");
        }
    }
    /**
     * Assert that 2 sets are equivalent, with exact difference messages if they aren't
     * @param expected
     * @param actual
     * @param <T>
     */
    public static <T> void assertSetsEquivalent(Set<T> expected, Set<T> actual){
        Set<T> toLoop = expected;  //loop over the larger one (since def has a wrong one), or random
        Set<T> toCheck = actual;
        if(expected.size() != actual.size()){
            toLoop = expected.size() > actual.size() ? expected : actual;
            toCheck = expected.size() > actual.size() ? actual : expected;
        }
        for(T obj: toLoop){
            if(!toCheck.contains(obj)){
                throw new AssertionFailedError(String.format("Set %s does not include an element from %s. Element: %s",
                        toCheck == actual ? "actual" : "expected", toLoop == actual ? "actual" : "expected", obj));
            }
        }
        //if pass, by Set requirements, does not contain the value
    }
    public static <T> void assertEmpty(Collection<T> collection){
        assertTrue(collection.isEmpty(), "Collection is not empty. Contains " + collection.size() + " elements. Elements are: " + Arrays.toString(collection.toArray()));
    }
    public static <T> void assertEmpty(T[] arr){
        assertEquals(0, arr.length, "Collection is not empty. Contains " + arr.length + " elements. Elements are: " + Arrays.toString(arr));
    }
    public static void assertEmpty(int[] arr){
        assertEquals(0, arr.length, "Collection is not empty. Contains " + arr.length + " elements. Elements are: " + Arrays.toString(arr));
    }
    public static void assertEmpty(long[] arr){
        assertEquals(0, arr.length, "Collection is not empty. Contains " + arr.length + " elements. Elements are: " + Arrays.toString(arr));
    }
    public static void assertEmpty(char[] arr){
        assertEquals(0, arr.length, "Collection is not empty. Contains " + arr.length + " elements. Elements are: " + Arrays.toString(arr));
    }
    public static void assertEmpty(short[] arr){
        assertEquals(0, arr.length, "Collection is not empty. Contains " + arr.length + " elements. Elements are: " + Arrays.toString(arr));
    }
    /**
     * Negate an assertion using an executable function
     * @param executable
     */
    public static void assertNot(org.junit.jupiter.api.function.Executable executable){
        assertThrows(AssertionFailedError.class, executable);
    }

    public static void assert2DArraysEquivalent(int[][] expected, int[][] actual){
        assert2DArraysEquivalent(expected, actual, "");
    }
    public static void assert2DArraysEquivalent(int[][] expected, int[][] actual, String s){
        if(s != null) s = s + ". ";
        assertEquals(expected.length, actual.length, "Actual row count does not match expected. Should be " + expected.length + ", is " + actual.length);
        for(int i = 0; i < actual.length; i++){
            assertEquals(expected[i].length, actual[i].length, "Actual length does not match expected at index " + i + ". Should be " + expected.length + ", is " + actual.length);
            for(int j = 0; j < actual[i].length; j++){
                assertEquals(expected[i][j], actual[i][j], s + String.format("2D arrays not equal at i=%d, j=%d", i, j));
            }
        }
    }

    public static void assert2DArraysEquivalent(long[][] expected, long[][] actual){
        assert2DArraysEquivalent(expected, actual, "");
    }
    public static void assert2DArraysEquivalent(long[][] expected, long[][] actual, String s){
        if(s != null) s = s + ". ";
        assertEquals(expected.length, actual.length, "Actual row count does not match expected. Should be " + expected.length + ", is " + actual.length);
        for(int i = 0; i < actual.length; i++){
            assertEquals(expected[i].length, actual[i].length, "Actual length does not match expected at index " + i + ". Should be " + expected.length + ", is " + actual.length);
            for(int j = 0; j < actual[i].length; j++){
                assertEquals(expected[i][j], actual[i][j], s + String.format("2D arrays not equal at i=%d, j=%d", i, j));
            }
        }
    }

    public static void assert2DArraysEquivalent(double[][] expected, double[][] actual){
        assert2DArraysEquivalent(expected, actual, "");
    }
    public static void assert2DArraysEquivalent(double[][] expected, double[][] actual, String s){
        if(s != null) s = s + ". ";
        assertEquals(expected.length, actual.length, "Actual row count does not match expected. Should be " + expected.length + ", is " + actual.length);
        for(int i = 0; i < actual.length; i++){
            assertEquals(expected[i].length, actual[i].length, "Actual length does not match expected at index " + i + ". Should be " + expected.length + ", is " + actual.length);
            for(int j = 0; j < actual[i].length; j++){
                assertEquals(expected[i][j], actual[i][j], s + String.format("2D arrays not equal at i=%d, j=%d", i, j));
            }
        }
    }

    public static void assert2DArraysEquivalent(char[][] expected, char[][] actual){
        assert2DArraysEquivalent(expected, actual, "");
    }
    public static void assert2DArraysEquivalent(char[][] expected, char[][] actual, String s){
        if(s != null) s = s + ". ";
        assertEquals(expected.length, actual.length, "Actual row count does not match expected. Should be " + expected.length + ", is " + actual.length);
        for(int i = 0; i < actual.length; i++){
            assertEquals(expected[i].length, actual[i].length, "Actual length does not match expected at index " + i + ". Should be " + expected.length + ", is " + actual.length);
            for(int j = 0; j < actual[i].length; j++){
                assertEquals(expected[i][j], actual[i][j], s + String.format("2D arrays not equal at i=%d, j=%d", i, j));
            }
        }
    }

    public static <T> void assert2DArraysEquivalent(T[][] expected, T[][] actual){
        assert2DArraysEquivalent(expected, actual, "");
    }
    public static <T> void assert2DArraysEquivalent(T[][] expected, T[][] actual, String s){
        if(s != null) s = s + ". ";
        assertEquals(expected.length, actual.length, "Actual row count does not match expected. Should be " + expected.length + ", is " + actual.length);
        for(int i = 0; i < actual.length; i++){
            assertEquals(expected[i].length, actual[i].length, "Actual length does not match expected at index " + i + ". Should be " + expected.length + ", is " + actual.length);
            for(int j = 0; j < actual[i].length; j++){
                assertEquals(expected[i][j], actual[i][j], s + String.format("2D arrays not equal at i=%d, j=%d", i, j));
            }
        }
    }
}
