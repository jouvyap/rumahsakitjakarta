package bravostudio.rumahsakitjakarta;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;


public class JadwalActivity extends ActionBarActivity {

    String jsonString;
    String url = "http://jadwaldokterrumahsakitjakarta.meteor.com/jadwal?rumahsakit=";
    String rumahSakit;
    String jenisSakit;
    String query;

    int selector = 1;

    ArrayList<JadwalEntity> entities;

    ProgressDialog progressDialog;

    Spinner spinnerRS;
    Spinner spinnerJadwal;
    ArrayAdapter<String> adapterSpinnerJadwal;
    List<String> spinnerArray;

    RecyclerView recyclerView;
    RecyclerView.Adapter adapterView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jadwal);

        if (android.os.Build.VERSION.SDK_INT > 20) {
            Window window = JadwalActivity.this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(JadwalActivity.this.getResources().getColor(R.color.secondColor));
        }

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE |
                ActionBar.DISPLAY_USE_LOGO);
        getSupportActionBar().setIcon(R.mipmap.ic_action_go_to_today);

        entities = new ArrayList<>();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Mencari jadwal...");
        progressDialog.setCancelable(false);

        spinnerRS = (Spinner) findViewById(R.id.rsSpinner);
        spinnerJadwal= (Spinner) findViewById(R.id.jadwalSpinner);

        ArrayAdapter<CharSequence> adapterSpinnerRS = ArrayAdapter.createFromResource(
                this, R.array.rs_array,
                android.R.layout.simple_spinner_dropdown_item);
        adapterSpinnerRS.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRS.setAdapter(adapterSpinnerRS);
        spinnerRS.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch(position){
                    case 0:
                        spinnerJadwal.setVisibility(View.INVISIBLE);
                        recyclerView.setVisibility(View.INVISIBLE);
                        break;
                    case 1:
                        rumahSakit = "harapanbunda";
                        break;
                    case 2:
                        rumahSakit = "jakarta";
                        break;
                }
                if(position != 0){
                    query = url + rumahSakit;
                    selector = 1;
                    spinnerArray.clear();
                    spinnerJadwal.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.INVISIBLE);
                    new queryTask().execute();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerArray =  new ArrayList<>();
        adapterSpinnerJadwal = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, spinnerArray);
        adapterSpinnerJadwal.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerJadwal.setAdapter(adapterSpinnerJadwal);

        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view_jadwal);
        LinearLayoutManager layoutManager = new LinearLayoutManager(JadwalActivity.this);
        recyclerView.setLayoutManager(layoutManager);

        adapterView = new CustomAdapterJadwal(entities, JadwalActivity.this);
        recyclerView.setAdapter(adapterView);

        spinnerJadwal.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position!=0) {
                    entities.clear();

                    String[] temp = parent.getSelectedItem().toString().split("\\s+");
                    String hasil = temp[0];
                    for(int i = 1; i < temp.length; i++){
                        hasil = hasil + "%20";
                        hasil = hasil + temp[i];
                    }
                    jenisSakit = hasil;
                    query = url + rumahSakit + "&jenis=" + jenisSakit;
                    selector = 2;
                    new queryTask().execute();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_jadwal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private class queryTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try{
                jsonString = getJsonFromServer(query);
            }
            catch(IOException e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            super.onPostExecute(result);
            new parseTask().execute();
        }
    }

    private class parseTask extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... params) {
            if(jsonString != null){
                try {
                    JSONObject jsonObject = new JSONObject(jsonString);
                    JSONArray result = jsonObject.getJSONArray("row");

                    for (int i = 0; i < result.length(); i++) {
                        JSONObject r = result.getJSONObject(i);
                        if(selector == 1) {
                            spinnerArray.add(r.getString("jenis"));
                        }
                        else{
                            entities.add(new JadwalEntity(r.getString("nama"),
                                    r.getString("senin"),
                                    r.getString("selasa"),
                                    r.getString("rabu"),
                                    r.getString("kamis"),
                                    r.getString("jumat"),
                                    r.getString("sabtu")));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            super.onPostExecute(result);
            progressDialog.dismiss();
            if(selector == 1) {
                adapterSpinnerJadwal.notifyDataSetChanged();
                spinnerJadwal.setSelection(0);
            }
            else{
                adapterView.notifyDataSetChanged();
                recyclerView.setVisibility(View.VISIBLE);
            }
        }
    }

    //JSON buat HTTP
    public static String getJsonFromServer(String url) throws IOException {

        BufferedReader inputStream;

        URL jsonUrl = new URL(url);
        URLConnection dc = jsonUrl.openConnection();

        dc.setConnectTimeout(5000);
        dc.setReadTimeout(5000);

        inputStream = new BufferedReader(new InputStreamReader(dc.getInputStream()));

        // read the JSON results into a string
        return inputStream.readLine();
    }
}
