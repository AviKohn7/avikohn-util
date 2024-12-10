package dev.avikohn.util.testutil.params;

import org.junit.jupiter.params.provider.ArgumentsSource;

import java.lang.annotation.*;

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(StringArraySources.class)
@ArgumentsSource(StringArrayArgumentsProvider.class)
public @interface StringArraySource{
    String[] value() default {};
    String delimiter() default ",";
    boolean ignoreLeadingAndTrailingWhitespace() default true;
    String textBlock() default "";
}
