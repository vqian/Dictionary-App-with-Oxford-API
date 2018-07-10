package com.example.t_viqian.readanddisplayapicalls;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

public class UrbanDictionaryAPICall {
    Dialog myDialog;
    TextView wordDisplay, partOfSpeechDisplay, definitionDisplay;
    String word = "test", rootWord = "new", definition = "default definition", partOfSpeech = "unknown";

    protected void callUDAPI(Dialog d, String w){ // needs method for API call to run on thread correctly
        myDialog = d;
        word = w;
        new CallbackTask().execute("https://api.urbandictionary.com/v0/define?term=" + word);
    }

    public class CallbackTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
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
                definitionDisplay=(TextView)myDialog.findViewById(R.id.definitionDisplay);
                definitionDisplay.setText(e.toString());
                return e.toString();
            }
        }

        private String parseUDJSON(String UDJSON){
            try {
                JSONObject jObject = (new JSONObject(UDJSON)).getJSONArray("list").getJSONObject(0);
                if(jObject.getString("example").equals("")){
                    return "DEFINITION: " + jObject.getString("definition");
                } else{
                    return "DEFINITION: " + jObject.getString("definition") + "\n" + "EXAMPLES: " + jObject.getString("example");
                }
            } catch (JSONException e) {
                e.printStackTrace();
                return UDJSON;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            definition = parseUDJSON(result.toString());

            TextView txtclose;
            myDialog.setContentView(R.layout.custompopup);
            txtclose =(TextView) myDialog.findViewById(R.id.txtclose);
            txtclose.setText("X");
            txtclose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myDialog.dismiss();
                }
            });
            myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            wordDisplay=(TextView)myDialog.findViewById(R.id.wordDisplay);
            partOfSpeechDisplay=(TextView)myDialog.findViewById(R.id.partOfSpeechDisplay);
            definitionDisplay=(TextView)myDialog.findViewById(R.id.definitionDisplay);
            definitionDisplay.setMovementMethod(new ScrollingMovementMethod());
            wordDisplay.setText(word);
            partOfSpeechDisplay.setText("");
            definitionDisplay.setText(definition);

            myDialog.show();
        }
    }
}


