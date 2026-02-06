package dev.avikohn.util;

import java.io.IOException;
import java.nio.file.*;

/**
 * A class to handle incremental building of a CSV file (see CSVReader for CSV parsing).<br/>
 * Use the addLine(primitive...) or addLine(T[]) to add lines to the file,
 * or addLines(Type[][]) to add multiple lines at a time.<br/>
 * While type mixing is allowed, it is not recommended as it makes parsing more difficult (and not
 * supported by the CSVReader class)
 * <br/><br/>
 * Call one of the save methods to save the data to a file
 */
public class CSVBuilder{
    private final String separator;
    private static final String LINE_SEPARATOR = System.lineSeparator();
    private StringBuilder data;
    public CSVBuilder(){
        this(",");
    }
    public CSVBuilder(String separator){
        data = new StringBuilder();
        this.separator = separator;
    }
    public void addLine(int ...x){
        data.append(x[0]);
        for(int i = 1; i < x.length; i++){
            data.append(separator).append(x[i]);
        }
        data.append(LINE_SEPARATOR);
    }
    public void addLine(long ...x){
        data.append(x[0]);
        for(int i = 1; i < x.length; i++){
            data.append(separator).append(x[i]);
        }
        data.append(LINE_SEPARATOR);
    }
    public void addLine(char ...x){
        data.append(x[0]);
        for(int i = 1; i < x.length; i++){
            data.append(separator).append(x[i]);
        }
        data.append(LINE_SEPARATOR);
    }
    public void addLine(double ...x){
        data.append(x[0]);
        for(int i = 1; i < x.length; i++){
            data.append(separator).append(x[i]);
        }
        data.append(LINE_SEPARATOR);
    }

    public final <T> void addLine(T[] x){
        data.append(x[0]);
        for(int i = 1; i < x.length; i++){
            data.append(separator).append(x[i]);
        }
        data.append(LINE_SEPARATOR);
    }
    public void addLines(int[][] lines){
        for(int[] line : lines){
            addLine(line);
        }
    }
    public void addLines(long[][] lines){
        for(long[] line : lines){
            addLine(line);
        }
    }
    public void addLines(char[][] lines){
        for(char[] line : lines){
            addLine(line);
        }
    }
    public void addLines(double[][] lines){
        for(double[] line : lines){
            addLine(line);
        }
    }
    public <T> void addLines(T[][] lines){
        for(T[] line : lines){
            addLine(line);
        }
    }
    /**
     * Save the csv data to a file
     * @param path the path to save the data to
     * @param rewrite True to rewrite the file, false to append
     * @return True if successful, false if not
     */
    public boolean save(Path path, boolean rewrite){
        try {
//            Path parent = path.getParent();
//            if (parent != null) {
//                Files.createDirectories(parent);
//            }
            Files.write(path, data.toString().getBytes(), StandardOpenOption.CREATE, rewrite ? StandardOpenOption.TRUNCATE_EXISTING : StandardOpenOption.APPEND);
            return true;
        } catch(IOException e){
            return false;
        }
    }
    /**
     * Save the csv data to a file
     * @param folder the name of the folder to save in
     * @param name the name of the resulting file
     * @param rewrite True to rewrite the file, false to append
     * @return True if successful, false if not
     */
    public boolean save(String folder, String name, boolean rewrite){
        if(name == null || name.isEmpty()){
            throw new IllegalArgumentException("Name must not be empty or null");
        }
        return save(Path.of(folder, name+".csv"), rewrite);
    }
    /**
     * Save the csv data to a file relative to the folder that the JDK is running in
     * @param name the name of the resulting file
     * @param rewrite True to rewrite the file, false to append
     * @return True if successful, false if not
     */
    public boolean save(String name, boolean rewrite){
        return save("", name, rewrite);
    }
}
