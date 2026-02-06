package dev.avikohn.util.testutil.comparestructures;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

//todo: support for private and protected comparisons
public abstract class ContentComparer<T,S,U>{
    public static final Flag DEFAULT_ACCESS_FLAG = Flag.joinFlags(Flag.STATIC_AND_INSTANCE, Flag.EXPOSED);
    private ComparisonResult<S,U> previousResult;
	protected Flag accessFlag = DEFAULT_ACCESS_FLAG;
    private UnaryOperator<String> leftStringTransformFunc = e->e;
    private UnaryOperator<String> rightStringTransformFunc = e->e;
	public ContentComparer(){
		this(DEFAULT_ACCESS_FLAG);
	}
	public ContentComparer(Flag accessFlag){
		setAccessFlag(accessFlag);
	}
	public ComparisonResult<S,U> compare(Class<? extends S> leftClass, Class<? extends U> rightClass){
        List<List<String>> result = findComparison(collectAndSort(false, leftClass), collectAndSort(true, rightClass));
        this.previousResult = createCompResult(leftClass, rightClass, result.get(0), result.get(1),  result.get(2));
        return this.previousResult;
    }
    /**
     * @param theClass the class to get the stream from
     * @return a stream containing both declared and regular Ts, with possible duplicates
     */
    protected Stream<T> getFullTStream(Class<?> theClass){
        Stream<T> maybePublicStream = accessFlag.includesFlag(Flag.EXPOSED) ? getTStream(theClass) : Stream.empty();
        Stream<T> maybeDeclaredStream = accessFlag.includesFlag(Flag.DECLARED) ? getDeclaredTStream(theClass) : Stream.empty();
        return Stream.concat(maybePublicStream, maybeDeclaredStream);
    }
    protected ComparisonResult<S,U> createCompResult(Class<? extends S> leftClass, Class<? extends U> rightClass, List<String> intersection, List<String> leftUniqueContent, List<String> rightUniqueContent){
        return new ComparisonResult<S,U>(accessFlag, getName(), leftClass, rightClass, Collections.unmodifiableList(intersection), Collections.unmodifiableList(leftUniqueContent),  Collections.unmodifiableList(rightUniqueContent));
    }
    private List<List<String>> findComparison(TreeSet<String> leftList, TreeSet<String> rightList){
		ArrayList<String> intersection = new ArrayList<>(); //arraylists are thrown out after the method
        ArrayList<String> leftUniqueContent = new ArrayList<>();
        ArrayList<String> rightUniqueContent;
        for(String val: leftList){ //better algs exist but whatever
        	if(rightList.contains(val)){
        		intersection.add(val);
        		rightList.remove(val);
        	} else {
        		leftUniqueContent.add(val);
        	}
        }
        rightUniqueContent=new ArrayList<String>(rightList);
        return Arrays.asList(Collections.unmodifiableList(intersection), Collections.unmodifiableList(leftUniqueContent), Collections.unmodifiableList(rightUniqueContent));
    }
    private TreeSet<String> collectAndSort(boolean isRight, Class<?> theClass){
    	return collectAndSort(isRight, theClass, this::filterFunc);
	}
    private TreeSet<String> collectAndSort(boolean isRight, Class<?> theClass, Predicate<T> filterFunc){
        Stream<T> values = getFullTStream(theClass);
    	return values.filter(filterFunc).map(e->stringifyWithModifiers(e,theClass)).map(isRight ? rightStringTransformFunc : leftStringTransformFunc).sorted().collect(Collectors.toCollection(TreeSet<String>::new));
    }
    protected boolean filterFunc(T value){
        return matchesAccessFlag(value) && !isSynthetic(value);
    }
    private boolean matchesAccessFlag(T val){
    	boolean isStatic = isStatic(val);
        boolean matches = isStatic ? accessFlag.includesFlag(Flag.STATIC) : accessFlag.includesFlag(Flag.INSTANCE);
        if(!Flag.ONLY_ALL.isDisjointWith(accessFlag)){
            matches = matches && !accessFlag.isDisjointWith(getAccessModifierFlag(val));
        }
    	return matches;
    }

    private String stringifyWithModifiers(T val, Class<?> theClass){
    	String staticInfo = isStatic(val) && accessFlag.includesFlag(Flag.STATIC_AND_INSTANCE) ? "static " : ""; //only add if both static and instance are enabled
        String accessModifier = getAccessModifierString(val);
    	return replaceStringified(staticInfo+accessModifier+stringifyToCompare(val), theClass);
    }
    private String replaceStringified(String s, Class<?> theClass){
        s = s.replaceAll(theClass.getName().replaceAll("\\$", "\\$"), "[comparedClass]"); //todo: make better
        if(ComparisonSettings.FILTER_PACKAGES){
            s = s.replaceAll("([a-zA-Z_0-9]+[.])+", "");
        }
        return s;
    }
    private boolean isStatic(T val){
        return Modifier.isStatic(getModifiers(val));
    }
    private Flag getAccessModifierFlag(T val){
        int modifiers = getModifiers(val);
        if(Modifier.isPublic(modifiers)){
            return Flag.ONLY_PUBLIC;
        } else if(Modifier.isProtected(modifiers)){
            return Flag.ONLY_PROTECTED;
        } else if(Modifier.isPrivate(modifiers)){
            return Flag.ONLY_PRIVATE;
        } else { //is package-private
            return Flag.ONLY_PACKAGE_PRIVATE; //maybe just return empty string, but I think this is more clear
        }
    }
    private String getAccessModifierString(T val){
        int modifiers = getModifiers(val);
        if(Modifier.isPublic(modifiers)){
            return "public ";
        } else if(Modifier.isProtected(modifiers)){
            return "protected ";
        } else if(Modifier.isPrivate(modifiers)){
            return "private ";
        } else { //is package-private
            return "package-private "; //maybe just return empty string, but I think this is more clear
        }
    }
    public ComparisonResult<S,U> forceGetPreviousResult(Class<? extends S> leftClass, Class<? extends U> rightClass){
    	if(previousResult==null) compare(leftClass, rightClass);
    	return previousResult;
    }
    public Flag getAccessFlag(){
    	return accessFlag;
    }
    private void setAccessFlag(Flag flag){
        if(Flag.DECLARED_AND_EXPOSED.isDisjointWith(flag)){
            throw new IllegalArgumentException("AccessFlag must contain the DECLARED and/or the PUBLIC flag. The current flag is " + flag);
        }
        if(Flag.STATIC_AND_INSTANCE.isDisjointWith(flag)){
            throw new IllegalArgumentException("AccessFlag must contain the STATIC and/or the INSTANCE flag" + flag);
        }
        this.accessFlag = flag;
    }
    public ComparisonResult<S,U> getPreviousResult(){
    	return previousResult;
    }
    public void setModifyStringFunc(UnaryOperator<String> stringTransformFunc){
        this.leftStringTransformFunc = stringTransformFunc;
        this.rightStringTransformFunc = stringTransformFunc;
    }
    public void setLeftModifyStringFunc(UnaryOperator<String> stringTransformFunc){
        this.leftStringTransformFunc = stringTransformFunc;
    }
    public void setRightModifyStringFunc(UnaryOperator<String> stringTransformFunc){
        this.rightStringTransformFunc = stringTransformFunc;
    }
    protected boolean isSynthetic(T val){
        return false;
    }
    protected abstract int getModifiers(T val);
    protected abstract Stream<T> getTStream(Class<?> theClass);
    protected abstract Stream<T> getDeclaredTStream(Class<?> theClass);
    protected abstract String stringifyToCompare(T val);
    protected abstract String getName();
}