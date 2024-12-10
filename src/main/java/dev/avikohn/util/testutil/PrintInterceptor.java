package dev.avikohn.util.testutil;

import java.nio.charset.StandardCharsets;
import java.io.*;

import org.junit.jupiter.api.extension.*;

//incompatible with multi-threading
public class PrintInterceptor implements AfterEachCallback, BeforeEachCallback, BeforeAllCallback, AfterAllCallback{
	private static PrintStream oldSysOut = System.out;
    private static PrintStream oldSysErr = System.err;
    private static ByteArrayOutputStream newSysOut;
    private static boolean intercepted = false;

    @Override
    public void beforeEach(ExtensionContext context) throws Exception{
        clearPrintOutput();
    }
    @Override
    public void afterEach(ExtensionContext context) throws Exception{
        boolean testFailed = context.getExecutionException().isPresent();
        if(testFailed){
            oldSysOut.println("\nOutput stream content: \n[" + getPrintOutput()+"]");
        }
        String output = getPrintOutput(); //in any case, clear the output
    }

    @Override
    public void beforeAll(ExtensionContext context) throws Exception{
        interceptPrint();
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception{
        restorePrint();
    }

    private static void interceptPrint(){
        if(newSysOut == null){
            oldSysOut = System.out;
            newSysOut = new ByteArrayOutputStream();
            PrintStream sysOut = new PrintStream(newSysOut, false, StandardCharsets.UTF_8);
            System.setOut(sysOut);
            System.setErr(sysOut); //maybe change?
            intercepted = true;
        }
    }

    private static void restorePrint(){
        newSysOut.reset();
        newSysOut = null;
        System.setOut(oldSysOut);
        System.setErr(oldSysErr);
    }
    /**
     * Returns and clears the current print output
     * @return the current print output
     */
    public static String getPrintOutput(){
        String output = newSysOut.toString();
        clearPrintOutput();
        return output;
    }
    /**
     * Clears the new System.out output stream
     */
    public static void clearPrintOutput(){
        newSysOut.reset();
    }
    /**
     * Returns the current print output without clearing
     * @return the current print output
     */
    public static String peekPrintOutput(){
        return newSysOut.toString();
    }
    /**
     * Returns the original system output stream.
     * Use this to print to the console without interception.
     * @return the original system output stream
     */
    public static PrintStream getOldSysOut(){
        return oldSysOut;
    }
    /**
     * Returns the new system output stream
     * @return the new system output stream
     */
    public static ByteArrayOutputStream getNewSysOut(){
        return newSysOut;
    }

    /**
     * @return if the print has been intercepted
     */
    public static boolean isIntercepted(){
        return intercepted;
    }
}