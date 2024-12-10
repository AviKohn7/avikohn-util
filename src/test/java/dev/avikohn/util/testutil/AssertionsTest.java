package dev.avikohn.util.testutil;

import dev.avikohn.util.testutil.comparestructures.CompExample1;
import dev.avikohn.util.testutil.comparestructures.CompExample2;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

import static dev.avikohn.util.testutil.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class AssertionsTest{

    @Test
    void testAssertEqualsWithDiff(){
        assertThrows(AssertionFailedError.class,
                ()->Assertions.assertEqualsWithDiff("abcde a", "abcdep a"),
                "Difference found:\n<~abcde~><**abcdep**> a");
        assertEqualsWithDiff("abcde a", "abcde a");
    }
    @Test
    void testAssertEqualsWithDiffDistance(){
        assertThrows(AssertionFailedError.class, ()-> {
            assertEqualsWithDiff("""
                    d
                    d
                    d
                    db
                    da
                    da
                    d
                    da
                    da
                    ddb
                    d
                    d
                    d
                    d
                    d
                    """, """
                    d
                    d
                    d
                    db
                    da
                    da
                    deee
                    da
                    da
                    ddb
                    d
                    d
                    d
                    d
                    d
                    """, 3);
        }, """
            Difference found (~~ means in expected, ** means in actual):
            ...
            db\n
            da\n
            da\n
            <~d~><**deee**>\n
            da\n
            da\n
            ddb\n
            ...
            """);
        }
    @Test
    void testAssertStructuresIdentical(){
        //assertStructuresIdentical(CompExample1.class, CompExample2.class);
    }
}