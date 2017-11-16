package sym.labo2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.google.gson.Gson;

public class ObjectActivity extends AppCompatActivity {

    private static final String TAG = "ObjectActivity";

    private Button button = null;
    private TextView resultText = null;
    private Switch compressSwitch = null;
    private EditText firstname = null;
    private EditText lastname = null;
    private EditText email = null;

    private ObjectSendRequest osr = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_object);

        this.resultText = (TextView)findViewById(R.id.textView);
        resultText.setMovementMethod(new ScrollingMovementMethod());

        this.button = (Button)findViewById(R.id.button);
        this.compressSwitch = (Switch)findViewById(R.id.compressSwitch);
        this.firstname = (EditText)findViewById(R.id.firstname);
        this.lastname = (EditText)findViewById(R.id.lastname);
        this.email = (EditText)findViewById(R.id.email);

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

        Gson gson = new Gson();
        Person p = new Person(firstname.getText().toString(), lastname.getText().toString(), email.getText().toString());

        try {
            osr.sendRequest(gson.toJson(p), "http://sym.iict.ch/rest/json", compressSwitch.isChecked());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}