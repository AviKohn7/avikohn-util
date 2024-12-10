package dev.avikohn.util.testutil.comparestructures;

import java.util.*;
import java.util.function.*;

public class ClassComparison<T,S>{
	private final Class<T> leftClass;
	private final Class<S> rightClass;
	private final Class<?>[] comparersToSkip;
	private List<ComparisonResult<T,S>> prevResults;
	private Flag accessFlag = ContentComparer.DEFAULT_ACCESS_FLAG;
	private UnaryOperator<String> leftStringModifier = (e)->e;
	private UnaryOperator<String> rightStringModifier = (e)->e;
	public ClassComparison(Class<T> leftClass, Class<S> rightClass, Class<?>... comparersToSkip){
		this.leftClass = leftClass;
		this.rightClass = rightClass;
		this.comparersToSkip = comparersToSkip;
	}
	public ClassComparison<T,S> setAccessFlag(Flag accessFlag){
		this.accessFlag = accessFlag;
		return this;
	}
	public ClassComparison<T,S> setStringModifier(UnaryOperator<String> stringModifier){
		this.leftStringModifier = stringModifier;
		this.rightStringModifier = stringModifier;
		return this;
	}
	public ClassComparison<T,S> setLeftStringModifier(UnaryOperator<String> stringModifier){
		this.leftStringModifier = stringModifier;
		return this;
	}
	public ClassComparison<T,S> setRightStringModifier(UnaryOperator<String> stringModifier){
		this.rightStringModifier = stringModifier;
		return this;
	}
	public void compareAndPrint(){
		compareAndPrint(PrinterSettings.DEFAULT);
	}
	public void compareAndPrint(PrinterSettings settings){
		compare();
		printResults(settings);
	}
	public List<ComparisonResult<T,S>> compare(){
		List<ComparisonResult<T,S>> comparers = new ArrayList<>();
		if(shouldRunComparer(NestedClassComparer.class)) comparers.add(compareSingle(new NestedClassComparer<>(accessFlag)));
		if(shouldRunComparer(FieldComparer.class)) comparers.add(compareSingle(new FieldComparer<>(accessFlag)));
		if(shouldRunComparer(MethodComparer.class)) comparers.add(compareSingle(new MethodComparer<>(accessFlag)));
		if(shouldRunComparer(ConstructorComparer.class)) comparers.add(compareSingle(new ConstructorComparer<>(accessFlag)));
		
		prevResults = comparers;
		return prevResults;
	}
	private boolean shouldRunComparer(Class<?> comparerClass){
		return Arrays.stream(comparersToSkip).noneMatch(e->e.getName().equals(comparerClass.getName()));
	}
	private ComparisonResult<T,S> compareSingle(ContentComparer<?,T,S> comparer){
		comparer.setLeftModifyStringFunc(leftStringModifier);
		comparer.setRightModifyStringFunc(rightStringModifier);
		return comparer.compare(leftClass, rightClass);
	}
	public void printResults(){
		printResults(PrinterSettings.DEFAULT);
	}
	public void printResults(PrinterSettings settings){
		StructurePrinter printer = createPrinter();
		printer.setSettings(settings);
		printer.print();
	}
	public String getResultsString(){
		return getResultsString(PrinterSettings.DEFAULT);
	}
	public String getResultsString(PrinterSettings settings){
		StructurePrinter printer = createPrinter();
		StringBuilder builder = new StringBuilder();
		printer.setSettings(settings.withAppender(builder));
		printer.print();
		return builder.toString();
	}
	/**
	 * @return a printer with the ComparisonResults from this comparison (appender and hideEmptyComparers as default)
	 */
	public StructurePrinter createPrinter(){
		if(prevResults == null){
			throw new UnsupportedOperationException("Comparison must be done before printing");
		}
		return new StructurePrinter(prevResults.toArray(ComparisonResult<?,?>[]::new));
	}
	public boolean isIdentical(){
		return isPartiallyIdentical(cr->true);
	}
	public boolean isPartiallyIdentical(Predicate<ComparisonResult<T,S>> filterFunc){
		if(prevResults == null){
			throw new UnsupportedOperationException("Must compare before checking if the classes are identical");
		}
		return prevResults.stream().filter(filterFunc).allMatch(res->res.isIdentical());
	}
	public List<ComparisonResult<T,S>> getComparisonResults(){
		return prevResults;
	}
}