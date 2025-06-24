package com.example.bdsqltester.scenes;

import com.example.bdsqltester.datasources.MainDataSource;
import com.example.bdsqltester.dtos.Grade;
import com.example.bdsqltester.dtos.Jadwal;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SiswaController {

    @FXML private Label namaLabel;
    @FXML private Label tglLahirLabel;
    @FXML private Label alamatLabel;
    @FXML private Label noHpOrtuLabel;

    @FXML private TableView<Jadwal> jadwalTable;
    @FXML private TableColumn<Jadwal, String> hariColumn;
    @FXML private TableColumn<Jadwal, String> jamColumn;
    @FXML private TableColumn<Jadwal, String> mapelColumn;
    @FXML private TableColumn<Jadwal, String> guruColumn;

    @FXML private TableView<Grade> nilaiTable;
    @FXML private TableColumn<Grade, String> jenisUjianColumn;
    @FXML private TableColumn<Grade, Integer> nilaiColumn;
    @FXML private TableColumn<Grade, String> mapelNilaiColumn;

    private int userId;

    public void setUserId(int id) {
        this.userId = id;
    }

    @FXML
    public void initialize() {
        loadBiodata();
        loadJadwal();
        loadNilai();
    }



//    private int userId;
//
//    public void setUserId(int userId) {
//        this.userId = userId;
//        this.currentNrp = userId;
//        loadBiodata();
//        loadJadwal();
//        loadNilai();
//    }

    private void loadBiodata() {
        String sql = "SELECT nama_siswa, tgl_lahir, alamat, no_hp_ortu FROM siswa WHERE nrp = ?";
        try (Connection conn = MainDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                namaLabel.setText(rs.getString("nama_siswa"));
                tglLahirLabel.setText(rs.getString("tgl_lahir"));
                alamatLabel.setText(rs.getString("alamat"));
                noHpOrtuLabel.setText(rs.getString("no_hp_ortu"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadJadwal() {
        ObservableList<Jadwal> list = FXCollections.observableArrayList();
        String sql = """
                SELECT jk.hari, jk.jam, mp.nama_mapel, g.nama_guru
                FROM siswa_kelas sk
                JOIN kelas k ON sk.id_kelas = k.id_kelas
                JOIN jadwal_kelas jk ON jk.id_kelas = k.id_kelas
                JOIN mata_pelajaran mp ON jk.id_mapel = mp.id_mapel
                JOIN guru g ON jk.nip_guru = g.nip_guru
                WHERE sk.nrp = ?
                """;
        try (Connection conn = MainDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Jadwal jadwal = new Jadwal(rs);
                list.add(jadwal);
            }

            jadwalTable.setItems(list);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadNilai() {
        ObservableList<Grade> list = FXCollections.observableArrayList();
        String sql = """
                SELECT nu.jenis_ujian, nu.nilai, mp.nama_mapel
                FROM nilai_ujian nu
                JOIN mata_pelajaran mp ON nu.id_mapel = mp.id_mapel
                WHERE nu.nrp = ?
                """;
        try (Connection conn = MainDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Grade grade = new Grade(rs);
                list.add(grade);
            }
            nilaiTable.setItems(list);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
