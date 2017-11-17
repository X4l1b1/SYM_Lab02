package sym.labo2;

import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by pierre-samuelrochat on 07.11.17.
 */

public class TextSendRequest {
    private static final String TAG = "TextSendRequest";

    private ArrayList<CommunicationEventListener> listeners = new ArrayList<>();
    private ArrayList<Pair<String, String>> delayedRequests = new ArrayList<>();

    ConnectivityManager cm;
    NetworkInfo activeNetworkInfo;

    private Context context;

    public TextSendRequest(Context context) {

        this.context = context;
        this.cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        context.registerReceiver(new MyReceiver(this),
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    public void sendRequest(String request, String url, boolean delay) throws Exception {

        activeNetworkInfo = cm.getActiveNetworkInfo();

        //Execute async request if connection is available, otherwise store it if delay is true
        if(activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            new DelayedSendRequestTask().execute(request, url);
        } else {
            if(delay) {
                Toast.makeText(context, "Request added to list", Toast.LENGTH_LONG).show();
                delayedRequests.add(new Pair(request, url));
            } else {
                Toast.makeText(context, "No connection", Toast.LENGTH_LONG).show();
            }
        }

    }

    public void sendDelayedRequests() {

        //Execute async request for every waiting request if connection is available
        for(Pair<String, String> p : delayedRequests) {

            activeNetworkInfo = cm.getActiveNetworkInfo();

            if(activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
                Log.i(TAG, "Sending request...");
                new DelayedSendRequestTask().execute(p.first, p.second);
            } else {
                Toast.makeText(context, "No Connection", Toast.LENGTH_LONG).show();
            }
        }
        delayedRequests.clear();
    }

    public void setCommunicationEventListener (CommunicationEventListener l) {
        //Add listener to the list of listeners
        this.listeners.add(l);
    }

    private class DelayedSendRequestTask extends AsyncTask<String, Void, String> {

        OkHttpClient client = new OkHttpClient();

        @Override
        protected String doInBackground(String... params) {

            try {

                RequestBody body =
                        RequestBody.create(MediaType.parse("application/text; charset=utf-8"),
                                params[0].getBytes());

                //Create OkHttp request
                Request request = new Request.Builder()
                        .url(params[1])
                        .post(body)
                        .build();


                Response response = client.newCall(request).execute();

                return response.body().string();

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;

        }

        protected void onPostExecute(String res) {

            //Notify all subscribed listeners
            for(CommunicationEventListener l : listeners) {
                if(res != null) {
                    l.handleServerResponse(res);
                    Log.i(TAG, "Notifying Listeners");
                } else {
                    Toast.makeText(context, "null response", Toast.LENGTH_LONG).show();
                    Log.i(TAG, "null response");
                }
            }
        }
    }

}
