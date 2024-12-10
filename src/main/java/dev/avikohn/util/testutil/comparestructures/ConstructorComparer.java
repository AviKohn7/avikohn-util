package dev.avikohn.util.testutil.comparestructures;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Stream;
import java.util.function.Function;

public class ConstructorComparer<S,U> extends ExecutableComparer<Constructor<?>,S,U>{
	public static boolean GUESS_EXTRA_PARAMETER = false;
	static Set<String> methodParameterslessClasses = new HashSet<String>();
	public ConstructorComparer(){
		super();
	}
	public ConstructorComparer(Flag accessFlag){
		super(accessFlag);
	}
	@Override
	protected Stream<Constructor<?>> getFullTStream(Class<?> theClass){
		Stream<Constructor<?>> toTest = super.getFullTStream(theClass);
		checkIfMethodParametersAdded(theClass, toTest);
		return super.getFullTStream(theClass); //have to run again bc Streams get used up
	}
	@Override
    protected Stream<Constructor<?>> getTStream(Class<?> theClass){
    	return Arrays.stream(theClass.getConstructors());
    }
    @Override
    protected Stream<Constructor<?>> getDeclaredTStream(Class<?> theClass){
    	return Arrays.stream(theClass.getDeclaredConstructors());
    }
    private void checkIfMethodParametersAdded(Class<?> declaringClass, Stream<Constructor<?>> constructorStream){
		Optional<Constructor<?>> optionalC =  constructorStream.findFirst();
    	if(optionalC.isPresent() && needsAndLackingMethodParameters(declaringClass, optionalC.get())){
    		methodParameterslessClasses.add(declaringClass.getName());
    	}
    }
	private boolean needsAndLackingMethodParameters(Class<?> declaringClass, Constructor<?> constructor){
		if(declaringClass.isMemberClass() || declaringClass.isLocalClass() ||
				(declaringClass.isAnonymousClass() && declaringClass.getSuperclass() != null &&
						(declaringClass.getSuperclass().isMemberClass() || declaringClass.getSuperclass().isLocalClass()))){
			if(constructor.getParameters().length == 0) return false;
			return !constructor.getParameters()[0].isNamePresent();
		} else {
			return false;
		}
	}
	private boolean hasMethodParameters(Constructor<?> constructor){
		return constructor.getParameters().length == 0 || constructor.getParameters()[0].isNamePresent();
	}
	@Override
	protected Stream<Parameter> getParamStream(Constructor<?> constructor){
		return super.getParamStream(constructor).skip(tryGuessExtraParameters(constructor));
	}
	private int tryGuessExtraParameters(Constructor<?> constructor){
		if(!GUESS_EXTRA_PARAMETER || hasMethodParameters(constructor)){
			return 0;
		}
		return guessExtraParameters(constructor);
	}
	private int guessExtraParameters(Constructor<?> constructor){
		Class<?> containingClass = constructor.getDeclaringClass();
		if(containingClass.isEnum()){
			return 2; //synthetic, check non-member
		} if(containingClass.isRecord() || Modifier.isStatic(containingClass.getModifiers())){
			return 0;
		} else if(containingClass.isAnonymousClass()){
			Class<?> parentClass = containingClass.getSuperclass();
			if(parentClass == null) return 0; //idk, shouldn't run
			if(Modifier.isStatic(parentClass.getModifiers())){
				return 0;
			} else if(parentClass.isLocalClass() || parentClass.isMemberClass()){
				return 1; //implicit
			} else if(parentClass.isEnum()){
				return 2; //unverified. todo: verify
			} else {
				return 0;
			}
		} else if(containingClass.isLocalClass() || containingClass.isMemberClass()){
			return 1; //synthetic, check non-member
		} else {
			return 0;
		}
	}

    @Override
    protected String getName(){
    	return "Constructor Comparer";
    }

	@Override
	protected String stringifyToCompare(Constructor<?> constructor){
		return "[constructor]"+super.stringifyToCompare(constructor); //for better printing
	}

    public static Set<String> getMethodParameterslessClasses(){
    	return methodParameterslessClasses;
    }
}