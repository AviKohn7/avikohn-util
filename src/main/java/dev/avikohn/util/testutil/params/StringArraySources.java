package dev.avikohn.util.testutil.params;

import org.apiguardian.api.API;
import org.junit.jupiter.params.provider.CsvSource;

import java.lang.annotation.*;

@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface StringArraySources {
    StringArraySource[] value();
}
