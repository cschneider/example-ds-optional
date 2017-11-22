package net.lr.ds.optional;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;

/**
 * This component implements a gogo command to display a message and optionally
 * send it to eventadmin.
 * 
 * As the org.osgi.service.event package is an optional import we can not
 * simnply use an optional reference to EventAdmin. Instead we use an internal
 * interface to decouple from the optional package.
 */
@Component(service = MessageCommand.class, property = { "osgi.command.scope=dsopt", "osgi.command.function=message" })
public class MessageCommand {

    @Reference(cardinality = ReferenceCardinality.OPTIONAL)
    EventSender sender;

    public void message(String message) {
        System.out.println(message);
        if (sender != null) {
            sender.send(message);
        }
    }
}
