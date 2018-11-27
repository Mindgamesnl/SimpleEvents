package com.craftmend.simpleevents.executors;

import java.util.function.Consumer;

public class EventExecutor<T> {

    private Consumer<T> executable;

    public void run(T payload) {
        executable.accept(payload);
    }

    public void onExecute(Consumer<T> executable) {
        this.executable = executable;
    }

}
