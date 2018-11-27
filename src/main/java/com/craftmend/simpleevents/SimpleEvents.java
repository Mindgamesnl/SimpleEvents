package com.craftmend.simpleevents;

import com.craftmend.simpleevents.abstracts.AbstractEvent;
import com.craftmend.simpleevents.annotations.SimpleEvent;
import com.craftmend.simpleevents.executors.EventExecutor;
import com.craftmend.simpleevents.interfaces.Listener;

import lombok.NoArgsConstructor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
public class SimpleEvents {

    private Map<String, List<EventExecutor>> executorMap = new HashMap<>();

    /**
     * Generate EventExecutor with AbstractEvent primitive Type
     * And register it
     *
     * @param event
     * @param <E>
     * @return
     */
    public <E extends AbstractEvent> EventExecutor<E> registerEvent(Class<E> event) {
        //check if it exists, if not then add it to chan
        if (!executorMap.containsKey(event.getName())) executorMap.put(event.getName(), new ArrayList<>());

        //setup executor
        EventExecutor<E> executor = new EventExecutor<>();

        //register and return executor
        executorMap.get(event.getName()).add(executor);
        return executor;
    }


    /**
     * Triggers all EventExecutors wit primitive type of Event
     *
     * @param event
     */
    public void triggerEvent(AbstractEvent event) {
        //check if it exists, if not, cancel since there are no handlers, so why do anything
        if (!executorMap.containsKey(event.getClass().getName())) return;

        //get all executors, and pass them the payload
        executorMap.get(event.getClass().getName()).forEach(eventExecutor -> eventExecutor.run(event));
    }


    /**
     * Register a method from a class as a listener
     *
     * @param method
     */
    private void registerMethod(Method method, Listener listener) {
        //the argument is not an instance of AbstractEvent so we should not add it, we should throw an exception
        if (method.getParameterTypes()[0].isAssignableFrom(AbstractEvent.class)) throw new IllegalArgumentException("Argument with EventHandler interface must be instance of AbstractEvent");

        //register it by class
        registerEvent((Class) method.getParameterTypes()[0]).onExecute(payload -> {
            try {
                //invoke the Method, with the payload class and the parent object
                method.invoke(listener, payload);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        });
    }


    /**
     * Register all events in a listener
     *
     * @param listener
     */
    public void registerListener(Listener listener) {
        Method[] methods = listener.getClass().getMethods();

        //loop for all methods that have the annotation of @SimpleEvent
        for (Method method : methods)
            if (method.isAnnotationPresent(SimpleEvent.class)) registerMethod(method, listener);
    }

}
