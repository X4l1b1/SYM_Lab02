package sym.labo2;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonWriter;
import android.util.Log;

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

import javax.net.ssl.HttpsURLConnection;

public class ObjectActivity extends AppCompatActivity {

    private static final String TAG = "ObjectSendRequest";
    public static final String REQUEST_METHOD = "POST";
    public static final int READ_TIMEOUT = 5000;
    public static final int CONNECTION_TIMEOUT = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_object);

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
                personObject.put("firstName", "John");
                personObject.put("lastName", "Smith");
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

        protected void onPostExecute(String res) {}

    }

}