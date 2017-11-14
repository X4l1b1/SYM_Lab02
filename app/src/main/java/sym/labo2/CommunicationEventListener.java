package sym.labo2;

import java.util.EventListener;

/**
 * Created by pierre-samuelrochat on 05.11.17.
 */

public interface CommunicationEventListener extends EventListener {
    boolean handleServerResponse(String response);
}