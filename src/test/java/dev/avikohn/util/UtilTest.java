package dev.avikohn.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UtilTest{

    @Test
    void normalizeLineSeparatorWindows(){
        assertEquals("hi\r\naa",Util.normalizeLineSeparator("hi\naa", "\r\n"));
        assertEquals("hi\r\naa",Util.normalizeLineSeparator("hi\r\naa", "\r\n"));
        assertEquals("hi\r\naa",Util.normalizeLineSeparator("hi\r\r\r\naa", "\r\n"));
    }
    @Test
    void normalizeLineSeparatorMac(){
        assertEquals("hi\naa",Util.normalizeLineSeparator("hi\r\naa", "\n"));
        assertEquals("hi\naa",Util.normalizeLineSeparator("hi\r\r\naa", "\n"));
    }
}