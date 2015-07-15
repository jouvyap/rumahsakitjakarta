package bravostudio.rumahsakitjakarta;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Jouvy on 11-Mar-15.
 * Nyimpen lokasi yang di retrieve dari place api
 */
public class MapEntity{
    String nama;
    LatLng posisi;

    MapEntity(String nama, LatLng posisi){
        this.nama = nama;
        this.posisi = posisi;
    }
}
