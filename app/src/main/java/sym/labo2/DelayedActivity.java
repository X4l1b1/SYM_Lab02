package sym.labo2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class DelayedActivity extends AppCompatActivity {

    private static final String TAG = "DelayedActivity";

    private Button button = null;
    private TextView resultText = null;
    private DelayedSendRequest dsr = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delayed);

        this.resultText = (TextView) findViewById(R.id.textView);
        this.button = (Button) findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                send();
            }
        });

        dsr = new DelayedSendRequest(this.getApplicationContext());
        dsr.setCommunicationEventListener( new CommunicationEventListener(){
            public boolean handleServerResponse(String response) {
                // TO DO : Parse response
                Log.i(TAG, response);
                resultText.setText(response);
                return true;
            }
        });
    }

    private void send() {
        try {
            dsr.sendRequest("Hello World!", "http://sym.iict.ch/rest/txt");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

/*
    Response from server
 11-10 15:29:05.
 213 5253-5253/sym.labo2
 I/DelayedActivity: Hello World!
 PHP_SELF: /rest/index.php
 GATEWAY_INTERFACE: CGI/1.1
 SERVER_ADDR: 193.134.218.24
 SERVER_NAME: sym.iict.ch
 SERVER_SOFTWARE: Apache/2.2.22 (Ubuntu)
 SERVER_PROTOCOL: HTTP/1.1
 REQUEST_METHOD: POST
 REQUEST_TIME: 1510324173
 QUERY_STRING:
 DOCUMENT_ROOT: /var/www/sym/web
 HTTP_ACCEPT_ENCODING: gzip
 HTTP_CONNECTION: Keep-Alive
 HTTP_HOST: sym.iict.ch
 HTTP_USER_AGENT: Dalvik/2.1.0 (Linux; U; Android 8.0.0; Android SDK built for x86 Build/OSR1.170901.027)REMOTE_ADDR: 10.192.91.251
 REMOTE_PORT: 46770
 SCRIPT_FILENAME: /var/www/sym/web/rest/index.phpSERVER_ADMIN: fabien.dutoit@heig-vd.ch
 SERVER_PORT: 80
 SERVER_SIGNATURE: <address>Apache/2.2.22 (Ubuntu) Server at sym.iict.ch Port 80</address>
 SCRIPT_NAME: /rest/index.php
 REQUEST_URI: /rest/txt
 */