package io.github.nickm980.smallville.events;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonSubTypes.Type;

public class EventBus {
    private List<SmallvilleListener> eventHandlers = new ArrayList<SmallvilleListener>();
    private static EventBus instance;

    public void registerListener(SmallvilleListener listener) {
	eventHandlers.add(listener);
    }

    public void postEvent(SmallvilleEvent e) {
	Method[] methods;

	for (Object o : eventHandlers) {
	    methods = o.getClass().getMethods();
	    for (Method m : methods) {
		if (m.getAnnotation(Listen.class) != null) {
		    try {
			if (m.getParameterTypes()[0].isAssignableFrom(e.getClass())) {
			    m.invoke(o, e);
			}
		    } catch (IllegalAccessException ex) {
		    } catch (IllegalArgumentException ex) {
		    } catch (InvocationTargetException ex) {
		    }
		}
	    }
	}
    }

    public static EventBus getEventBus() {
	if (instance == null) {
	    synchronized (EventBus.class) {
		if (instance == null) {
		    instance = new EventBus();
		}
	    }
	}
	return instance;
    }
}