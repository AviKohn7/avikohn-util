package dev.avikohn.util.testutil.comparestructures;

import java.util.List;
public record ComparisonResult<T,R>(
	Flag isStaticFlag,
	String name,
	Class<? extends T> leftClass,
	Class<? extends R> rightClass,
	List<String> intersection,
	List<String> leftDifference,
	List<String> rightDifference
){
	public boolean isIdentical(){
		return leftDifference.isEmpty() && rightDifference.isEmpty();
	}
}