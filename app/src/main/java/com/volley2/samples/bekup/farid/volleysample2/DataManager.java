package com.volley2.samples.bekup.farid.volleysample2;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

public class DataManager extends AppCompatActivity {
    public final static String TAG = "bekupApp";
    final static String EXTRA_DATA_ERROR_MESSAGE = "The data passed is invalid, Please use class DataRequest for this operation";
    private static final int DISK_CACHE_SIZE = 1024 * 1024 * 10;
    private DataRequest data;
    private View viewResult;
    private ScrollView container;
    private static RequestQueue queueManager;
    Toast _toast;
    private ProgressDialog _progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.data_manager);
        setTitle("Data Manager Activity");

        _toast = Toast.makeText(this, "", Toast.LENGTH_SHORT);

        Bundle extras = getIntent().getExtras();
        if (extras.get("request") != null) {
            this.data = (DataRequest) extras.getSerializable("request");
            run();
        } else {
            showToast(EXTRA_DATA_ERROR_MESSAGE);
            finish();
        }

        container = (ScrollView) findViewById(R.id.lyScrollView);

    }

    @Override
    protected void onStop() {
        queueManager.stop();
        _progress.dismiss();
        super.onStop();
    }

    private void showProgress(boolean visibility) {
        if (visibility) {
            if (_progress == null) {
                _progress = new ProgressDialog(this);
                _progress.setTitle("Downloading data");
                _progress.setMessage("Please wait...");
                _progress.setCancelable(false);
            }
            _progress.show();
        } else {
            _progress.dismiss();
        }
    }

    public void btn_click(View view) {
        finish();
    }

    public void showToast(String msg) {
        _toast.setText(msg);
        _toast.show();
    }

    /**
     * fungsi ini menguraikan data menjadi sebuah request dan langsung di add ke Volley agar langsung diproses
     */
    private void run() {
        if (queueManager == null) {
            Cache diskcache = new DiskBasedCache(getCacheDir(), DISK_CACHE_SIZE);
            Network netCache = new BasicNetwork(new HurlStack());

            queueManager = new RequestQueue(diskcache, netCache);
        }

        queueManager.start();

        showProgress(true);

        if (data.type == DATA_TYPE.TEXT || data.type == DATA_TYPE.XML) {

            StringRequest strReq = new StringRequest(Request.Method.GET, data.get_url(), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    respondString(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    onErrorRequest(error);
                }
            });

            strReq.setRetryPolicy(new DefaultRetryPolicy(3000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            queueManager.add(strReq);

        } else if (data.type == DATA_TYPE.JSON) {

            JsonObjectRequest json_req = new JsonObjectRequest(Request.Method.GET, data.get_url(), null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            responeJson(response);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    onErrorRequest(error);
                }
            }
            );


            queueManager.add(json_req);
        } else if(this.data.type == DATA_TYPE.BITMAP ) {

            ImageRequest img_req = new ImageRequest(data.get_url(), new Response.Listener<Bitmap>() {
                @Override
                public void onResponse(Bitmap response) {
                    respondBitmap(response);
                }
            }, 0, 0, ImageView.ScaleType.FIT_XY, Bitmap.Config.ALPHA_8, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    onErrorRequest(error);
                }
            });

            queueManager.add(img_req);
        }
    }

    private void responeJson(JSONObject response) {
        showProgress(false);
        showToast("Data downloaded");
        String result = "";

        try{
            JSONArray jarray = response.getJSONArray("data");

            for (int i = 0 ;i<jarray.length();i++){
                JSONObject row = jarray.getJSONObject(i);
                result += "===============" + " data " + i + " =============== \n";
                result += "nama = " + row.get("nama") + "\n";
                result += "gender = " + row.get("gender") + "\n";
                result += "alamat = " + row.get("alamat") + "\n";
            }

            container.removeAllViews();
            TextView tx = new TextView(getApplicationContext());
            tx.setText(result);

            container.addView(tx);
            Log.i(TAG, "string result = " + response.toString());
        }catch (Exception e){
            showToast("Json data is not valid");
            e.printStackTrace();
        }

    }


    public void onErrorRequest(VolleyError errorData) {
        showProgress(false);


        errorData.printStackTrace();

        AlertDialog.Builder alert_manager = new AlertDialog.Builder(this);

        alert_manager.setNegativeButton("Abort", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                showToast("Error Download. make sure you have internet or check the url \n");
            }
        });

        alert_manager.setPositiveButton("Use Cache", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                load_cache();
            }
        });

        AlertDialog confirm = alert_manager.create();
        confirm.setTitle("Download failed");
        confirm.setMessage("Unable to download data, you can use cache instead. do you want to use cache data?");
        confirm.show();

    }

    public void load_cache(){
        Cache.Entry entry = queueManager.getCache().get(this.data.get_url());
        if(entry != null){
            if(this.data.type == DATA_TYPE.TEXT || this.data.type == DATA_TYPE.XML){
                String cache_result = new String(entry.data);
                respondString(cache_result);

            }else if(this.data.type == DATA_TYPE.BITMAP){
                respondBitmap(BitmapFactory.decodeByteArray(entry.data,0,entry.data.length));
            }else if(this.data.type == DATA_TYPE.JSON){
                try{
                    responeJson(new JSONObject(new String(entry.data)));
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
            showToast("data has been successfully loaded from cache");
        }else{
            showToast("failed to load data from cache");
        }
    }
    /**
     * @param data
     */
    public void respondString(String data) {
        container.removeAllViews();
        TextView tx = new TextView(this);
        tx.setText(data);

        container.addView(tx);

        showProgress(false);
        showToast("Data downloaded");
    }

    public void respondBitmap(Bitmap bmp) {
        container.removeAllViews();

        ImageView imgv = new ImageView(getApplicationContext());
        imgv.setImageBitmap(bmp);

        container.addView(imgv);
        showProgress(false);
        showToast("Data downloaded");
    }
}
