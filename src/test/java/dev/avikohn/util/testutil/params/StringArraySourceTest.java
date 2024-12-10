package dev.avikohn.util.testutil.params;

import org.junit.jupiter.params.ParameterizedTest;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class StringArraySourceTest{
    @ParameterizedTest
    @StringArraySource(value={"ab,ac,fd","ab,ac,fd,sd","ab,ac:fd","ab:aa,"," ab,ac , fd"}, delimiter = ",|:")
    @StringArraySource(textBlock = """
            ab,ac,fd
            ab,ac,fd,sd
            ab,ac:fd
            ab:aa,
             ab,ac , fs
            """, delimiter = ",|:")
    public void testParamsWorks(String[] strs){
        assertTrue(fitsTest(strs), Arrays.toString(strs));
    }
    @ParameterizedTest
    @StringArraySource(value={"ab,ac","ab,ac,fd,sda","ab,ac,f d",""," ab,ac,d"})
    @StringArraySource(textBlock = """
            ab,ac
            ab,ac,fd,sda
            ab,ac:f d
            
             ab,ac , f
            """, delimiter = ",|:")
    public void testParamFails(String[] strs){
        assertFalse(fitsTest(strs), Arrays.toString(strs));
    }
    private boolean fitsTest(String[] strs){
        return strs.length >= 3 &&  Arrays.stream(strs).allMatch(s->s.matches("..") || s.isEmpty());
    }
}