package com.craftmend.simpleevents;

import com.craftmend.simpleevents.abstracts.AbstractEvent;
import com.craftmend.simpleevents.annotations.SimpleEvent;
import com.craftmend.simpleevents.enums.EventPriority;
import com.craftmend.simpleevents.executors.EventExecutor;
import com.craftmend.simpleevents.interfaces.Listener;

import lombok.NoArgsConstructor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * SimpleEvents by Mindgamesnl, version 1.0.
 *
 * Github: https://github.com/Mindgamesnl/SimpleEvents
 */

@NoArgsConstructor
public class SimpleEvents {

    private Map<String, List<EventExecutor>> executorMap = new HashMap<>();

    /**
     * Register an event to be executed, with all parameters
     * this returns the executor that would contain the method or caller
     *
     * @param event The Event class to be called
     * @param priority The event priority, for what it should be executed
     * @param ignoreCancelled if the event should be executed, even if it is cancelled, true = run
     * @param <E> the class type for the event executor
     * @return event executor that got registered, must be used to assign callback.
     */
    public <E extends AbstractEvent> EventExecutor<E> registerEvent(Class<E> event, EventPriority priority, boolean ignoreCancelled) {
        //check if it exists, if not then add it to chan
        if (!executorMap.containsKey(event.getName())) executorMap.put(event.getName(), new ArrayList<>());

        //setup executor
        EventExecutor<E> executor = new EventExecutor<>(priority, ignoreCancelled);

        //register and return executor
        executorMap.get(event.getName()).add(executor);
        return executor;
    }


    /**
     * Triggers all EventExecutors wit primitive type of Event
     * with the payload of the AbstractEvent
     *
     * @param event event type and content to be called
     */
    public boolean triggerEvent(AbstractEvent event) {
        //check if it exists, if not, cancel since there are no handlers, so why do anything
        if (!executorMap.containsKey(event.getClass().getName())) return false;

        //get all executors, and pass them the payload
        executorMap.get(event.getClass().getName()).stream()
                .sorted(Comparator.comparing(eventExecutor -> eventExecutor.getEventPriority().getLevel()))
                .collect(Collectors.toList())
                .forEach(eventExecutor -> {
                    if (event.isCancelled() && !eventExecutor.isIgnoresCancelled()) return;
                    eventExecutor.run(event);
                });

        return event.isCancelled();
    }


    /**
     * Register a Java Reflection Method as an event listener.
     * Used to register functions in the Listener interface
     *
     * @param method the reflection Method reference that should be called
     * @param listener the class in which the function originates
     * @param priority the event priority that is linked with the Java Annotation
     * @param ignoresCancelled decide if the function should still be called even if the event was cancelled
     */
    private void registerMethod(Method method, Listener listener, EventPriority priority, boolean ignoresCancelled) {
        //register it by class
        registerEvent((Class) method.getParameterTypes()[0],
                priority,
                ignoresCancelled)
                .onExecute(payload -> {
            try {
                //invoke the Method, with the payload class and the parent object
                method.invoke(listener, payload);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        });
    }


    /**
     * Search a Class implementing the Listener for Methods with the SimpleEvent Annotation and AbstractEvent parameter
     *
     * @param listener the Class to search
     */
    public void registerListener(Listener listener) {
        Method[] methods = listener.getClass().getMethods();

        //loop for all methods that have the annotation of @SimpleEvent
        Arrays.stream(methods)
                .filter(method -> method.isAnnotationPresent(SimpleEvent.class))
                .filter(method -> !method.getParameterTypes()[0].isAssignableFrom(AbstractEvent.class))
                .collect(Collectors.toList())
                .forEach(method -> registerMethod(method,
                        listener,
                        method.getAnnotation(SimpleEvent.class).priority(),
                        method.getAnnotation(SimpleEvent.class).ignoreCancelled()
                ));
    }

}
