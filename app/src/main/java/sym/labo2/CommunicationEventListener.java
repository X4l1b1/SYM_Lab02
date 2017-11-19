package sym.labo2;

import java.util.EventListener;


/**
 * Event listener interface.
 */
public interface CommunicationEventListener extends EventListener {
    boolean handleServerResponse(String response);
}