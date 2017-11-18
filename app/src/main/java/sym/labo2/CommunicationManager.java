package sym.labo2;

import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CommunicationManager {

    private static final String TAG = "ObjectSendRequest";

    //Media types
    private final String JSON_MEDIA_TYPE = "application/json; charset=utf-8";
    private final String XML_MEDIA_TYPE  = "application/xml; charset=utf-8";
    private final String TEXT_MEDIA_TYPE = "application/text; charset=utf-8";

    //List of subscribed listener
    private ArrayList<CommunicationEventListener> listeners = new ArrayList<>();
    private ArrayList<Pair<String, String>> delayedRequests = new ArrayList<>();

    //Actrivity context
    private Context ctx;

    //Options
    private ContentType contentType;
    private boolean isCompressed = false;

    //Network informations
    private ConnectivityManager connectivityManager;
    private NetworkInfo activeNetworkInfo;

    public CommunicationManager(Context context) {

        this.ctx = context;

        this.connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        context.registerReceiver(new MyReceiver(this),
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    public void sendRequest(String request, String url, ContentType type, boolean compress,
                            boolean delay) throws Exception {

        this.contentType  = type;
        this.isCompressed = compress;

        activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        //Execute async request if connection is available
        if(activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            new SendRequestTask().execute(request, url);
        } else {
            if(delay) {
                Toast.makeText(ctx, "Request added to list", Toast.LENGTH_LONG).show();
                delayedRequests.add(new Pair(request, url));
            } else {
                Toast.makeText(ctx, "No connection", Toast.LENGTH_LONG).show();
            }
        }

    }

    public void sendDelayedRequests() {

        //Execute async request for every waiting request if connection is available
        for(Pair<String, String> p : delayedRequests) {

            activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

            if(activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
                Log.i(TAG, "Sending request...");
                new SendRequestTask().execute(p.first, p.second);
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

    private byte[] compress(String json) {

        byte[] compressedJson = null;

        Log.i(TAG, "Before compression : " + json);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Deflater def = new Deflater(Deflater.BEST_COMPRESSION, true);
        DeflaterOutputStream dos = new DeflaterOutputStream(baos, def);

        try {

            dos.write(json.getBytes());
            dos.flush();
            dos.close();
            baos.flush();
            baos.close();

            compressedJson = baos.toByteArray();

        } catch(Exception e) {
            e.printStackTrace();
        }

        Log.i(TAG, "After compression : " + compressedJson.toString());

        return compressedJson;
    }

    private String decompress(byte[] bytes) {

        String decompressedJson = null;

        Log.i(TAG, "Before decompression : " + bytes.toString());


        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        Inflater inf = new Inflater(true);
        InflaterInputStream iis = new InflaterInputStream(bis, inf);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        int bytesRead;

        try{

            while ((bytesRead = iis.read()) != -1) {
                baos.write(bytesRead);
            }

            baos.flush();
            baos.close();

            decompressedJson = baos.toString();

        } catch(Exception e) {
            e.printStackTrace();
        }

        Log.i(TAG, "After decompression : " + decompressedJson);

        return decompressedJson;

    }

    private class SendRequestTask extends AsyncTask<String, Void, String> {

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        @Override
        protected String doInBackground(String... params) {

            byte[] bodyContent;

            //OkHttp request builder
            Request.Builder requestBuilder = new Request.Builder();

            //Compress the object and add the corresponding headers to request
            if(isCompressed) {
                bodyContent = compress(params[0]);
                requestBuilder
                        .addHeader("X-Network", "CSD")
                        .addHeader("X-Content-Encoding", "deflate");
            } else {
                bodyContent = params[0].getBytes();
            }

            String mediaType = "";

            //Sets media type
            switch(contentType) {
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
                    .url(params[1])
                    .post(body)
                    .build();

            //Execute request
            Response response;

            try {

                response = client.newCall(request).execute();

                if(isCompressed) {
                    return decompress(response.body().bytes());
                }

                return response.body().string();

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
