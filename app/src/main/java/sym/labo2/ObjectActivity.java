package sym.labo2;

import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.jdom2.DocType;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.XMLOutputter;

public class ObjectActivity extends AppCompatActivity {

    private static final String TAG = "ObjectActivity";

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

        //Set UI elements
        this.button = findViewById(R.id.button);
        this.compressSwitch = findViewById(R.id.compressSwitch);
        this.serializationSwitch = findViewById(R.id.serializationSwitch);
        this.firstname = findViewById(R.id.firstname);
        this.name = findViewById(R.id.name);
        this.phone = findViewById(R.id.phone);
        this.gender = findViewById(R.id.gender);

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

        //Create Request object
        communicationManager = new CommunicationManager(this.getApplicationContext());
        communicationManager.setCommunicationEventListener( new CommunicationEventListener(){
            public boolean handleServerResponse(String response) {

                if(!serializationSwitch.isChecked())
                    deserializeJSON(response);
                else
                    deserializeXML(response);

                return true;
            }
        });
    }

    private String serializeJSON(Person p) {
        //Create JSON from object using GSON
        JsonObject jsonObject = new JsonObject();
        JsonElement jsonElement = new Gson().toJsonTree(p);
        jsonObject.add("person", jsonElement);

        return jsonObject.toString();
    }

    private void deserializeJSON(String s) {

        //GSON object
        Gson gson = new Gson();

        //Recreate object from received response
        JsonObject jsonObject = gson.fromJson(s, JsonObject.class);

        Person p = gson.fromJson(jsonObject.get("Person"), Person.class);
        Log.i(TAG, "Created object from response : firstname = " + p.getFirstname()
                + ", name = " + p.getName() + ", gender = " + p.getGender() + ", phone = " + p.getPhone());

        resultText.setText(jsonObject.get("Person").toString());
    }

    private void deserializeXML(String s) {
        resultText.setText(s);
    }

    private String serializeXML(Person p) {
        //Create XML from object using XML
        DocType dtype = new DocType("directory", "http://sym.iict.ch/directory.dtd");
        Element directory = new Element("directory");
        Document doc = new Document(directory, dtype);

        Element person = new Element("person");
        person.addContent(new Element("name").setText(p.getName()));
        person.addContent(new Element("firstname").setText(p.getFirstname()));
        person.addContent(new Element("gender").setText("female"));
        person.addContent(new Element("phone")
                .setAttribute("type", "home")
                .setText(p.getPhone()));
        directory.addContent(person);

        return new XMLOutputter().outputString(doc);
    }

    private void send() {

        //Create object from TextEdit
        Person p = new Person(firstname.getText().toString(), name.getText().toString(),
                gender.getSelectedItem().toString(), phone.getText().toString());

        String serializedObject;
        String serverAddress;

        ContentType contentType;

        if(!serializationSwitch.isChecked()) {
            serializedObject = serializeJSON(p);
            serverAddress = JSON_SERVER;
            contentType = ContentType.JSON;
        } else {
            serializedObject = serializeXML(p);
            serverAddress = XML_SERVER;
            contentType = ContentType.XML;
        }

        //Send request
        try {
            communicationManager.sendRequest(serializedObject, serverAddress, contentType
                    , compressSwitch.isChecked(), false);
            Log.i(TAG, "Sending : " + serializedObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}