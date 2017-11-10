package sym.labo2;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class AsyncActivity extends AppCompatActivity {

    private static final String TAG = "AsyncActivity";

    private Button button = null;
    private TextView resultText = null;
    private AsyncSendRequest asr = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_async);

        this.resultText = (TextView) findViewById(R.id.textView);
        this.button = (Button) findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                send();
            }
        });

        asr = new AsyncSendRequest(this.getApplicationContext());
        asr.setCommunicationEventListener( new CommunicationEventListener(){
            public boolean handleServerResponse(String response) {
                resultText.setText(response);
                return true;
                }
        });
    }

    private void send() {
        try {
            asr.sendRequest("Hello World!", "http://sym.iict.ch/rest/txt");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}