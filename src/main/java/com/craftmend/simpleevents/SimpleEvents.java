package com.craftmend.simpleevents;

import com.craftmend.simpleevents.abstracts.AbstractEvent;
import com.craftmend.simpleevents.executors.EventExecutor;
import lombok.NoArgsConstructor;

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

}
