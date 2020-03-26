import java.lang.annotation.*;

@Target(value=ElementType.METHOD)
@Retention(value= RetentionPolicy.RUNTIME)

public @interface HttpRequestHandler{
    String value() default "";
}