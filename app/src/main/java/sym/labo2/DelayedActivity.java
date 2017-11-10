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