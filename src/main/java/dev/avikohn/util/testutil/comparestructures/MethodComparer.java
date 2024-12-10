package dev.avikohn.util.testutil.comparestructures;

import java.lang.reflect.Modifier;
import java.lang.reflect.Method;
import java.util.stream.Stream;
import java.util.stream.Collectors;
import java.util.Arrays;


public class MethodComparer<S,U> extends ExecutableComparer<Method,S,U>{
	public MethodComparer(){
		super();
	}
	public MethodComparer(Flag accessFlag){
		super(accessFlag);
	}
	@Override
    protected String stringifyToCompare(Method method){
		String returnType = method.getReturnType().getName();
		return returnType+" "+method.getName()+super.stringifyToCompare(method);
    }
	@Override
    protected Stream<Method> getTStream(Class<?> theClass){
    	return Arrays.stream(theClass.getMethods());
    }
    @Override
    protected Stream<Method> getDeclaredTStream(Class<?> theClass){
    	return Arrays.stream(theClass.getDeclaredMethods());
    }
    @Override
    protected String getName(){
    	return "Method Comparer";
    }
}