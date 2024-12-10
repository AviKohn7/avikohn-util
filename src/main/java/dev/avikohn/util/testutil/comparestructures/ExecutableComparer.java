package dev.avikohn.util.testutil.comparestructures;

import java.lang.reflect.*;
import java.util.stream.Stream;
import java.util.stream.Collectors;
import java.util.Arrays;


public abstract class ExecutableComparer<T extends Executable,S,U> extends MemberComparer<T,S,U>{
	public ExecutableComparer(){
		super();
	}
	public ExecutableComparer(Flag accessFlag){
		super(accessFlag);
	}
    @Override
    protected String stringifyToCompare(T executable){
		return stringifyParamNames(executable);
    }
	protected Stream<Parameter> getParamStream(T executable){
		return Arrays.stream(executable.getParameters())
				.filter(param -> !param.isSynthetic() && !param.isImplicit());
	}
    protected String stringifyParamNames(T executable){
    	return "("+getParamStream(executable)
                    .map(param -> param.getType().getName())
                    .collect(Collectors.joining(", "))+")";
    }
}