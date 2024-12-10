package dev.avikohn.util;

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
}
