package sym.labo2;

import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

/**
 * Activity used to send an object serialized as JSON or XML to a server and display the response
 */
public class ObjectActivity extends AppCompatActivity {

    private static final String TAG = ObjectActivity.class.getSimpleName();

    //Servers addresses
    private final String JSON_SERVER = "http://sym.iict.ch/rest/json";
    private final String XML_SERVER  = "http://sym.iict.ch/rest/xml";

    //UI elements
    private Button button;
    private TextView resultText;
    private Switch compressSwitch;
    private Switch serializationSwitch;
    private EditText firstname;
    private EditText name;
    private EditText phone;
    private Spinner gender;

    //Communication manager
    private CommunicationManager communicationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_object);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        this.resultText = (TextView)findViewById(R.id.textView);
        resultText.setMovementMethod(new ScrollingMovementMethod());

        //Sets UI elements
        this.button = findViewById(R.id.button);
        this.compressSwitch = findViewById(R.id.compressSwitch);
        this.serializationSwitch = findViewById(R.id.serializationSwitch);
        this.firstname = findViewById(R.id.firstname);
        this.name = findViewById(R.id.name);
        this.phone = findViewById(R.id.phone);
        this.gender = findViewById(R.id.gender);

        //Init gender spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.genders, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gender.setAdapter(adapter);

        //Button behaviour
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                send();
            }
        });

        //Sets behaviour when notified with response
        communicationManager = new CommunicationManager(this.getApplicationContext());
        communicationManager.setCommunicationEventListener( new CommunicationEventListener(){
            public boolean handleServerResponse(String response) {
                resultText.setText(response);
                return true;
            }
        });
    }


    private void send() {

        //Create object from TextEdit fields
        Person p = new Person(firstname.getText().toString(), name.getText().toString(),
                gender.getSelectedItem().toString(), phone.getText().toString());

        RequestManager rd;

        //Serialize to JSON formal by default or XML
        if(!serializationSwitch.isChecked()) {
            rd = new RequestManager(p, DataType.JSON, compressSwitch.isChecked(), JSON_SERVER);
        } else {
            rd = new RequestManager(p, DataType.XML, compressSwitch.isChecked(), XML_SERVER);
        }

        //Send request
        try {
            communicationManager.sendRequest(rd, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}