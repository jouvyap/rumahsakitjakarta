package bravostudio.rumahsakitjakarta;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;


public class DetilActivity extends ActionBarActivity {

    ArrayList<Entity> entities;
    int position;

    String bpjs = "";
    String query = "http://rumahsakitjakarta.meteor.com/cari?data=";

    ProgressDialog progressDialog;

    ArrayList<Integer> laporData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detil);

        if (android.os.Build.VERSION.SDK_INT > 20) {
            Window window = DetilActivity.this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(DetilActivity.this.getResources().getColor(R.color.secondColor));
        }

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE |
                ActionBar.DISPLAY_USE_LOGO);
        getSupportActionBar().setIcon(R.mipmap.ic_logo);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Mengecek layanan BPJS...");
        progressDialog.setCancelable(false);

        Bundle bundle = getIntent().getExtras();
        entities = (ArrayList<Entity>) bundle.getSerializable("entities");
        position = (int) bundle.getSerializable("position");

        TextView detilNama = (TextView) findViewById(R.id.detilNama);
        TextView detilKode = (TextView) findViewById(R.id.detilKode);
        TextView detilJenis = (TextView) findViewById(R.id.detilJenis);
        TextView detilAlamat = (TextView) findViewById(R.id.detilAlamat);
        TextView detilKelurahan = (TextView) findViewById(R.id.detilKelurahan);
        TextView detilKecamatan = (TextView) findViewById(R.id.detilKecamatan);
        TextView detilKota = (TextView) findViewById(R.id.detilKota);
        TextView detilKodePos = (TextView) findViewById(R.id.detilKodePos);
        TextView detilTelepon = (TextView) findViewById(R.id.detilTelepon);
        TextView detilFax = (TextView) findViewById(R.id.detilFax);
        TextView detilEmail = (TextView) findViewById(R.id.detilEmail);
        TextView detilWebsite = (TextView) findViewById(R.id.detilWebsite);

        detilNama.setText(entities.get(position).nama);
        detilKode.setText("" + entities.get(position).kode);
        detilJenis.setText(entities.get(position).jenis);
        detilAlamat.setText(entities.get(position).alamat);
        detilKelurahan.setText(entities.get(position).kelurahan);
        detilKecamatan.setText(entities.get(position).kecamatan);
        detilKota.setText(entities.get(position).kota);
        detilKodePos.setText(entities.get(position).kodePos);
        detilTelepon.setText(entities.get(position).telepon);
        detilFax.setText(entities.get(position).fax);
        detilEmail.setText(entities.get(position).email);
        detilWebsite.setText(entities.get(position).website);

        if(entities.get(position).jenis.isEmpty()){
            detilJenis.setText("-");
        }
        if(entities.get(position).alamat.isEmpty()){
            detilAlamat.setText("-");
        }
        if(entities.get(position).kelurahan.isEmpty()){
            detilKelurahan.setText("-");
        }
        if(entities.get(position).kecamatan.isEmpty()){
            detilKecamatan.setText("-");
        }
        if(entities.get(position).kota.isEmpty()){
            detilKota.setText("-");
        }
        if(entities.get(position).kodePos.isEmpty() ||
                entities.get(position).kodePos.equals("null")){
            detilKodePos.setText("-");
        }
        if(entities.get(position).telepon.isEmpty()){
            detilTelepon.setText("-");
        }
        if(entities.get(position).fax.isEmpty()){
            detilFax.setText("-");
        }
        if(entities.get(position).email.isEmpty()){
            detilEmail.setText("-");
        }
        if(entities.get(position).website.isEmpty()){
            detilWebsite.setText("-");
        }

        String[] tempNama = entities.get(position).nama.split("\\s+");
        String hasil = tempNama[0];
        for(int i = 1; i < tempNama.length; i++){
            hasil = hasil + "%20";
            hasil = hasil + tempNama[i];
        }
        query = query + hasil;

        new queryTask().execute();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detil, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_share) {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "RS " + entities.get(position).nama + ", " +
                entities.get(position).alamat + ", " + entities.get(position).kota + ", telp. " +
                entities.get(position).telepon + " via Rumah Sakit Jakarta for Android.");
            startActivity(Intent.createChooser(shareIntent, "Bagikan detail Rumah Sakit"));

            return true;
        }
        if (id == R.id.action_lapor){
            createLapor(null);
        }

        return super.onOptionsItemSelected(item);
    }

    public void goToMap(View view){
        Intent intent = new Intent(this, MapActivity.class);
        intent.putExtra("tujuan",entities.get(position).nama);
        startActivity(intent);
    }

    private class queryTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try{
                bpjs= getJsonFromServer(query);
            }
            catch(IOException e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            super.onPostExecute(result);
            progressDialog.dismiss();

            if(bpjs.equals("true")){
                TextView detilBPJS = (TextView) findViewById(R.id.detilBPJS);
                detilBPJS.setText("Ya");
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

    public void createLapor(View view){
        laporData = new ArrayList<>();
        AlertDialog.Builder builder = new AlertDialog.Builder(DetilActivity.this);
        builder.setTitle(getString(R.string.action_lapor))
                .setIcon(R.mipmap.ic_launcher)
//                .setMessage("Terdapat kesalahan data atau ingin " +
//                        "membantu kami dengan melengkapi data Rumah Sakit?")
                .setMultiChoiceItems(R.array.lapor_array, null,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                if(isChecked){
                                    laporData.add(which);
                                }
                            }
                        })
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                                "mailto", "jouvyap@gmail.com", null));
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "[Rumah Sakit Jakarta] Lapor Data");
                        emailIntent.putExtra(Intent.EXTRA_TEXT, "Nama Rumah Sakit: " + entities.get(position).nama +
                                "\nKesalahan data pada: \nData seharusnya: \n\nSaya menjamin bahwa" +
                                " data yang saya berikan valid dan benar adanya.");
                        startActivity(Intent.createChooser(emailIntent, "Send email via"));
                    }
                })
                .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
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
