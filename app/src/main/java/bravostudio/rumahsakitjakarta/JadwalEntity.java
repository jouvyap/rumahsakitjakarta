package bravostudio.rumahsakitjakarta;

/**
 * Created by Jouvy on 20-Mar-15.
 * Nyimpen entitiy buat jadwal dokter
 */
public class JadwalEntity {
    String nama;
    String senin;
    String selasa;
    String rabu;
    String kamis;
    String jumat;
    String sabtu;

    JadwalEntity(String nama, String senin, String selasa, String rabu, String kamis,
                 String jumat, String sabtu) {
        this.nama = nama;
        this.senin = senin;
        this.selasa = selasa;
        this.rabu = rabu;
        this.kamis = kamis;
        this.jumat = jumat;
        this.sabtu = sabtu;
    }

}
