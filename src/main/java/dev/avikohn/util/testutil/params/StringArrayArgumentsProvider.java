package dev.avikohn.util.testutil.params;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.*;
import org.junit.platform.commons.util.Preconditions;

import java.util.Arrays;
import java.util.stream.Stream;

class StringArrayArgumentsProvider extends AnnotationBasedArgumentsProvider<StringArraySource>{
    private static final String LINE_SEPARATOR = "\n";

    @Override
    protected Stream<? extends Arguments> provideArguments(ExtensionContext context, StringArraySource stringArrSource){
        boolean textBlockDeclared = !stringArrSource.textBlock().isEmpty();
        Preconditions.condition(stringArrSource.value().length > 0 ^ textBlockDeclared, () -> "@StringArraySource must be declared with either `value` or `textBlock` but not both");
        return textBlockDeclared ? this.parseTextBlock(stringArrSource) : this.parseValueArray(stringArrSource);

    }
    private Stream<? extends Arguments> parseValueArray(StringArraySource stringArrSource){
        String[] values = stringArrSource.value();
        return parseArray(values, stringArrSource);
    }
    private Stream<? extends Arguments> parseTextBlock(StringArraySource stringArrSource){
        String[] portions = stringArrSource.textBlock().split(LINE_SEPARATOR);
        return parseArray(portions, stringArrSource);
    }
    private Stream<? extends Arguments> parseArray(String[] array, StringArraySource stringArrSource){
        return Arrays.stream(array)
                .map(s->splitString(s, stringArrSource))
                .map(arr->Arguments.of((Object)arr));
    }
    private String[] splitString(String s, StringArraySource stringArrSource){
        String[] strs = s.split(stringArrSource.delimiter(), -1);
        if(stringArrSource.ignoreLeadingAndTrailingWhitespace()){
            for(int i = 0; i < strs.length; i++){
                strs[i] = strs[i].trim();
            }
        }
        return strs;
    }
}
