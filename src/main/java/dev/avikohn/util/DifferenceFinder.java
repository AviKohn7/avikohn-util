package dev.avikohn.util;

import com.github.difflib.text.*;
import java.util.*;
import java.util.stream.Collectors;
public class DifferenceFinder{
	public static final DiffRowGenerator generator = DiffRowGenerator.create().showInlineDiffs(true).mergeOriginalRevised(true).inlineDiffByWord(true).oldTag(isStart ->isStart ? "<~" : "~>").newTag(isStart -> isStart ? "<**" : "**>").build();
	public static DifferenceFinder getDifference(String oldString, String newString){
		return new DifferenceFinder(oldString.replace("\r","\\r"), newString.replace("\r","\\r"));
	}
	private List<DiffRow> rows;
	private DifferenceFinder(String oldString, String newString){
		rows = generator.generateDiffRows(Arrays.asList(oldString.split("\n")),Arrays.asList(newString.split("\n")));
	}
	public static void main(String[] args){
		System.out.println(getDifference(args[0]
				.replace("\\n","\n"),args[1].replace("\\n","\n"))
				.getResult());
	}
	public String getResult(){
		String str = rows.stream()
				.map(DiffRow::getOldLine)
				.collect(Collectors.joining("\\n\n"));
		if(rows.stream().allMatch(row->row.getTag().equals(DiffRow.Tag.EQUAL))){
			str="Strings are identical";
		}
		return str;
	}
	/**
	 * Get the result with contiguous identical rows removed.
	 * @param rowsToInclude how many rows above and below a found difference to keep
	 * @return
	 */
	public String getResultShortened(int rowsToInclude){
		int[] distanceCache = getDistanceCache(rowsToInclude);

		String str = buildShortenedResult(distanceCache);

		if(str.isEmpty()) str = "Strings are identical";

		return str;
	}
	/**
	 * Gets an integer array representing the distance each element is from a diff-row
	 * An element to the right will have the correct distance value, but one to the left may be incorrect,
	 * but will be greater than 0
	 * @param distance
	 * @return
	 */
	private int[] getDistanceCache(int distance){
		int[] cache = new int[rows.size()];
		distance++; //increase so as to include the change itself
		for(int i = 0; i < cache.length; i++){
			if(!rows.get(i).getTag().equals(DiffRow.Tag.EQUAL)){
				//store values to the left
				int currentDist = distance;
				for(int j = i; j >= 0 && cache[j] == 0 && currentDist > 0; j--, currentDist--){
					cache[j] = currentDist;
				}
			} else if(i != 0 && cache[i-1] > 1){ //store new value to the right
				cache[i] = cache[i-1]-1;
			}
		}
		return cache;
	}

	/**
	 * Build a result using a distance cache from this.getDistanceCache(int)
	 * @param distanceCache result from this.getDistanceCache(int)
	 * @return a string representing shortened results
	 */
	private String buildShortenedResult(int[] distanceCache){
		StringBuilder toBuild = new StringBuilder();
		for(int i = 0; i < rows.size(); i++){
			if(distanceCache[i] > 0){
				if(i != 0 && distanceCache[i-1] == 0) toBuild.append("...\n");
				toBuild.append(rows.get(i).getOldLine()+"\\n\n");
				if(i != rows.size() - 1 && distanceCache[i+1] == 0) toBuild.append("...\n");
			}
		}
		return toBuild.toString();
	}
}