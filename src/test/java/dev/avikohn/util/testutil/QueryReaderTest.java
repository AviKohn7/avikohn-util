package dev.avikohn.util.testutil;

import dev.avikohn.util.testutil.params.XMLSource;
import org.junit.jupiter.params.ParameterizedTest;

import static dev.avikohn.util.testutil.Assertions.assertEqualsWithDiff;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class QueryReaderTest{
    @ParameterizedTest
    @XMLSource("src/test/testFiles/queryTest.txt")
    public void test(String input, String output, Integer i){
        assertEqualsWithDiff(input+"a\n", output);
    }
//    @ParameterizedTest
//    @XMLSource("src/test/testFiles/queryTest.txt")
//    public void testTrim(String input, String output, Integer i){
//        assertEqualsWithDiff(input+"a", output);
//    }
}
