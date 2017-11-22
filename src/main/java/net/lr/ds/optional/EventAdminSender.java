package net.lr.ds.optional;
import java.util.HashMap;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;

/**
 * This component has mandatory dependency on EventAdmin. As this is an optional import
 * it can only be instantiated if the org.osgi.service.event package is present.
 * Still we do not have to check for class loading exceptions.
 */
@Component
public class EventAdminSender implements EventSender {
    @Reference
    EventAdmin eventAdmin;

    @Override
    public void send(String msg) {
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("message", msg);
        eventAdmin.postEvent(new Event("mytopic", properties));
        System.out.println("Sent message to EventAdmin");
    }

}
