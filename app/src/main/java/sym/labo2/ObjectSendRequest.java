
/**
 * Created by lemdjo on 12.11.2017.
 */
package sym.labo2;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
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

public class ObjectSendRequest {

    private static final String TAG = "ObjectSendRequest";
    public static final String REQUEST_METHOD = "POST";
    public static final int READ_TIMEOUT = 5000;
    public static final int CONNECTION_TIMEOUT = 5000;

    private ArrayList<CommunicationEventListener> listeners = new ArrayList<>();

    private Context context;

    public ObjectSendRequest(Context context) {
        this.context = context;
    }

    public void sendRequest(String request, String url) throws Exception {

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

    private class ObjectSendRequestTask extends AsyncTask<String, Void, String> {

        protected String onPreExecute(String... params) {
            return null;
        }

        @Override
        protected String doInBackground(String... params) {
            String url_str = params[1];
            String result = "";

            try {
                URL url = new URL(url_str);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                //Set methods and timeouts
                conn.setRequestMethod(REQUEST_METHOD);
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setDoInput(true);
                conn.setDoOutput(true);

                //Connect to our url
                conn.connect();

                OutputStream os = conn.getOutputStream();

                //Create JSON Object
                JSONObject personObject = new JSONObject();
                personObject.put("firstName", "Marie");
                personObject.put("lastName", "Lemdjo");
                personObject.put("email", "a.b@c");

                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));


                writer.write(personObject.toString());
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
            } catch (JSONException e) {
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