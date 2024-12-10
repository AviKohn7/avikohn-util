package dev.avikohn.util.testutil;

import static dev.avikohn.util.testutil.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(PrintInterceptor.class)
public class PrintInterceptorTest{

    @Test
    public void testCaughtOutput() {
        System.out.println("catch plz");
        assertEqualsPrintln("catch plz");
        System.out.print("catch plz?");
        assertEqualsPrint("catch plz?");
    }
}
