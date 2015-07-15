package bravostudio.rumahsakitjakarta;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class MapActivity extends ActionBarActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    GoogleApiClient mGoogleApiClient;
    // Request code to use when launching the resolution activity
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    // Unique tag for the error dialog fragment
    private static final String DIALOG_ERROR = "dialog_error";
    // Bool to track whether the app is already resolving an error
    private boolean mResolvingError = false;
    private static final String STATE_RESOLVING_ERROR = "resolving_error";

    GoogleMap map;
    Double latitude = -6.211544;
    Double longitude = 106.845172;

    String tujuan;

    String jsonString;
    String url = "";
    String url1 = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?radius=1000&types=hospital&key=AIzaSyAyGCUEnStO2EXXWzeYY6VrCOJKdpdhN3c&location=";
    String url2 = "https://maps.googleapis.com/maps/api/place/textsearch/json?types=hospital&key=AIzaSyAyGCUEnStO2EXXWzeYY6VrCOJKdpdhN3c&query=Rumah+Sakit+";
    String url3 = "http://data.go.id/api/action/datastore_search?resource_id=59dcaaf4-5770-4098-aa22-9c74983787b9";

    List<MapEntity> entities;
    ArrayList<Entity> entities2;

    ProgressDialog progressDialog;
    String tempId = "";
    int tempCode = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        if (android.os.Build.VERSION.SDK_INT > 20) {
            Window window = MapActivity.this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(MapActivity.this.getResources().getColor(R.color.secondColor));
        }

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE |
                ActionBar.DISPLAY_USE_LOGO);
        getSupportActionBar().setIcon(R.mipmap.ic_logo);

        Bundle bundle = getIntent().getExtras();
        tujuan = (String) bundle.get("tujuan");

        mResolvingError = savedInstanceState != null &&
                savedInstanceState.getBoolean(STATE_RESOLVING_ERROR, false);

        buildGoogleApiClient();

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        entities = new ArrayList<>();
        entities2 = new ArrayList<>();

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        map = mapFragment.getMap();
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if(tempId.equals(marker.getId())){
                    tempCode = 2;

                    String[] temp = marker.getTitle().split("\\s+");
                    String hasil = temp[0];
                    for(int i = 1; i < temp.length; i++){
                        hasil = hasil + "%20";
                        hasil = hasil + temp[i];
                    }

                    url = url3 +  "&q=" + hasil;
                    new queryTask().execute();
                }
                else{
                    tempId = marker.getId();
                    makeToast("Tekan kembali untuk detail");
                }

                return false;
            }
        });
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onMapReady(GoogleMap map){
        map.setMyLocationEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart(){
        super.onStart();
        if(!mResolvingError){
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop(){
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == REQUEST_RESOLVE_ERROR){
            mResolvingError = false;
            if(resultCode == RESULT_OK){
                if(!mGoogleApiClient.isConnecting() &&
                        !mGoogleApiClient.isConnected()){
                    mGoogleApiClient.connect();
                }
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_RESOLVING_ERROR, mResolvingError);
    }

    @Override
    public void onConnected(Bundle bundle) {
        if(tujuan.equals("terdekat")) {
            Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            if (mLastLocation != null) {
                latitude = mLastLocation.getLatitude();
                longitude = mLastLocation.getLongitude();

            }
            url = url1 + latitude + "," + longitude;
        }
        else{
            String[] temp = tujuan.split("\\s+");
            String hasil = temp[0];
            for(int i = 1; i < temp.length; i++){
                hasil = hasil + "+";
                hasil = hasil + temp[i];
            }

            url = url2 + hasil;
        }

        new queryTask().execute();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if(mResolvingError){
        }
        else if(connectionResult.hasResolution()){
            try{
                mResolvingError = true;
                connectionResult.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
            }
            catch(IntentSender.SendIntentException e){
                mGoogleApiClient.connect();
            }
        }
        else{
            showErrorDialog(connectionResult.getErrorCode());
            mResolvingError = true;
        }
    }

    private void showErrorDialog(int errorCode){
        ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
        Bundle args = new Bundle();
        args.putInt(DIALOG_ERROR, errorCode);
        dialogFragment.setArguments(args);
        dialogFragment.show(getFragmentManager(), "errordialog");
    }

    public void onDialogDismissed(){
        mResolvingError = false;
    }

    public static class ErrorDialogFragment extends DialogFragment{
        public ErrorDialogFragment(){ }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState){
            int errorCode = this.getArguments().getInt(DIALOG_ERROR);
            return GooglePlayServicesUtil.getErrorDialog(errorCode,
                    this.getActivity(), REQUEST_RESOLVE_ERROR);
        }

        @Override
        public void onDismiss(DialogInterface dialog){
            ((MapActivity)getActivity()).onDialogDismissed();
        }
    }

    private class queryTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            if(tempCode == 1){
                progressDialog.setMessage("Mencari lokasi...");
            }
            else{
                progressDialog.setMessage("Mencari detail...");
            }
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try{
                if(tempCode == 1) {
                    jsonString = getJSON(url);
                }
                else{
                    entities2.clear();
                    jsonString = getJsonFromServer(url);
                }
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

    private class parseTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            if (jsonString != null) {
                if(tempCode == 1) {
                    try {
                        JSONObject jsonObject = new JSONObject(jsonString);
                        JSONArray results = jsonObject.getJSONArray("results");

                        for (int i = 0; i < results.length(); i++) {
                            JSONObject r = results.getJSONObject(i);

                            JSONObject geometry = r.getJSONObject("geometry");
                            JSONObject location = geometry.getJSONObject("location");
                            Double lat = location.getDouble("lat");
                            Double lng = location.getDouble("lng");

                            String nama = r.getString("name");
                            LatLng posisi = new LatLng(lat, lng);

                            entities.add(new MapEntity(nama, posisi));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else{
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

                            entities2.add(new Entity(id, no, kode, nama, jenis, alamat, kelurahan,
                                    kecamatan, kota, kodePos, telepon, fax, website, email, humas));
                        }
                    }
                    catch (JSONException e){
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            super.onPostExecute(result);
            progressDialog.dismiss();

            if(tempCode == 1) {
                if (entities.isEmpty()) {
                    LatLng current = new LatLng(latitude, longitude);
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(current, 11));

                    makeToast("Rumah Sakit tidak ditemukan");
                } else {
                    if (tujuan.equals("terdekat")) {
                        for (MapEntity entity : entities) {
                            map.addMarker(new MarkerOptions()
                                    .title(entity.nama)
                                    .position(entity.posisi));
                        }

                        LatLng current = new LatLng(latitude, longitude);
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(current, 14));

                        makeToast("Ditemukan " + entities.size() + " RS disekitar Anda");
                    } else {
                        map.addMarker(new MarkerOptions()
                                .title(entities.get(0).nama)
                                .position(entities.get(0).posisi));

                        LatLng current = new LatLng(entities.get(0).posisi.latitude, entities.get(0).posisi.longitude);
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(current, 15));

                        makeToast("Ditemukan RS " + tujuan);
                    }
                }
            }
            else{
                if(entities2.isEmpty()){
                    makeToast("Detail tidak ditemukan");
                }
                else {
                    goToHasil(null);
                }
            }
        }

    }

    //JSON buat HTTPS
    public String getJSON(String address) throws IOException{
        StringBuilder builder = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(address);
        try{
            HttpResponse response = client.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if(statusCode == 200){
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                while((line = reader.readLine()) != null){
                    builder.append(line);
                }
            } else {
                Log.e(MainActivity.class.toString(),"Failed JSON object");
            }
        }catch(ClientProtocolException e) {
            e.printStackTrace();
        }
        return builder.toString();
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

    public void goToHasil(View view){
        Intent intent = new Intent(this, HasilActivity.class);
        intent.putExtra("entities", entities2);
        startActivity(intent);
    }

    public void makeToast(String text){
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
}
