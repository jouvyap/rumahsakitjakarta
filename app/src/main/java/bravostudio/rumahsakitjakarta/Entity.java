package bravostudio.rumahsakitjakarta;

import java.io.Serializable;

/**
 * Created by Jouvy on 10-Mar-15.
 * Nyimpen Entity dari Data.id
 */
public class Entity implements Serializable{
    int id;
    int no;
    int kode;
    String nama;
    String jenis;
    String alamat;
    String kelurahan;
    String kecamatan;
    String kota;
    String kodePos;
    String telepon;
    String fax;
    String website;
    String email;
    String humas;

    Entity(int id, int no, int kode, String nama, String jenis, String alamat, String kelurahan,
           String kecamatan, String kota, String kodePos, String telepon, String fax,
           String website, String email, String humas){
        this.id = id;
        this.no = no;
        this.kode = kode;
        this.nama = nama;
        this.jenis = jenis;
        this.alamat = alamat;
        this.kelurahan = kelurahan;
        this.kecamatan = kecamatan;
        this.kota = kota;
        this.kodePos = kodePos;
        this.telepon = telepon;
        this.fax = fax;
        this.website = website;
        this.email = email;
        this.humas = humas;
    }
}
