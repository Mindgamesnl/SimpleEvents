package com.craftmend.simpleevents.abstracts;

import lombok.Data;

@Data
public abstract class AbstractEvent {

    private boolean cancelled = false;

}
