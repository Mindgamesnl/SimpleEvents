package com.craftmend.simpleevents.executors;

import com.craftmend.simpleevents.enums.EventPriority;
import lombok.Getter;

import java.util.function.Consumer;

public class EventExecutor<T> {

    @Getter private EventPriority eventPriority;
    @Getter private boolean ignoresCancelled;

    private Consumer<T> executable;

    public EventExecutor(EventPriority priority, boolean ignoresCancelled) {
        this.eventPriority = priority;
        this.ignoresCancelled = ignoresCancelled;
    }

    public void run(T payload) {
        executable.accept(payload);
    }

    public void onExecute(Consumer<T> executable) {
        this.executable = executable;
    }

}
