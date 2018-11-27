# SimpleEvents
Simple events is a tiny java library to add a simple event system to your projects. Nothing big, nothing special. A small module for big projects.

## Example code
Step 1, create your event.
```java
public class MyCoolEvent extends AbstractEvent {

    private String content;

    private MyCoolEvent(String content) {
        this.content = content;
    }

    public String getContent() {
        return this.content;
    }

}
```
step 2, register your handler
```java
SimpleEvents simpleEvents = new SimpleEvents();
```

And that's it.
you can register an event listener with
```java
simpleEvents.registerEvent(MyCoolEvent.class).onExecute(event -> {
    System.out.println("The MyCoolEvent content is " + event.getContent());
});
```

and trigger it later with
```java
simpleEvents.triggerEvent(new MyCoolEvent("i am an event hi there!"));
```

That's it, as easy as 123.