package dev.avikohn.util.testutil.params;

import org.junit.jupiter.params.provider.ArgumentsSource;

import java.lang.annotation.*;

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@ArgumentsSource(XMLArgumentsProvider.class)
public @interface XMLSource{
    String value();
}