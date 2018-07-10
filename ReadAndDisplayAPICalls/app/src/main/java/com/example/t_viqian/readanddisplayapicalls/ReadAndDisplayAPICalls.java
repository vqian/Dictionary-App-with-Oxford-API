package com.example.t_viqian.readanddisplayapicalls;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.app.*;
import android.os.*;
import android.text.method.ScrollingMovementMethod;
import android.view.*;
import android.widget.*;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import org.json.*;

import com.example.t_viqian.readanddisplayapicalls.OxfordDictionaryAPICall;
import com.example.t_viqian.readanddisplayapicalls.UrbanDictionaryAPICall;

public class ReadAndDisplayAPICalls extends AppCompatActivity {
    Dialog myDialog;
    //TextView wordDisplay, partOfSpeechDisplay, definitionDisplay;
    String word = "test"; // NEW
    //String word = "test", rootWord = "new", definition = "default definition", partOfSpeech = "unknown";
    //static Boolean rootWordFound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        myDialog = new Dialog(this);
        setContentView(R.layout.activity_read_and_display_apicalls);
    }

    public void ShowPopup(View v) throws JSONException {
        word = ((EditText)findViewById(R.id.wordInput)).getText().toString();
        OxfordDictionaryAPICall OxfordCall = new OxfordDictionaryAPICall();
        OxfordCall.callAPI(myDialog, word);
        //UrbanDictionaryAPICall UDCall = new UrbanDictionaryAPICall();
        //UDCall.callUDAPI(myDialog, word);
    }
}
