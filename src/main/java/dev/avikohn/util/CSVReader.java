package dev.avikohn.util;

import java.io.IOException;
import java.nio.file.*;
import java.util.Arrays;
import java.util.List;
import java.util.function.*;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * A class for reading of a single-type CSV file (see CSVBuilder for creating a CSV file).<br/>
 * Use one of the readX() methods for reading primitive 2D-arrays,
 * or read(parser) to convert to an arbitrary datatype (with the ability to use arrays instead of lists)
 * <br/><br/>
 * Use readLineCustom(parser) if you would like to define how exactly to read a CSV line (instead of parsing it element-wise)
 * <br/><br/>
 * You can change the delimiter with setDelimiter, default is a comma
 */
public class CSVReader{
    private String delimiter = ",";
    private final Path csvPath;
    public CSVReader(Path path){
        csvPath = path;
    }
    public CSVReader(String folder, String name){
        this(Path.of(folder, name + ".csv"));
    }
    public CSVReader(String name){
        this("", name);
    }
    public CSVReader setDelimiter(String delimiter){
        this.delimiter = delimiter;
        return this;
    }
    public <T> char[][] readChar() throws IOException{
        return readLines(e->
                e.collect(StringBuilder::new, StringBuilder::append, StringBuilder::append) // Collect into a StringBuilder
                .toString() // Convert StringBuilder to String
                .toCharArray()).toArray(char[][]::new);
    }
    public <T> double[][] readDouble() throws IOException{
        return readLines(e->e.mapToDouble(Double::parseDouble).toArray())
                .toArray(double[][]::new);
    }
    public <T> long[][] readLong() throws IOException{
        return readLines(e->e.mapToLong(Long::parseLong).toArray())
                .toArray(long[][]::new);
    }
    public <T> int[][] readInt() throws IOException{
        return readLines(e->e.mapToInt(Integer::parseInt).toArray())
                .toArray(int[][]::new);
    }
    private <T> List<T> readLines(Function<Stream<String>, T> mapper)
            throws IOException{
        try(Stream<String> lines = Files.lines(csvPath)) {
            return lines.map(line ->
                    mapper.apply(Arrays.stream(line.split(Pattern.quote(delimiter))))).toList();
        }
    }
    public <T> List<List<T>> read(Function<String, T> parser)
        throws IOException{
        return readLines(e->e.map(parser).toList());
    }
    public <T> List<T[]> read(Function<String, T> parser, IntFunction<T[]> arrayGenerator)
            throws IOException{
        return readLines(e->e.map(parser).toArray(arrayGenerator));
    }
    public <T> T[][] read(Function<String, T> parser,
                              IntFunction<T[]> arrayGenerator,
                              IntFunction<T[][]> twoDArrayGenerator)
            throws IOException{
        return readLines(e->e.map(parser).toArray(arrayGenerator)).toArray(twoDArrayGenerator);
    }

    public <T> List<T> readLineCustom(Function<String[], T> parser)
            throws IOException{
        return readLines(e->parser.apply(e.toArray(String[]::new)));
    }
}
