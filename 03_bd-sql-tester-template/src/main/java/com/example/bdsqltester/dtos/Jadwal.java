package com.example.bdsqltester.dtos;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Jadwal {
    public long id;
    public String hari;
    public String jam;
    public String mataPelajaran;
    public String kelas;

    public Jadwal(long id,String hari, String jam,String mataPelajaran,String kelas)
    {
        this.id =id;
        this.hari = hari;
        this.jam = jam;
        this. mataPelajaran = mataPelajaran;
        this.kelas = kelas;
    }

    public Jadwal(ResultSet rs) throws SQLException {
        this.id = rs.getLong("id");
        this.hari = rs.getString("hari");
        this.jam = rs.getString("jam");
        this.mataPelajaran = rs.getString("mata_pelajaran");
        this.kelas = rs.getString("kelas");
    }
}
