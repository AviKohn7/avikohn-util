package dev.avikohn.util.testutil.comparestructures;

import java.lang.reflect.Member;

public abstract class MemberComparer<T extends Member, S, U> extends ContentComparer<T, S, U>{
    public MemberComparer(){
        super();
    }
    public MemberComparer(Flag accessFlag){
        super(accessFlag);
    }
    @Override
    protected int getModifiers(T val){
        return val.getModifiers();
    }
    @Override
    protected boolean isSynthetic(T val){
        return val.isSynthetic();
    }
}
