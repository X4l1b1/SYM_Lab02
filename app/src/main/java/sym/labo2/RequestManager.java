package sym.labo2;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.jdom2.DocType;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

/**
 * RequestManager
 * Stores DataType to send (TXT, JSON or XML), the object to send (String for TXT or Person for
 * JSON ans XML), a boolean that indicates if the request must be compressed and the url of the
 * destination.
 * Privides methods such as compress(), decompress(), serializeJSON(), deserializeJSON(),
 * serializeXML(), deserializeXML(),
 */
public class RequestManager {

    private static final String TAG = RequestManager.class.getSimpleName();

    private Object data;
    private DataType type;
    private boolean compress;
    private String url;

    public RequestManager(Object data, DataType type, boolean compress, String url) {
        this.data = data;
        this.type = type;
        this.compress = compress;
        this.url = url;
    }

    public boolean isCompressed() { return compress; }

    public DataType getDataType() { return type; }

    public String getUrl() { return url; }

    public byte[] constructRequestBody() {

        byte[] requestBody = null;

        //Serialize and compress according to options
        switch(type) {
            case JSON :
                if(compress) {
                    requestBody = compress(serializeJSON((Person)data));
                } else {
                    requestBody = serializeJSON((Person)data).getBytes();
                    Log.i(TAG, "Data : " + new String(requestBody, 0));
                }
                break;
            case XML  :
                if(compress) {
                    requestBody = compress(serializeXML((Person)data));
                } else {
                    requestBody = serializeXML((Person)data).getBytes();
                    Log.i(TAG, "Data : " + new String(requestBody, 0));
                }
                break;
            case TXT :
                requestBody = ((String)data).getBytes();
                Log.i(TAG, "Data : " + data);
                break;
        }

        return requestBody;
    }

    //Deserialize and decompress according to options
    public String handleResponse(byte[] response) {

        String responseBody = null;

        switch(type) {
            case JSON :
                if(compress) {
                    responseBody = deserializeJSON(decompress(response));
                } else {
                    String s = new String(response, 0);
                    Log.i(TAG, "Response : " + s);
                    responseBody = deserializeJSON(s);
                }
                break;
            case XML  :
                if(compress) {
                    responseBody = deserializeXML(decompress(response));
                } else {
                    String s = new String(response, 0);
                    Log.i(TAG, "Response : " + s);
                    responseBody = deserializeXML(s);
                }
                break;
            case TXT :
                responseBody = new String(response, 0);
                break;
        }

        return responseBody;
    }

    //Serialize a Person object to JSON format
    private String serializeJSON(Person p) {

        //Create JSON from object using GSON
        JsonObject jsonObject = new JsonObject();
        JsonElement jsonElement = new Gson().toJsonTree(p);
        jsonObject.add("person", jsonElement);

        return jsonObject.toString();
    }

    //Deserialize a JSON to a Person object
    private String deserializeJSON(String s) {

        //GSON object
        Gson gson = new Gson();

        //Recreate object from received response
        JsonObject jsonObject = gson.fromJson(s, JsonObject.class);

        Person p = gson.fromJson(jsonObject.get("person"), Person.class);
        Log.i(TAG, "Reconstruct object from JSON : firstname = " + p.getFirstname()
                + ", name = " + p.getName() + ", gender = " + p.getGender()
                + ", phone = " + p.getPhone());

        return jsonObject.get("person").toString();
    }

    //Serialize a Person object to XML format
    private String serializeXML(Person p) {

        //Create XML from object using XML
        DocType dtype = new DocType("directory", "http://sym.iict.ch/directory.dtd");
        Element directory = new Element("directory");
        Document doc = new Document(directory, dtype);

        Element person = new Element("person");
        person.addContent(new Element("name").setText(p.getName()));
        person.addContent(new Element("firstname").setText(p.getFirstname()));
        person.addContent(new Element("gender").setText(p.getGender()));
        person.addContent(new Element("phone")
                .setAttribute("type", "home")
                .setText(p.getPhone()));
        directory.addContent(person);

        return new XMLOutputter().outputString(doc);
    }

    //Deserialize a XML to a Person object
    private String deserializeXML(String s) {

        //Received XML and object to reconstruct
        String xml = "";
        Person p;

        SAXBuilder builder = new SAXBuilder();
        InputStream is = new ByteArrayInputStream(s.getBytes());

        //Recreate object from received response
        try {
            Document doc = builder.build(is);
            Element rootElement = doc.getRootElement();
            Element person = rootElement.getChild("person");

            p = new Person(person.getChildText("firstname"), person.getChildText("name"),
                    person.getChildText("gender"), person.getChildText("phone"));

            Log.i(TAG, "Reconstruct object from XML : firstname = " + p.getFirstname()
                    + ", name = " + p.getName() + ", gender = " + p.getGender()
                    + ", phone = " + p.getPhone());

            xml = new XMLOutputter().outputString(person);

        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return xml;
    }

    //Compress data to send
    private byte[] compress(String s) {

        byte[] compressedData = null;

        Log.i(TAG, "Data before compression : " + s);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Deflater def = new Deflater(Deflater.BEST_COMPRESSION, true);
        DeflaterOutputStream dos = new DeflaterOutputStream(baos, def);

        try {

            dos.write(s.getBytes());
            dos.flush();
            dos.close();
            baos.flush();
            baos.close();

            compressedData = baos.toByteArray();

        } catch(IOException e) {
            e.printStackTrace();
        }

        Log.i(TAG, "Data after compression : " + new String(compressedData, 0));

        return compressedData;
    }

    //Decompress received data
    private String decompress(byte[] bytes) {

        String decompressedData = null;

        Log.i(TAG, "Data before decompression : " + new String(bytes, 0));

        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        Inflater inf = new Inflater(true);
        InflaterInputStream iis = new InflaterInputStream(bis, inf);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        int bytesRead;

        try{

            while ((bytesRead = iis.read()) != -1) {
                baos.write(bytesRead);
            }

            baos.flush();
            baos.close();

            decompressedData = baos.toString();

        } catch(IOException e) {
            e.printStackTrace();
        }

        Log.i(TAG, "Data after decompression : " + decompressedData);

        return decompressedData;

    }
}
