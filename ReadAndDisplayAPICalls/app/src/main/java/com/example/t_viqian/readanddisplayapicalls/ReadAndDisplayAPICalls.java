package com.example.t_viqian.readanddisplayapicalls;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.app.*;
import android.os.*;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.*;
import android.widget.*;
import java.io.*;
import java.net.URL;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import org.json.*;

import com.example.t_viqian.readanddisplayapicalls.Definition;
import com.example.t_viqian.readanddisplayapicalls.UDParser;

public class ReadAndDisplayAPICalls extends AppCompatActivity {
    Dialog myDialog;
    TextView wordDisplay, partOfSpeechDisplay, definitionDisplay;
    String word = "test", rootWord = "new", definition = "default definition", partOfSpeech = "unknown";
    static Boolean rootWordFound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        myDialog = new Dialog(this);
        setContentView(R.layout.activity_read_and_display_apicalls);
    }

    public void ShowPopup(View v) throws JSONException {
        word = ((EditText)findViewById(R.id.wordInput)).getText().toString();
        callAPI();
        //callUDAPI();
    }

    private void callAPI(){ // needs method for API call to run on thread correctly
        new CallbackTask().execute(inflections());

    }

    private void callUDAPI() throws JSONException {
        UDParser udparser = new UDParser("http://api.urbandictionary.com/v0/");
        String JSONData = udparser.getJSONData(word);
        Definition[] test = udparser.getDefinitionsWithJSONData(JSONData);
        for(int i = 0; i < test.length; i++)
        {
            definition += "WORD: " + test[i].getWordName() + " DEFINITION: " + test[i].getDefinition() + "/n";
        }
    }

    private String inflections() {
        final String language = "en";
        final String word_id = word.toLowerCase(); //word id is case sensitive and lowercase is required
        return "https://od-api.oxforddictionaries.com:443/api/v1/inflections/" + language + "/" + word_id;
    }

    private String dictionaryEntries() {
        final String language = "en";
        final String word_id = rootWord.toLowerCase(); //word id is case sensitive and lowercase is required
        return "https://od-api.oxforddictionaries.com:443/api/v1/entries/" + language + "/" + word_id;
    }

    //in android calling network requests on the main thread forbidden by default
    //create class to do async job
    private class CallbackTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            final String app_id = "5091ae41";
            final String app_key = "79df4260fc4b69036f1a12382579c626";

            HostnameVerifier allHostsValid = new HostnameVerifier() { // ADDED
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid); // ADDED

            try {
                URL url = new URL(params[0]);
                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.setRequestProperty("Accept","application/json");
                urlConnection.setRequestProperty("app_id",app_id);
                urlConnection.setRequestProperty("app_key",app_key);

                // read the output from the server
                BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();

                String line = null;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line + "\n");
                }

                return stringBuilder.toString();

            }
            catch (Exception e) {
                e.printStackTrace();
                return e.toString();
            }
        }

        private String parseJSONInflections(String result){
            try {
                JSONObject jObject = (new JSONObject(result)).getJSONArray("results").getJSONObject(0).getJSONArray("lexicalEntries").getJSONObject(0);
                JSONObject jObject1 = jObject.getJSONArray("inflectionOf").getJSONObject(0);
                partOfSpeech = jObject.getString("lexicalCategory");
                return jObject1.getString("id");
            } catch (JSONException e) {
                e.printStackTrace();
                return "error";
            }
        }

        private String parseJSONDefinitions(String result){ // DOESN'T WORK YET
            try {
                JSONArray jArray = (new JSONObject(result)).getJSONArray("results").getJSONObject(0).getJSONArray("lexicalEntries").getJSONObject(0).getJSONArray("entries");
                JSONArray jArray1 = jArray.getJSONObject(0).getJSONArray("senses").getJSONObject(0).getJSONArray("definitions");
                return jArray1.getString(0);
            } catch (JSONException e) {
                e.printStackTrace();
                return result;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if(rootWordFound){
                definition = parseJSONDefinitions(result);

                TextView txtclose;
                myDialog.setContentView(R.layout.custompopup);
                txtclose =(TextView) myDialog.findViewById(R.id.txtclose);
                txtclose.setText("X");
                txtclose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        myDialog.dismiss();
                        rootWordFound = false;
                    }
                });
                myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                wordDisplay=(TextView)myDialog.findViewById(R.id.wordDisplay);
                partOfSpeechDisplay=(TextView)myDialog.findViewById(R.id.partOfSpeechDisplay);
                definitionDisplay=(TextView)myDialog.findViewById(R.id.definitionDisplay);
                definitionDisplay.setMovementMethod(new ScrollingMovementMethod());
                wordDisplay.setText(word);
                if(!(partOfSpeech.equals("Verb")||partOfSpeech.equals("Noun"))){
                    partOfSpeechDisplay.setText(partOfSpeech.toLowerCase().substring(0,3) + ".");
                } else{
                    partOfSpeechDisplay.setText(partOfSpeech.toLowerCase().charAt(0) + ".");
                }
                if(rootWord.equals(word)){
                    definitionDisplay.setText(definition);
                }   else{
                    definitionDisplay.setText(rootWord.toUpperCase() + ": " + definition);
                }

                myDialog.show();
            } else{
                rootWord = parseJSONInflections(result);
                rootWordFound = true;
                new CallbackTask().execute(dictionaryEntries());
            }
        }
    }
}
