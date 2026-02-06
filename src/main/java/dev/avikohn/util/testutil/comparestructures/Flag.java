package dev.avikohn.util.testutil.comparestructures;

public class Flag{
	public static final Flag EMPTY = new Flag(0);
	public static final Flag STATIC = new Flag(0b00000001);
	public static final Flag INSTANCE =  new Flag(0b00000001 << 1);
	public static final Flag STATIC_AND_INSTANCE =  joinFlags(INSTANCE, STATIC);
	public static final Flag DECLARED = new Flag(0b00000001 << 2);
	public static final Flag EXPOSED = new Flag(0b00000001 << 3);
	public static final Flag DECLARED_AND_EXPOSED = joinFlags(DECLARED, EXPOSED);
	public static final Flag ONLY_PUBLIC = new Flag(0b00000001 << 4);
	public static final Flag ONLY_PROTECTED = new Flag(0b00000001 << 5);
	public static final Flag ONLY_PACKAGE_PRIVATE = new Flag(0b00000001 << 6);
	public static final Flag ONLY_PRIVATE = new Flag(0b00000001 << 7);
	static final Flag ONLY_ALL = joinFlags(ONLY_PUBLIC, ONLY_PROTECTED, ONLY_PACKAGE_PRIVATE, ONLY_PRIVATE); //package-private
	public static final Flag ALL = joinFlags(STATIC_AND_INSTANCE, DECLARED_AND_EXPOSED, ONLY_ALL);

	public static Flag joinFlags(Flag... flags){
		if(flags == null || flags.length == 0) throw new IllegalArgumentException("There must be at least one flag in joinFlags");

		int flagValue = flags[0].getFlagValue();
		for(Flag f: flags){
			flagValue |= f.getFlagValue();
		}
		return new Flag(flagValue);
	}

	public static Flag mutualFlag(Flag... flags){
		if(flags == null || flags.length == 0) throw new IllegalArgumentException("There must be at least one flag in mutualFlag");

		int flagValue = flags[0].getFlagValue();
		for(Flag f: flags){
			flagValue &= f.getFlagValue();
		}
		return new Flag(flagValue);
	}

	static boolean matchesFlag(int theInt, Flag flag){
		return (theInt & flag.getFlagValue()) == flag.getFlagValue();
	}

	private final int flagValue;
	private Flag(int flagValue){
		this.flagValue = flagValue;
	} 
	public int getFlagValue(){
		return flagValue;
	}
	public boolean includesFlag(Flag flag){
		return (flagValue & flag.getFlagValue()) == flag.getFlagValue();
	}
	public boolean isDisjointWith(Flag flag){
		return (flag.flagValue & flagValue) == 0;
	}
	@Override
	public boolean equals(Object obj){
		return obj instanceof Flag && ((Flag)obj).getFlagValue() == flagValue;
	}
	@Override
	public int hashCode(){
		return flagValue;
	}
	@Override
	public String toString(){
		return String.format("%8s", Integer.toBinaryString(getFlagValue())).replace(' ', '0');
	}
}