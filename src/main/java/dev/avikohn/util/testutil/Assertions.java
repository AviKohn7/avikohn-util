package dev.avikohn.util.testutil;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.avikohn.util.DifferenceFinder;
import dev.avikohn.util.testutil.comparestructures.*;
import org.opentest4j.AssertionFailedError;

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
}
