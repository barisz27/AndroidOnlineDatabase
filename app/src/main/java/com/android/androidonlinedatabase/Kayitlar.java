package com.android.androidonlinedatabase;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Kayitlar extends AppCompatActivity implements View.OnClickListener {

    private static final String KAYIT_TAG = "Kayitlar";
    private static final int CAM_REQUREST = 1313;

    private EditText etIsim;
    private EditText etEmail;
    private EditText etTelefon;
    private ImageButton ibProfilePhoto;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kayitlar_layout);

        kontrolleriBaslat();
    }


    private void kontrolleriBaslat() {
        etIsim = (EditText) findViewById(R.id.etIsim);
        etTelefon = (EditText) findViewById(R.id.etTelefon);
        etEmail = (EditText) findViewById(R.id.etEmail);
        Button bKaydet = (Button) findViewById(R.id.bKaydet);
        ibProfilePhoto = (ImageButton) findViewById(R.id.ibProfilePhoto);
        bKaydet.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        try {
            String isim = etIsim.getText().toString();
            String email = etEmail.getText().toString();
            String telefon = etTelefon.getText().toString();
            String cihaz_adi = getDeviceName(); // exception beklenen eleman..
            String tarih = getFullTime();

            // arkaplan işlemi başlatılıyor
            LoadStuff startOperation = new LoadStuff(this);
            startOperation.execute(isim, email, telefon, cihaz_adi, tarih);

        } catch (Exception e) {
            String error = e.toString();

            Dialog d = new Dialog(this);
            d.setTitle("HATA!");
            TextView tv = new TextView(this);
            tv.setText(error);
            d.setContentView(tv);
            d.show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == CAM_REQUREST) {
                Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                ibProfilePhoto.setImageBitmap(thumbnail);
            }
        } else {
            Toast.makeText(Kayitlar.this, "Lütfen resim çektiğinizden emin olun", Toast.LENGTH_SHORT).show();
        }
    }

    public void ibProfilePhotoOnClick(View view){
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAM_REQUREST);
    }

    class LoadStuff extends AsyncTask<String, String, String> {
        String domain_url;
        Context context;
        ProgressDialog progressDialog;

        public LoadStuff(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            // bağlantı adresi
            domain_url = "http://mysqlandroidproject.comxa.com/add_info.php";
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("İşlem yürütülüyor..");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {

            Log.i(KAYIT_TAG, "Arkaplan işlemleri başaltıldı");

            String isim = strings[0];
            String email = strings[1];
            String telefon = strings[2];
            String cihaz_adi = strings[3];
            String tarih = strings[4];

            try {
                URL url = new URL(domain_url);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                Log.i(KAYIT_TAG, "Bağlantı kuruldu");
                publishProgress("Sunucuya bağlanılıyor");
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);

                OutputStream os = connection.getOutputStream();
                Log.i(KAYIT_TAG, "OutputStream");

                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

                String data = URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(isim, "UTF-8") + "&" +
                        URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8") + "&" +
                        URLEncoder.encode("mobile", "UTF-8") + "=" + URLEncoder.encode(telefon, "UTF-8") + "&" +
                        URLEncoder.encode("deviceid", "UTF-8") + "=" + URLEncoder.encode(cihaz_adi, "UTF-8") + "&" +
                        URLEncoder.encode("date", "UTF-8") + "=" + URLEncoder.encode(tarih, "UTF-8");

                bufferedWriter.write(data);
                Log.i(KAYIT_TAG, "Veri sunucuya yazılıyor");
                publishProgress("Veriler sunucuya kaydediliyor");
                bufferedWriter.flush();
                bufferedWriter.close();

                os.close();

                InputStream is = connection.getInputStream();
                is.close();

                connection.disconnect();

                return "Kayıt başarıyla eklendi";

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(String... args) {
            progressDialog.setMessage(args[0]);
        }

        @Override
        protected void onPostExecute(String returnedString) {
            if (progressDialog != null) {
                progressDialog.cancel();
            }

            new AlertDialog.Builder(context).setMessage(returnedString).setTitle("Başarılı").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            }).setCancelable(false).show();
            Log.i(KAYIT_TAG, "Dialog oluşturuldu");

        }

    }

    /**
     * Returns the consumer friendly device name
     * Cihaz ismini markasıyla beraber gösterir
     */
    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        }
        return capitalize(manufacturer) + " " + model;
    }

    private static String capitalize(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        char[] arr = str.toCharArray();
        boolean capitalizeNext = true;

//        String phrase = "";
        StringBuilder phrase = new StringBuilder();
        for (char c : arr) {
            if (capitalizeNext && Character.isLetter(c)) {
//                phrase += Character.toUpperCase(c);
                phrase.append(Character.toUpperCase(c));
                capitalizeNext = false;
                continue;
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true;
            }
//            phrase += c;
            phrase.append(c);
        }

        return phrase.toString();
    }

    private String getFullTime(){ // tarih + saat gösterir..
        String fullTime;
        Calendar c = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        fullTime = dateFormat.format(c.getTime());

        return fullTime;
    }
}
