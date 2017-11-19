package sym.labo2;

import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * CommunicationManager that creates an async request and notify subscribed listeners
 * when it receive a response.
 * If there is no connection available, the request can be stored until connection is retrieved.
 */
public class CommunicationManager {

    private static final String TAG = CommunicationManager.class.getSimpleName();

    //Media types
    private final String JSON_MEDIA_TYPE = "application/json; charset=utf-8";
    private final String XML_MEDIA_TYPE  = "application/xml; charset=utf-8";
    private final String TEXT_MEDIA_TYPE = "application/text; charset=utf-8";

    //List of subscribed listener
    private ArrayList<CommunicationEventListener> listeners = new ArrayList<>();
    private ArrayList<RequestManager> delayedRequests = new ArrayList<>();

    //Actrivity context
    private Context ctx;

    //Network informations
    private ConnectivityManager connectivityManager;
    private NetworkInfo activeNetworkInfo;

    public CommunicationManager(Context context) {

        //Context of calling activity
        this.ctx = context;
        this.connectivityManager =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        //Register the context of the activity to a change of connectivity event
        //When a change occur, method onReceive() of CustomReceiver class is executed
        context.registerReceiver(new CustomReceiver(this),
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    public void sendRequest(RequestManager data, boolean delay) throws Exception {

        activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        //Execute async request if connection is available
        if(activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            new SendRequestTask().execute(data);
        } else {
            if(delay) {
                Toast.makeText(ctx, "Request Added to List", Toast.LENGTH_LONG).show();
                delayedRequests.add(data);
            } else {
                Toast.makeText(ctx, "No connection", Toast.LENGTH_LONG).show();
            }
        }

    }

    public void sendDelayedRequests() {

        //Execute async request for every waiting request if connection is available
        for(RequestManager d : delayedRequests) {

            activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

            if(activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
                new SendRequestTask().execute(d);
                Toast.makeText(ctx, "Sending Waiting Requests", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(ctx, "No Connection", Toast.LENGTH_LONG).show();
            }
        }
        delayedRequests.clear();
    }


    public void setCommunicationEventListener (CommunicationEventListener l) {
        //Add listener to the list of listeners
        this.listeners.add(l);
    }

    private class SendRequestTask extends AsyncTask<RequestManager, Void, String> {

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        @Override
        protected String doInBackground(RequestManager... requestManagers) {

            //OkHttp request builder
            Request.Builder requestBuilder = new Request.Builder();

            byte[] bodyContent = requestManagers[0].constructRequestBody();

            //Compress the object and add the corresponding headers to request
            if(requestManagers[0].isCompressed())
                requestBuilder
                        .addHeader("X-Network", "CSD")
                        .addHeader("X-Content-Encoding", "deflate");


            Log.i(TAG, "Sending...");

            String mediaType = "";

            //Sets media type
            switch(requestManagers[0].getDataType()) {
                case JSON :
                    mediaType = JSON_MEDIA_TYPE;
                    break;
                case XML  :
                    mediaType = XML_MEDIA_TYPE;
                    break;
                case TXT :
                    mediaType = TEXT_MEDIA_TYPE;
                    break;
            }

            //Construct body
            RequestBody body = RequestBody.create(MediaType.parse(mediaType), bodyContent);

            //Create OkHttp request
            Request request = requestBuilder
                    .url(requestManagers[0].getUrl())
                    .post(body)
                    .build();

            //Execute request
            Response response;

            try {

                response = client.newCall(request).execute();
                return requestManagers[0].handleResponse(response.body().bytes());

            }catch (Exception e){
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
                    Toast.makeText(ctx, "NULL Response", Toast.LENGTH_LONG).show();
                    Log.i(TAG, "NULL Response");
                }
            }
        }
    }
}
