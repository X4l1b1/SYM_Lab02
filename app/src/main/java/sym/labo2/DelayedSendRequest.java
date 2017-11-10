package sym.labo2;

import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by pierre-samuelrochat on 07.11.17.
 */

public class DelayedSendRequest {
    private static final String TAG = "DelayedSendRequest";

    public static final String REQUEST_METHOD = "POST";
    public static final int READ_TIMEOUT =  5000;
    public static final int CONNECTION_TIMEOUT = 5000;

    private ArrayList<CommunicationEventListener> listeners = new ArrayList<>();
    private ArrayList<Pair<String, String>> delayedRequests = new ArrayList<>();

    ConnectivityManager cm;
    NetworkInfo activeNetworkInfo;

    private Context context;

    public DelayedSendRequest(Context context) {

        this.context = context;
        this.cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        context.registerReceiver(new MyReceiver(this),
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    public void sendRequest(String request, String url) throws Exception {

        activeNetworkInfo = cm.getActiveNetworkInfo();

        if(activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            new DelayedSendRequestTask().execute(request, url);
        } else {
            Toast.makeText(context, "Request added to list", Toast.LENGTH_LONG).show();
            delayedRequests.add(new Pair(request, url));
        }

    }

    public void sendDelayedRequests() {

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
        this.listeners.add(l);
    }

    private class DelayedSendRequestTask extends AsyncTask<String, Void, String> {

        protected String onPreExecute(String... params) {
            return null;
        }

        @Override
        protected String doInBackground(String... params) {
            String url_str = params[1];
            String result = "";

            try {
                URL url = new URL(url_str);

                HttpURLConnection conn = (HttpURLConnection)url.openConnection();

                //Set methods and timeouts
                conn.setRequestMethod(REQUEST_METHOD);
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setDoInput(true);
                conn.setDoOutput(true);

                //Connect to our url
                conn.connect();

                OutputStream os = conn.getOutputStream();

                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

                writer.write(params[0]);
                writer.flush();
                writer.close();
                os.close();

                int responseCode = conn.getResponseCode();

                if (responseCode == HttpsURLConnection.HTTP_OK) {

                    InputStreamReader streamReader = new InputStreamReader(conn.getInputStream());
                    BufferedReader reader = new BufferedReader(streamReader);

                    Log.i(TAG, "HTTP OK");

                    //Create a new buffered reader and String Builder
                    StringBuilder stringBuilder = new StringBuilder();
                    String inputLine;

                    //Check if the line we are reading is not null
                    while ((inputLine = reader.readLine()) != null) {
                        Log.i(TAG, "Getting lines");
                        stringBuilder.append(inputLine);
                    }

                    result = stringBuilder.toString();

                } else {
                    return "Failed !";
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;

        }

        protected void onProgressUpdate(Integer... values) {}

        protected void onPostExecute(String res) {
            for(CommunicationEventListener l : listeners) {
                Log.i(TAG, "Notifying Listeners");
                l.handleServerResponse(res);
            }
        }
    }

}
