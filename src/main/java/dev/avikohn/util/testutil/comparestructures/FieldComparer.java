package dev.avikohn.util.testutil.comparestructures;

import java.lang.reflect.Modifier;
import java.lang.reflect.Field;
import java.util.stream.Stream;
import java.util.Arrays;


public class FieldComparer<S,U> extends MemberComparer<Field,S,U>{
	public FieldComparer(){
		super();
	}
	public FieldComparer(Flag accessFlag){
		super(accessFlag);
	}
	@Override
    protected Stream<Field> getTStream(Class<?> theClass){
    	return Arrays.stream(theClass.getFields());
    }
    @Override
    protected Stream<Field> getDeclaredTStream(Class<?> theClass){
    	return Arrays.stream(theClass.getDeclaredFields());
    }
    @Override
    protected String stringifyToCompare(Field field){
		String fieldName = field.getType().getName() + " " + field.getName();
		return fieldName;
    }
    @Override
    protected String getName(){
    	return "Field Comparer";
    }
}