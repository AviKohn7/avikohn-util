package dev.avikohn.util.testutil.comparestructures;

import java.util.*;
import java.io.IOException;
import java.util.function.Function;
import java.util.stream.Collectors;

import static dev.avikohn.util.Util.normalizeLineSeparator;

//todo: filter out fully qualified names (careful abt inner classes)
public class StructurePrinter{
	public static final int INDENTATION = 2;
	//failed because java generics
	// public static <S,U> StructurePrinter getFromComparers(Class<S> leftClass, Class<U> rightClass, ContentComparer<?,S,U>... comparers){
	// 	return new StructurePrinter(Arrays.stream(comparers).map(e->e.compare(leftClass, rightClass)).toArray(ComparisonResult<?,?>[]::new));
	// }

	private final ComparisonResult<?,?>[] compResults;
	private PrinterSettings settings = PrinterSettings.DEFAULT;

	public StructurePrinter(ComparisonResult<?,?>... compResults){
		if(compResults.length == 0){
			throw new IllegalArgumentException("There must be at least one ComparisonResult given");
		}
		this.compResults = compResults;
	}

	 public void setSettings(PrinterSettings settings){
		 if(settings == null) throw new IllegalArgumentException("StructurePrinter settings may not be null");
		 this.settings = settings;
	 }

	public void print(){
		checkIdentical();
		printInfo();
	}
	private void checkIdentical(){
		try{
			if(isIdentical()){
				appendLine("The classes structures are identical! (as tested)");
				appendLine("");
			} else {
				if(!ConstructorComparer.getMethodParameterslessClasses().isEmpty()){
					appendLine("Some classes aren't compiled with -parameters, causing issues with InnerClassComparer (for java < 21)");
					appendLine("Please turn off InnerClassComparer or recompile the following classes with -parameters: " + String.join(", ", ConstructorComparer.getMethodParameterslessClasses().toArray(new String[0])));
					if(ConstructorComparer.GUESS_EXTRA_PARAMETER) appendLine("GUESS_EXTRA_PARAMETER is on, synthetic and implicit parameters have been guessed");
					else appendLine("Turn on ConstructorComparer.GUESS_EXTRA_PARAMETER to guess synthetic and implicit parameters");
					appendLine("");
					ConstructorComparer.getMethodParameterslessClasses().clear();
				}
			}
		} catch(IOException e){
			System.out.println("Appending failed");
		}
	}
	private boolean isIdentical(){
		for(ComparisonResult<?,?> compResult: compResults){
			if(!compResult.isIdentical()){
				return false;
			}
		}
		return true;
	}
	private void printInfo(){
		printSection("Intersection", 0, ComparisonResult::intersection);
		printSection("Left Unique Items", 0,  ComparisonResult::leftDifference); //todo: change items to different word
		printSection("Right Unique Items", 0, ComparisonResult::rightDifference);
	}
	public void printSection(String title, int indentationLevel, Function<ComparisonResult<?,?>,List<String>> listApplier){
		try{
			appendLineIndentationLevel(title, indentationLevel);
			appendLineIndentationLevel("================", indentationLevel);
			if(!isAllEmpty(listApplier)){
				for(ComparisonResult<?,?> compResult: compResults){
					List<String> list = listApplier.apply(compResult);
					if(list.isEmpty() && settings.hideEmptyComparers()) continue;
					else printComparer(indentationLevel+1, compResult, list);
				}
				appendLineIndentationLevel("", indentationLevel);
			} else {
				appendLineIndentationLevel("No Relevant Information Found", indentationLevel+1);
				appendLine("");
			}
		} catch(IOException e){
			System.out.println("Appending failed");
		}
	}
	private boolean isAllEmpty(Function<ComparisonResult<?,?>,List<String>> listApplier){
		for(ComparisonResult<?,?> compResult: compResults){
			if(!listApplier.apply(compResult).isEmpty()){
				return false;
			}
		}
		return true;
	}
	private <T,S> void printComparer(int indentationLevel, ComparisonResult<? extends T,? extends S> compResult, List<String> list) throws IOException {

		String title = getSubsectionTitle(compResult);
		if(!list.isEmpty()){
			if(compResult.isStaticFlag().includesFlag(Flag.STATIC_AND_INSTANCE)){
				appendLineIndentationLevel(title, indentationLevel);

				List<String> staticStuff = list.stream().filter(e->e.length() > 6 && e.startsWith("static")).map(e->e.substring(7)).collect(Collectors.toList()); //substring 7 to get rid of the space
				List<String> instanceStuff = list.stream().filter(e->e.length() <= 6 || !e.startsWith("static")).collect(Collectors.toList());
				
				if(!staticStuff.isEmpty()) printInlineSubSection("Static", indentationLevel+1, stringifyComparisonList(staticStuff));
				if(!instanceStuff.isEmpty()) printInlineSubSection("Instance", indentationLevel+1, stringifyComparisonList(instanceStuff));
			} else {
				printSubSection(title, indentationLevel, stringifyComparisonList(list));
			}
		} else {
			printSubSection(title, indentationLevel, "No relevant members found");
		}
	}
	//maybe remove, idk
	private <T,S> String getSubsectionTitle(ComparisonResult<? extends T,? extends S> compResult){
		if(compResult.isStaticFlag().includesFlag(Flag.STATIC_AND_INSTANCE)){
			return String.format("%s (%s, %s) testing %s values", compResult.name(), compResult.leftClass().getName(), compResult.rightClass().getName(), compResult.isStaticFlag());
		} else {
			String flagString = compResult.isStaticFlag().includesFlag(Flag.STATIC) ? "Static" : "Instance";
			return String.format("%s (%s, %s). %s values", compResult.name(), compResult.leftClass().getName(), compResult.rightClass().getName(), flagString);
		}
	}
	private void printSubSection(String title, int indentationLevel, String content) throws IOException {
		appendLineIndentationLevel(title, indentationLevel);
		appendLineIndentationLevel(content, indentationLevel+1);
	}

	/**
	 * Prints a subsection inline if possible (no line breaks in content) and falls back to printSubSection if not
	 * @param title the title of the subsection
	 * @param indentationLevel the indentationLevel of the subsection
	 * @param content the content to be printed for the subsection
	 * @throws IOException
	 */
	private void printInlineSubSection(String title, int indentationLevel, String content) throws IOException {
		if(!content.contains("\n")) appendLineIndentationLevel(title+": "+content, indentationLevel);
		else printSubSection(title+":", indentationLevel, content);
	}
	private String stringifyComparisonList(List<String> list){
		return String.join(", ", list);
	}
	private String tryFilterPackages(String str){
		if(settings.filterPackages()){
			str = str.replaceAll("([a-zA-Z_0-9]+[.])+", "");
		}
		return str;
	}
	private void append(String input) throws IOException {
		settings.getAppender().append(tryFilterPackages(input));
	}
	private void appendLine(String input) throws IOException {
		append(input+System.lineSeparator());
	}
	private void appendIndentationLevel(String input, int indentationLevel) throws IOException {
		append(normalizeLineSeparator(indentString(input, INDENTATION *indentationLevel)));
	}
	private void appendLineIndentationLevel(String input, int indentationLevel) throws IOException {
		appendIndentationLevel(input+System.lineSeparator(), indentationLevel);
	}
	//the String.indent function, just not with the extra \n
	private String indentString(String str, int n){
		String toReturn = str.indent(n);
		return toReturn.substring(0, toReturn.length() != 0 && str.charAt(str.length()-1) != '\n'? toReturn.length() - 1 : toReturn.length()); //remove the extra newline
	}
}