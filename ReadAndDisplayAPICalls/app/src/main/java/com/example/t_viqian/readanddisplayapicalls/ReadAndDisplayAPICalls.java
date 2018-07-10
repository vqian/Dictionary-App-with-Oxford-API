package com.example.t_viqian.readanddisplayapicalls;

import android.support.v7.app.AppCompatActivity;
import android.app.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import org.json.*;

public class ReadAndDisplayAPICalls extends AppCompatActivity {
    Dialog myDialog;
    String word = "test";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        myDialog = new Dialog(this);
        setContentView(R.layout.activity_read_and_display_apicalls);
    }

    public void ShowPopup(View v) throws JSONException {
        word = ((EditText)findViewById(R.id.wordInput)).getText().toString();
        //OxfordDictionaryAPICall OxfordCall = new OxfordDictionaryAPICall();
        //OxfordCall.callAPI(myDialog, word);
        UrbanDictionaryAPICall UDCall = new UrbanDictionaryAPICall();
        UDCall.callUDAPI(myDialog, word);
    }
}
