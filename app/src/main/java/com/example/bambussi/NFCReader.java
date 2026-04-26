package com.example.bambussi;

import static android.content.Intent.FLAG_ACTIVITY_REORDER_TO_FRONT;
import android.content.Intent;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class NFCReader extends AppCompatActivity implements NfcAdapter.ReaderCallback{
    NfcAdapter nfcAdapter;
    TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfcreader);

        text = (TextView) findViewById(R.id.txtNFCtext);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null){
            Log.d("ERROR", "No NFC reader detected");
            finish();
        }

        findViewById(R.id.btnBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangePage();
            }
        });
    }

    @Override
    public void onTagDiscovered(Tag tag) {
        Ndef ndef = Ndef.get(tag);
        if (ndef == null) {
            Log.d("ERROR", "NDEF not supported");
        } else {
            try {
                ndef.connect();
                NdefMessage ndefMessage = ndef.getNdefMessage();
                byte[] ndefMessageBytes = ndefMessage.toByteArray();
                if (ndefMessageBytes != null) {
                    String value = new String(ndefMessageBytes, StandardCharsets.UTF_8).split("en")[1];
                    Log.d("TAG", value);
                    Matchmaker.CheckTagValue(this, value);
                    ndef.close();
                    finish();
                }
                ndef.close();
            } catch (IOException e) {
                Log.d("ERROR", "NDEF connect to tag IOException " + e.getMessage());
            } catch (FormatException e) {
                Log.d("ERROR", "NDEF connect to tag RunTimeException " + e.getMessage());
            }
        }
    }

    public void ChangePage(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (nfcAdapter != null)
            nfcAdapter.disableReaderMode(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (nfcAdapter != null){
            if (!nfcAdapter.isEnabled()){
                startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
            }
            Bundle options = new Bundle();
            options.putInt(NfcAdapter.EXTRA_READER_PRESENCE_CHECK_DELAY, 250);
            nfcAdapter.enableReaderMode(this, this,
                    NfcAdapter.FLAG_READER_NFC_A |
                            NfcAdapter.FLAG_READER_NFC_B |
                            NfcAdapter.FLAG_READER_NFC_F |
                            NfcAdapter.FLAG_READER_NFC_V |
                            NfcAdapter.FLAG_READER_NFC_BARCODE,
                    options);
        }
    }
}