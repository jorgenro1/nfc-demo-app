package com.dgnest.nfcdemo;

import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.TextView;

import java.math.BigInteger;


public class MainActivity extends ActionBarActivity {

    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;

    private TextView tvTitle;

    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvTitle = (TextView) findViewById(R.id.tvTitle);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        pendingIntent = PendingIntent.getActivity(
                this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        nfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        NfcProcessThread nfcThread = new NfcProcessThread(intent);
        Log.d("NfcActivity: ", "new intent");
        nfcThread.start();
    }

    public class NfcProcessThread extends Thread {
        Intent intent;

        public NfcProcessThread(Intent intent) {
            this.intent = intent;
        }

        @Override
        public void run() {
            Looper.prepare();
            final String action = intent.getAction();
            Log.d("NfcWrite", "Action: " + action);

            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

            uid = bin2hex(tag.getId());
            Log.d("NfcActivity: ", "leido = " + uid);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (uid.equals("04A5484ABD3980")) {
                        tvTitle.setText("prender led");
                    } else if (uid.equals("0459B24ABD3980")) {
                        tvTitle.setText("apagar led");
                    } else {
                        tvTitle.setText("tag desconocido");
                    }


                }
            });
        }
    }

    //To display the UID
    static String bin2hex(byte[] data) {
        return String.format("%0" + (data.length * 2) + "X", new BigInteger(1, data));
    }
}
