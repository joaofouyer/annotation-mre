package me.fouyer;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.SOURCE)
public @interface Positional {
    int position();
    int length();
    // String will be stripped only if set true. Example: str.substring(0, 10).strip()
    boolean strip() default false;
}
