package com.android.androidonlinedatabase;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button bEkle;
    private Button bGoster;
    private TextView tvAgDurumu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        kontrolleriBaslat();

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            tvAgDurumu.setVisibility(View.INVISIBLE);
        }
        else {
            bEkle.setEnabled(false);
            bGoster.setEnabled(false);
        }
    }

    private void kontrolleriBaslat()
    {
        bEkle = (Button) findViewById(R.id.bEkle);
        bEkle.setOnClickListener(this);
        bGoster = (Button) findViewById(R.id.bGoster);
        bGoster.setOnClickListener(this);
        tvAgDurumu = (TextView) findViewById(R.id.tvAgDurumu);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId())
        {
            case R.id.bEkle:
                startActivity(new Intent(this, Kayitlar.class));
                break;

            case R.id.bGoster:

                break;
        }
    }
}
