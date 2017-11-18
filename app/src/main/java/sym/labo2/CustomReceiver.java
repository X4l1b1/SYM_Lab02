package sym.labo2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

/**
 * Used to test and execute waiting request when connection is retrieved
 */
public class CustomReceiver extends BroadcastReceiver {

    private static final String TAG = "CustomReceiver";

    private CommunicationManager communicationManager;

    public CustomReceiver(CommunicationManager cm) {
        this.communicationManager = cm;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.i(TAG, "A change occurred in the connection state");

        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        //Check connectivity, send waiting requests if the connection is ON
        if(activeNetwork != null && activeNetwork.isConnected()) {
            Log.i(TAG, "Begin to send waiting requests");
            communicationManager.sendDelayedRequests();
        }
    }
}
