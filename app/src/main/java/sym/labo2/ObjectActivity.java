package sym.labo2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ObjectActivity extends AppCompatActivity {

    private static final String TAG = "ObjectActivity";

    private Button button = null;
    private TextView resultText = null;
    private ObjectSendRequest osr = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_object);

        this.resultText = (TextView) findViewById(R.id.textView);
        this.button = (Button) findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                send();
            }
        });

        osr = new ObjectSendRequest(this.getApplicationContext());
        osr.setCommunicationEventListener( new CommunicationEventListener(){
            public boolean handleServerResponse(String response) {
                resultText.setText(response);
                return true;
            }
        });
    }


    private void send() {
        try {
            osr.sendRequest("Hello World!", "http://sym.iict.ch/rest/txt");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}