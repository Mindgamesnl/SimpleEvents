package com.craftmend.simpleevents.annotations;

import com.craftmend.simpleevents.enums.EventPriority;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface SimpleEvent {

    EventPriority priority() default EventPriority.NORMAL;

    boolean ignoreCancelled() default false;

}
