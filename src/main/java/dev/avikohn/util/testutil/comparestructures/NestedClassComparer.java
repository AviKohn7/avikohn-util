package dev.avikohn.util.testutil.comparestructures;

import java.util.function.Function;
import java.util.stream.Stream;
import java.util.*;


public class NestedClassComparer<S,U> extends ContentComparer<Class<?>,S,U>{
	public NestedClassComparer(){
		super();
	}
	public NestedClassComparer(Flag accessFlag){
		super(accessFlag);
	}
	@Override
	public ComparisonResult<S,U> compare(Class<? extends S> leftClass, Class<? extends U> rightClass){
		ComparisonResult<S,U> defaultRes = super.compare(leftClass, rightClass);
		List<String> leftUniq = new ArrayList<>(defaultRes.leftDifference()); //make set
		List<String> rightUniq = new ArrayList<>(defaultRes.rightDifference()); //make set
		List<String> intersection = new ArrayList<>(); //make set

		for(String str: defaultRes.intersection()){
			Class<?> leftInnerClass = getInnerClass(leftClass, str);
			Class<?> rightInnerClass = getInnerClass(rightClass, str);;

			//IMPORTANT for recursion
			ClassComparison<?,?> comp = new ClassComparison<>(leftInnerClass, rightInnerClass).setAccessFlag(accessFlag);
			changeStringModifiers(comp, leftClass, rightClass);
			comp.compare();

			if(comp.isIdentical()){
				intersection.add(str);
			} else if(comp.isPartiallyIdentical(cr->!cr.name().equals(getName()))){ //all members are identical => the difference was in a lower inner class
				StructurePrinter printer = comp.createPrinter();
				leftUniq.add(getResultString(printer, str, ComparisonResult::leftDifference));
				rightUniq.add(getResultString(printer, str, ComparisonResult::rightDifference));
			} else {
				//add strings to dif
				StructurePrinter printer = comp.createPrinter();
				leftUniq.add(getResultString(printer, str, ComparisonResult::leftDifference));
				rightUniq.add(getResultString(printer, str, ComparisonResult::rightDifference));
			}
		}
		Collections.sort(intersection);
		Collections.sort(leftUniq);
		Collections.sort(rightUniq); //TODO: double check not dups here
		return createCompResult(leftClass, rightClass, intersection, leftUniq, rightUniq);
    }
	private void changeStringModifiers(ClassComparison<?,?> comp, Class<? extends S> leftClass, Class<? extends U> rightClass){
		if(ComparisonSettings.REPLACE_PARENT_CLASS_IN_CHILD){
			final String leftParentName = leftClass.getName()+"$";
			final String rightParentName = rightClass.getName()+"$";
			comp.setLeftStringModifier(e->e.replace(leftParentName, ""));
			comp.setRightStringModifier(e->e.replace(rightParentName, ""));
		}
	}
	private Class<?> getInnerClass(Class<?> parentClass, String childName){
		final String realChildName = childName.replaceAll("[^ ]* |^\\w+", ""); //remove extra data at the start
		return getFullTStream(parentClass)
				.filter(e->e.getSimpleName().equals(realChildName))
				.findFirst()
				.orElseThrow(()->new RuntimeException("Can't get nested class Class<?> variable for parent class "+parentClass.getName() + " and child class " + realChildName));
	}
	private String getResultString(StructurePrinter printer, String title, Function<ComparisonResult<?,?>,List<String>> listApplier){
		StringBuilder builder = new StringBuilder();
		printer.setSettings(PrinterSettings.DEFAULT.withAppender(builder).withHideEmptyComparers(true));
		printer.printSection("Expanded: "+title, 0,  listApplier);
		return builder.toString().stripTrailing()+"\n";
	}
	@Override
	protected boolean isSynthetic(Class<?> theClass){
		return theClass.isSynthetic();
	}
	@Override
	protected int getModifiers(Class<?> theClass){
		return theClass.getModifiers();
	}
	@Override
    protected Stream<Class<?>> getTStream(Class<?> theClass){
    	return Arrays.stream(theClass.getClasses());
    }
    @Override
    protected Stream<Class<?>> getDeclaredTStream(Class<?> theClass){
    	return Arrays.stream(theClass.getDeclaredClasses());
    }
    @Override
    protected String stringifyToCompare(Class<?> theClass){
		return theClass.getSimpleName();
    }
    @Override
    protected String getName(){
    	return "Nested Class Comparer";
    }
}