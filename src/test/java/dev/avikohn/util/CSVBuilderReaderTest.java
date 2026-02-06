package dev.avikohn.util;

import dev.avikohn.util.testutil.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CSVBuilderReaderTest{
    private static final String FOLDER = "src/test/tempTestFiles";
    private final int[][] inputValues = {{1,2,3}, {4,5,6}, {7,8,9}, {10,11,12}, {13,14,15}};
    private record Point(int x, int y){
        @Override
        public String toString(){
            return x + "|" + y;
        }
        public static Point parse(String s){
            if(!s.matches("[0-9]+\\|[0-9]+")){
                throw new IllegalArgumentException("Invalid parse string");
            } else {
                String[] parts = s.split("\\|");
                return new Point(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
            }

        }
    }
    //#region util
    private int[][] getIntVals(){
        return inputValues;
    }
    private long[][] getLongVals(){
        return Stream.of(inputValues)
                .map(e->
                        IntStream.of(e).mapToLong(i->(long)i).toArray())
                .toArray(long[][]::new);
    }
    private double[][] getDoubleVals(){
        double[][] doubleVals = new double[inputValues.length][];
        double addFactor = 0;
        for(int i = 0; i < doubleVals.length; i++){
            doubleVals[i] = new double[inputValues[i].length];
            for(int j = 0; j < doubleVals[i].length; j++){
                doubleVals[i][j] = inputValues[i][j] + addFactor;
            }
            addFactor += .1;
        }
        return doubleVals;
    }
    private char[][] getCharVals(){
        char[][] charVals = new char[inputValues.length][];
        char addFactor = 'a' - 1;
        for(int i = 0; i < charVals.length; i++){
            charVals[i] = new char[inputValues[i].length];
            for(int j = 0; j < charVals[i].length; j++){
                charVals[i][j] = (char) (inputValues[i][j] + addFactor);
            }
        }
        return charVals;
    }
    private Point[][] getPointVals(){
        return Stream.of(inputValues)
                .map(e->
                        IntStream.of(e).mapToObj(i->new Point(i, i*i)).toArray(Point[]::new))
                .toArray(Point[][]::new);
    }
    private void setIntBuilder(CSVBuilder builder){
        int[][] vals = getIntVals();
        for(int i = 0; i < 3; i++){
            builder.addLine(vals[i]);
        }
        int[][] newVals = new int[vals.length - 3][];
        System.arraycopy(vals, 3, newVals, 0, vals.length - 3);
        builder.addLines(newVals);
    }
    private CSVBuilder getIntBuilder(){
        CSVBuilder builder = new CSVBuilder();
        setIntBuilder(builder);
        return builder;
    }
    private CSVBuilder getLongBuilder(){
        CSVBuilder builder = new CSVBuilder();
        long[][] vals = getLongVals();
        for(int i = 0; i < 3; i++){
            builder.addLine(vals[i]);
        }
        long[][] newVals = new long[vals.length - 3][];
        System.arraycopy(vals, 3, newVals, 0, vals.length - 3);
        builder.addLines(newVals);
        return builder;
    }
    private CSVBuilder getDoubleBuilder(){
        CSVBuilder builder = new CSVBuilder();
        double[][] vals = getDoubleVals();
        for(int i = 0; i < 3; i++){
            builder.addLine(vals[i]);
        }
        double[][] newVals = new double[vals.length - 3][];
        System.arraycopy(vals, 3, newVals, 0, vals.length - 3);
        builder.addLines(newVals);
        return builder;
    }
    private CSVBuilder getCharBuilder(){
        CSVBuilder builder = new CSVBuilder();
        char[][] vals = getCharVals();
        for(int i = 0; i < 3; i++){
            builder.addLine(vals[i]);
        }
        char[][] newVals = new char[vals.length - 3][];
        System.arraycopy(vals, 3, newVals, 0, vals.length - 3);
        builder.addLines(newVals);
        return builder;
    }

    private CSVBuilder getPointBuilder(){
        CSVBuilder builder = new CSVBuilder();
        Point[][] vals = getPointVals();
        for(int i = 0; i < 3; i++){
            builder.addLine(vals[i]);
        }
        Point[][] newVals = new Point[vals.length - 3][];
        System.arraycopy(vals, 3, newVals, 0, vals.length - 3);
        builder.addLines(newVals);
        return builder;
    }

    //#endregion

    @AfterAll
    public static void cleanup(){
        Path folder = Path.of(FOLDER);
        for(File f: folder.toFile().listFiles()){
            f.delete();
        }
    }
    @Test
    public void testCreateFile() throws IOException{
        CSVBuilder builder = getIntBuilder();
        boolean success = builder.save(FOLDER, "builder", true);
        assertTrue(success, "1 failed");

        boolean success2 = builder.save(FOLDER, "builder2", false);
        assertTrue(success2, "2 failed");

        CSVReader reader1 = new CSVReader(FOLDER, "builder");
        CSVReader reader2 = new CSVReader(FOLDER, "builder2");

        Assertions.assert2DArraysEquivalent(getIntVals(), reader1.readInt());
        Assertions.assert2DArraysEquivalent(getIntVals(), reader2.readInt());
    }
    @Test
    public void testIntFile() throws IOException{
        CSVBuilder builder = getIntBuilder();
        String name = "BuilderInt";
        boolean success = builder.save(FOLDER, name, true);
        assertTrue(success, "Int failed");

        CSVReader reader1 = new CSVReader(FOLDER, name);

        Assertions.assert2DArraysEquivalent(getIntVals(), reader1.readInt());
    }
    @Test
    public void testLongFile() throws IOException{
        CSVBuilder builder = getLongBuilder();
        String name = "BuilderLong";
        boolean success = builder.save(FOLDER, name, true);
        assertTrue(success, "Long failed");

        CSVReader reader1 = new CSVReader(FOLDER, name);

        Assertions.assert2DArraysEquivalent(getLongVals(), reader1.readLong());
    }
    @Test
    public void testDoubleFile() throws IOException{
        CSVBuilder builder = getDoubleBuilder();
        String name = "BuilderDouble";
        boolean success = builder.save(FOLDER, name, true);
        assertTrue(success, "Double failed");

        CSVReader reader1 = new CSVReader(FOLDER, name);

        Assertions.assert2DArraysEquivalent(getDoubleVals(), reader1.readDouble());
    }
    @Test
    public void testCharFile() throws IOException{
        CSVBuilder builder = getCharBuilder();
        String name = "BuilderChar";
        boolean success = builder.save(FOLDER, name, true);
        assertTrue(success, "Char failed");

        CSVReader reader1 = new CSVReader(FOLDER, name);

        Assertions.assert2DArraysEquivalent(getCharVals(), reader1.readChar());
    }

    @Test
    public void testPointFile() throws IOException{
        CSVBuilder builder = getPointBuilder();
        String name = "BuilderPoint";
        boolean success = builder.save(FOLDER, name, true);
        assertTrue(success, "Point failed");

        CSVReader reader1 = new CSVReader(FOLDER, name);

        Assertions.assert2DArraysEquivalent(getPointVals(), reader1.read(Point::parse, Point[]::new, Point[][]::new));
    }

    @Test
    public void testSingleLine() throws IOException{
        CSVBuilder builder = new CSVBuilder();
        String name = "SingleLine";
        builder.addLine(inputValues[0]);
        boolean success = builder.save(FOLDER, name, true);
        assertTrue(success, "Single Line failed");

        CSVReader reader = new CSVReader(FOLDER, name);
        Assertions.assert2DArraysEquivalent(new int[][]{inputValues[0]}, reader.readInt());
    }

    @Test
    public void testSingleInput() throws IOException{
        CSVBuilder builder = new CSVBuilder();
        String name = "SingleInput";
        builder.addLine(inputValues[0][0]);
        boolean success = builder.save(FOLDER, name, true);
        assertTrue(success, "Single Input failed");

        CSVReader reader = new CSVReader(FOLDER, name);
        Assertions.assert2DArraysEquivalent(new int[][]{{inputValues[0][0]}}, reader.readInt());
    }

    @Test
    public void testCustomDelimiter() throws IOException{
        CSVBuilder builder = new CSVBuilder("|");
        setIntBuilder(builder);
        String name = "BuilderDelim";
        boolean success = builder.save(FOLDER, name, true);
        assertTrue(success, "Delim failed");

        CSVReader reader1 = new CSVReader(FOLDER, name).setDelimiter("|");

        Assertions.assert2DArraysEquivalent(getIntVals(), reader1.readInt());
    }
}
