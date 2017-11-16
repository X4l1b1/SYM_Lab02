package sym.labo2;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class ObjectSendRequest {

    private static final String TAG = "ObjectSendRequest";

    private ArrayList<CommunicationEventListener> listeners = new ArrayList<>();

    private Context context;
    private boolean isCompressed = false;

    public ObjectSendRequest(Context context) {
        this.context = context;
    }

    public void sendRequest(String request, String url, boolean compress) throws Exception {

        this.isCompressed = compress;

        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        if(activeNetwork != null && activeNetwork.isConnected()) {
            new ObjectSendRequest.ObjectSendRequestTask().execute(request, url);
        } else {
            Toast.makeText(context, "Can't reach server !", Toast.LENGTH_LONG).show();
            Log.i(TAG, "Can't reach server !");
        }

    }


    public void setCommunicationEventListener (CommunicationEventListener l) {
        this.listeners.add(l);
    }

    private byte[] compress(String json) {

        byte[] compressedJson = null;

        Log.i(TAG, "Before compression : " + json);

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Deflater def = new Deflater(Deflater.BEST_COMPRESSION, true);
            DeflaterOutputStream dos = new DeflaterOutputStream(baos, def);

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

        Log.i(TAG, "Before compression : " + bytes.toString());

        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            Inflater inf = new Inflater(true);
            InflaterInputStream iis = new InflaterInputStream(bis, inf);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            int bytesRead;
            while ((bytesRead = iis.read()) != -1) {
                baos.write(bytesRead);
            }

            baos.flush();
            baos.close();

            decompressedJson = baos.toString();

        } catch(Exception e) {
            e.printStackTrace();
        }

        Log.i(TAG, "After compression : " + decompressedJson);

        return decompressedJson;

    }

    private class ObjectSendRequestTask extends AsyncTask<String, Void, String> {

        OkHttpClient client = new OkHttpClient();

        @Override
        protected String doInBackground(String... params) {


            try {

                byte[] bodyContent;

                Request.Builder requestBuilder = new Request.Builder();

                if(isCompressed) {
                    bodyContent = compress(params[0]);
                    requestBuilder
                            .addHeader("X-Network", "CSD")
                            .addHeader("X-Content-Encoding", "deflate");
                } else {
                    bodyContent = params[0].getBytes();
                }

                RequestBody body =
                        RequestBody.create(MediaType.parse("application/json; charset=utf-8"),
                                bodyContent);


                Request request = requestBuilder
                        .url(params[1])
                        .post(body)
                        .build();

                Response response = client.newCall(request).execute();

                if(isCompressed)
                    return decompress(response.body().bytes());

                return response.body().string();


            }catch (Exception e){
                e.printStackTrace();
            }

            return null;

        }

        protected void onPostExecute(String res) {
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
