package sym.labo2;

import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

public class TextActivity extends AppCompatActivity {

    private static final String TAG = "TextActivity";

    //Server address
    private final String TXT_SERVER  = "http://sym.iict.ch/rest/txt";

    //UI elements
    private Button button;
    private TextView resultText;
    private Switch delaySwitch;
    private EditText text;

    //Communication manager
    private CommunicationManager communicationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //Scrollable text view
        this.resultText = findViewById(R.id.textView);
        resultText.setMovementMethod(new ScrollingMovementMethod());

        //Set UI elements
        this.button = findViewById(R.id.button);
        this.delaySwitch = findViewById(R.id.delaySwitch);
        this.text = findViewById(R.id.text);

        //Button behaviour
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                send();
            }
        });

        //Create request object
        communicationManager = new CommunicationManager(this.getApplicationContext());
        communicationManager.setCommunicationEventListener( new CommunicationEventListener(){
            public boolean handleServerResponse(String response) {

                Log.i(TAG, response);
                resultText.setText(response);
                return true;
            }
        });

    }

    private void send() {

        //Send request
        try {
            communicationManager.sendRequest(text.getText().toString(), TXT_SERVER, ContentType.TXT,
                    false, delaySwitch.isChecked());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}