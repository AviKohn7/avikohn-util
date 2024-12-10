package dev.avikohn.util.testutil.comparestructures.runner;

import dev.avikohn.util.testutil.comparestructures.*;

public class CompareStructures{
	public static void main(String[] args) throws ClassNotFoundException{
		if(args.length != 2){
			System.out.println("Args must be 2");
			return;
		}
		Class<?> class1 = Class.forName(args[0]);
		Class<?> class2 = Class.forName(args[1]);
		// compareClasses(class1, class2);
		ClassComparison<?,?> comparison = new ClassComparison<>(class1, class2).setAccessFlag(Flag.ALL);
		comparison.compareAndPrint(PrinterSettings.DEFAULT);
		//compareFieldsAndPrint(class1, class2);		
	}
}