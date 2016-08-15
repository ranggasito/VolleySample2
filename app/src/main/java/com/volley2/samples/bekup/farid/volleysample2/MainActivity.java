package com.volley2.samples.bekup.farid.volleysample2;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    public final static String TAG = "bekupApp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * sebuah fungsi / method yang mengelola fungsi tombol ketika di clik(click handler) secara dinamis
     * agar tidak perlu membuat fungsi untuk setiap tombol.
     *
     * fungsi ini akan menentukan behaviour / tindakan berdasarkan id tombol yang di klik
     * @param view sebuah parameter yang di butuhkan untuk click handler yang akan di isi sebuah view oleh android yaitu sebuah tombol yang
     *             di klik
     */
    public void btnOnclick(View view){
        Intent i = new Intent(getApplicationContext(), DataManager.class);

        switch (view.getId()){
            case R.id.btn_getText:
                i.putExtra("request", new DataRequest(DATA_TYPE.TEXT, "https://www.dropbox.com/s/tp14vlyb0j4dsnm/sample.txt?dl=1"));
                break;
            case R.id.btn_getXML:
                i.putExtra("request", new DataRequest(DATA_TYPE.XML, "https://www.dropbox.com/s/nhpt8nozp94g2aq/sample.xml?dl=1"));
                break;
            case R.id.btn_getJSON:
                i.putExtra("request", new DataRequest(DATA_TYPE.JSON, "https://www.dropbox.com/s/hdz1vyor42oadde/sample.json?dl=1"));
                break;
            case R.id.btn_getBitmap:
                i.putExtra("request", new DataRequest(DATA_TYPE.BITMAP, "http://cdn.wonderfulengineering.com/wp-content/uploads/2014/06/Linux-wallpapers.png"));
                break;
        }

        startActivity(i);
    }
}
