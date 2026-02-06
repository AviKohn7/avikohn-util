package dev.avikohn.util;

import java.util.function.*;

public class Util{
    /**
     * Normalizes the string input with System.lineSeperator(), including removing extra \r (one for windows
     * and none for mac);
     * @param str a string
     * @return the string with all line breaks matching System.lineSeperator()
     * (assuming System.lineSeparator() returns either "\n" or "\r\n", else returns str);
     */
    public static String normalizeLineSeparator(String str){
        return normalizeLineSeparator(str, System.lineSeparator());
    }
    //for testing, normalizeLineSeparator but with a separator parameter
    protected static String normalizeLineSeparator(String str, String separator){
        if(separator.equals("\n")){
            return str.replaceAll("\r+\n", "\n");
        } else if(separator.equals("\r\n")){
            return str.replaceAll("\r*\n", "\r\n");
        } else {
            return str;
        }
    }

    public record TimeResult<T>(T value, long duration){}
    public record IntTimeResult(int value, long duration){}
    public record LongTimeResult(long value, long duration){}
    public record DoubleTimeResult(double value, long duration){}
    public record BooleanTimeResult(boolean value, long duration){}
    public static <T> TimeResult<T> testTime(Supplier<T> supplier){
        long start = System.nanoTime();
        T value = supplier.get();
        long duration = System.nanoTime() - start;
        return new TimeResult<>(value, duration);
    }
    public static long testTime(Runnable runnable){
        long start = System.nanoTime();
        runnable.run();
        return System.nanoTime() - start;
    }
    public static IntTimeResult testTime(IntSupplier supplier){
        long start = System.nanoTime();
        int value = supplier.getAsInt();
        long duration = System.nanoTime() - start;
        return new IntTimeResult(value, duration);
    }
    public static LongTimeResult testTime(LongSupplier supplier){
        long start = System.nanoTime();
        long value = supplier.getAsLong();
        long duration = System.nanoTime() - start;
        return new LongTimeResult(value, duration);
    }
    public static DoubleTimeResult testTime(DoubleSupplier supplier){
        long start = System.nanoTime();
        double value = supplier.getAsDouble();
        long duration = System.nanoTime() - start;
        return new DoubleTimeResult(value, duration);
    }
    public static BooleanTimeResult testTime(BooleanSupplier supplier){
        long start = System.nanoTime();
        boolean value = supplier.getAsBoolean();
        long duration = System.nanoTime() - start;
        return new BooleanTimeResult(value, duration);
    }
}
