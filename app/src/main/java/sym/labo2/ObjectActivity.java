package sym.labo2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class ObjectActivity extends AppCompatActivity {

    private static final String TAG = "ObjectActivity";

    //UI elements
    private Button button = null;
    private TextView resultText = null;
    private Switch compressSwitch = null;
    private EditText firstname = null;
    private EditText lastname = null;
    private EditText email = null;

    //GSON object
    private Gson gson = null;

    //Request sender
    private ObjectSendRequest osr = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_object);

        this.resultText = (TextView)findViewById(R.id.textView);
        resultText.setMovementMethod(new ScrollingMovementMethod());

        //Set UI elements
        this.button = (Button)findViewById(R.id.button);
        this.compressSwitch = (Switch)findViewById(R.id.compressSwitch);
        this.firstname = (EditText)findViewById(R.id.firstname);
        this.lastname = (EditText)findViewById(R.id.lastname);
        this.email = (EditText)findViewById(R.id.email);

        this.gson = new Gson();

        //Button behaviour
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                send();
            }
        });

        //Create Request object
        osr = new ObjectSendRequest(this.getApplicationContext());
        osr.setCommunicationEventListener( new CommunicationEventListener(){
            public boolean handleServerResponse(String response) {
                //Display server answer
                JsonObject jsonObject = gson.fromJson(response, JsonObject.class);
                jsonObject.get("Person");

                Person p = gson.fromJson(jsonObject.get("Person"), Person.class);
                Log.i(TAG, "Created object from JSON : firstname = " + p.getFirstname()
                        + ", lastname = " + p.getLastname() + ", email = " + p.getEmail());

                resultText.setText(jsonObject.get("Person").toString());
                return true;
            }
        });
    }


    private void send() {

        //Create object from TextEdit
        Person p = new Person(firstname.getText().toString(), lastname.getText().toString(), email.getText().toString());

        //Create JSON from object
        JsonObject jsonObject = new JsonObject();
        JsonElement jsonElement = gson.toJsonTree(p);
        jsonObject.add("Person", jsonElement);

        //Send request
        try {
            osr.sendRequest(jsonObject.toString(), "http://sym.iict.ch/rest/json", compressSwitch.isChecked());
            Log.i(TAG, gson.toJson(jsonObject));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}