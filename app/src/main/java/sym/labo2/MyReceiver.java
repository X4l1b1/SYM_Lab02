package sym.labo2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by pierre-samuelrochat on 07.11.17.
 */

public class MyReceiver extends BroadcastReceiver {

    private static final String TAG = "MyReceiver";

    private TextSendRequest tsr = null;

    public MyReceiver(TextSendRequest delayedSendRequest) {
        this.tsr = delayedSendRequest;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.i(TAG, "A change occured in connection state");

        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        if(activeNetwork != null && activeNetwork.isConnected()) {
            Log.i(TAG, "Begin to send waiting requests");
            tsr.sendDelayedRequests();
        }
    }
}
