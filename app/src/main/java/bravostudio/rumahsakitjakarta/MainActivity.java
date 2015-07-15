package bravostudio.rumahsakitjakarta;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {

    ArrayList<Entity> entities;

    EditText namaTeks;
    EditText jenisTeks;
    String kota;

    String jsonString;
    String url = "http://data.go.id/api/action/datastore_search?resource_id=59dcaaf4-5770-4098-aa22-9c74983787b9";
    String query = url;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (android.os.Build.VERSION.SDK_INT > 20) {
            Window window = MainActivity.this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(MainActivity.this.getResources().getColor(R.color.secondColor));
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Mencari RS...");
        progressDialog.setCancelable(false);

        Spinner kotaSpinner = (Spinner) findViewById(R.id.kotaSpinner);
        ArrayAdapter<CharSequence> adapterSpinner = ArrayAdapter.createFromResource(
                this, R.array.kota_array,
                android.R.layout.simple_spinner_dropdown_item);
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        kotaSpinner.setAdapter(adapterSpinner);
        kotaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch(position){
                    case 0:
                        kota = "Jakarta";
                        break;
                    case 1:
                        kota = "Jakarta%20Timur";
                        break;
                    case 2:
                        kota = "Jakarta%20Barat";
                        break;
                    case 3:
                        kota = "Jakarta%20Utara";
                        break;
                    case 4:
                        kota = "Jakarta%20Selatan";
                        break;
                    case 5:
                        kota = "Jakarta%20Pusat";
                        break;
                    default:
                        kota = "Jakarta";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                kota = "Jakarta";
            }
        });

        namaTeks = (EditText) findViewById(R.id.nama);
        jenisTeks = (EditText) findViewById(R.id.jenis);

        entities = new ArrayList<>();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_tentang) {
            createAbout(null);
            return true;
        }
        if (id == R.id.action_help) {
            goToHelp(null);
            return true;
        }
        if (id == R.id.action_jadwal){
            goToJadwal(null);
            return true;
        }

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
                entities.clear();
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
                try{
                    JSONObject jsonObject =  new JSONObject(jsonString);
                    JSONObject result = jsonObject.getJSONObject("result");
                    JSONArray records = result.getJSONArray("records");

                    for(int i = 0; i < records.length(); i++){
                        JSONObject r = records.getJSONObject(i);

                        int id = r.getInt("_id");
                        int no = r.getInt("NO.");
                        int kode = r.getInt("KODE RUMAH SAKIT");
                        String nama = r.getString("NAMA  RUMAH SAKIT");
                        String jenis = r.getString("JENIS RUMAH SAKIT");
                        String alamat = r.getString("ALAMAT LOKASI  RUMAH SAKIT");
                        String kelurahan = r.getString("KELURAHAN");
                        String kecamatan = r.getString("KECAMATAN");
                        String kota = r.getString("KOTA ADMINISTRASI");
                        String kodePos = r.getString("KODE POS");
                        String telepon = r.getString("NOMOR TELEPON");
                        String fax = r.getString("NOMOR  FAXIMILE");
                        String website = r.getString("WEBSITE");
                        String email = r.getString("E-MAIL");
                        String humas = r.getString("TELEPON HUMAS");

                        entities.add(new Entity(id, no, kode, nama, jenis, alamat, kelurahan,
                                kecamatan, kota, kodePos, telepon, fax, website, email, humas));
                    }
                }
                catch (JSONException e){
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            super.onPostExecute(result);
            progressDialog.dismiss();
            if(entities.isEmpty()){
                makeToast("Hasil tidak ditemukan");
            }
            else {
                goToHasil(null);
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

    public void cariButtonOnClick(View view){
        query = url;
        boolean temp1 = false;
        if(!namaTeks.getText().toString().matches("")){
            String[] temp = String.valueOf(namaTeks.getText()).split("\\s+");
            String hasil = temp[0];
            for(int i = 1; i < temp.length; i++){
                hasil = hasil + "%20";
                hasil = hasil + temp[i];
            }
            query = query + "&q=" + hasil;

            temp1 = true;
        }
        if(!jenisTeks.getText().toString().matches("")){
            String[] temp = String.valueOf(jenisTeks.getText()).split("\\s+");
            String hasil = temp[0];
            for(int i = 1; i < temp.length; i++){
                hasil = hasil + "%20";
                hasil = hasil + temp[i];
            }
            if(temp1) {
                query = query + "%20" + hasil;
            }
            else {
                query = query + "&q=" + hasil;
            }
        }
        if(!kota.equals("Jakarta")){
            query = query + "&filters={\"KOTA%20ADMINISTRASI\":\"" + kota + "\"}";
        }

        new queryTask().execute();
    }

    public void goToHasil(View view){
        Intent intent = new Intent(this, HasilActivity.class);
        intent.putExtra("entities", entities);
        startActivity(intent);
    }

    public void goToMap(View view){
        Intent intent = new Intent(this, MapActivity.class);
        intent.putExtra("tujuan","terdekat");
        startActivity(intent);
    }

    public void goToJadwal(View view){
        Intent intent = new Intent(this, JadwalActivity.class);
        startActivity(intent);
    }

    public void goToHelp(View view){
        Intent intent = new Intent(this, HelpActivity.class);
        startActivity(intent);
    }

    public void makeToast(String text){
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    public void createAbout(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(getString(R.string.action_tentang))
                .setIcon(R.mipmap.ic_launcher)
                .setMessage("Dibuat oleh BRAVO Studio.\nVer " + BuildConfig.VERSION_NAME)
                .setPositiveButton("Rate This App", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Uri uri = Uri.parse("market://details?id=bravostudio.rumahsakitjakarta");
                        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                        try {
                            startActivity(goToMarket);
                        } catch (ActivityNotFoundException e) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=bravostudio.rumahsakitjakarta")));
                        }
                    }
                })
                .setNeutralButton("Send Feedback", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                                "mailto", "jouvyap@gmail.com", null));
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "[Rumah Sakit Jakarta] Feedback");
                        startActivity(Intent.createChooser(emailIntent, "Send email via"));
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if(keyCode == KeyEvent.KEYCODE_MENU){
            openOptionsMenu();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
