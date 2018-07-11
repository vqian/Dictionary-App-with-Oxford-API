package com.example.t_viqian.readanddisplayapicalls;

import android.support.v7.app.AppCompatActivity;
import android.app.*;
import android.os.*;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.*;
import android.widget.*;

public class ReadAndDisplayAPICalls extends AppCompatActivity {
    Dialog myDialog;
    String word = "test", textInput = "sample text";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        myDialog = new Dialog(this);
        setContentView(R.layout.activity_read_and_display_apicalls);
    }

    public void ShowPopup(View v) {
        textInput = ((EditText)findViewById(R.id.wordInput)).getText().toString();
        String[] rawWords = textInput.split(" ");
        SpannableString ss = new SpannableString(textInput);
        int startIndex = 0;
        for(final String parsedWord : rawWords){
            final String redactedWord = parsedWord.replace(".", "").replace(",", "").replace("?", "").replace("!","");
            ClickableSpan wordSpan = new ClickableSpan() {
                @Override
                public void onClick(View textView) {
                    word = redactedWord;
                    //BingDictionaryAPICall BingCall = new BingDictionaryAPICall();
                    //BingCall.callBingAPI(myDialog, word);
                    OxfordDictionaryAPICall OxfordCall = new OxfordDictionaryAPICall();
                    OxfordCall.callAPI(myDialog, word);
                    //UrbanDictionaryAPICall UDCall = new UrbanDictionaryAPICall();
                    //UDCall.callUDAPI(myDialog, word);
                }
            };
            ss.setSpan(wordSpan, startIndex, startIndex + parsedWord.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            startIndex = startIndex + parsedWord.length() + 1;
        }
        ((EditText)findViewById(R.id.wordInput)).setText(ss);
        ((EditText)findViewById(R.id.wordInput)).setMovementMethod(LinkMovementMethod.getInstance());
    }
}
