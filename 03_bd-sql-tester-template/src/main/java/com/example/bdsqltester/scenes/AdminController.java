package com.example.bdsqltester.scenes;

import com.example.bdsqltester.datasources.GradingDataSource;
import com.example.bdsqltester.datasources.MainDataSource;
import com.example.bdsqltester.dtos.Assignment;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.sql.*;
import java.util.ArrayList;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.time.LocalDate;
import java.sql.*;

public class AdminController {

    // Input Data Siswa
    @FXML private TextField nrpField;
    @FXML private TextField namaField;
    @FXML private DatePicker tglLahirPicker;
    @FXML private TextField alamatField;
    @FXML private TextField noHpOrtuField;
    @FXML private PasswordField passwordField;

    // Input Jadwal Kelas
    @FXML private ComboBox<String> kelasCombo;
    @FXML private ComboBox<String> hariCombo;
    @FXML private TextField jamField;
    @FXML private ComboBox<String> guruCombo;
    @FXML private ComboBox<String> mapelCombo;

    // Bagi/Naik Kelas
    @FXML private ComboBox<String> siswaCombo;
    @FXML private ComboBox<String> kelasTujuanCombo;
    @FXML private ComboBox<String> waliKelasCombo;

    @FXML
    public void initialize() {
        hariCombo.getItems().addAll("Senin", "Selasa", "Rabu", "Kamis", "Jumat");

        // TODO: Load data into combo boxes from DB
        loadComboData();
    }

    private void loadComboData() {
        try (Connection conn = MainDataSource.getConnection()) {
            Statement stmt = conn.createStatement();

            ResultSet rsKelas = stmt.executeQuery("SELECT nama_kelas FROM kelas");
            while (rsKelas.next()) {
                kelasCombo.getItems().add(rsKelas.getString(1));
                kelasTujuanCombo.getItems().add(rsKelas.getString(1));
            }

            ResultSet rsGuru = stmt.executeQuery("SELECT nama_guru FROM guru");
            while (rsGuru.next()) {
                guruCombo.getItems().add(rsGuru.getString(1));
                waliKelasCombo.getItems().add(rsGuru.getString(1));
            }

            ResultSet rsMapel = stmt.executeQuery("SELECT nama_mapel FROM mata_pelajaran");
            while (rsMapel.next()) {
                mapelCombo.getItems().add(rsMapel.getString(1));
            }

            ResultSet rsSiswa = stmt.executeQuery("SELECT nama_siswa FROM siswa");
            while (rsSiswa.next()) {
                siswaCombo.getItems().add(rsSiswa.getString(1));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSimpanSiswa() {
        String nrpText = nrpField.getText();
        String nama = namaField.getText();
        LocalDate tglLahir = tglLahirPicker.getValue();
        String alamat = alamatField.getText();
        String noHpOrtu = noHpOrtuField.getText();
        String password = passwordField.getText();

        String sql = "INSERT INTO siswa (nrp, nama_siswa, tgl_lahir, alamat, no_hp_ortu, password) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = MainDataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, Integer.parseInt(nrpText));
            pstmt.setString(2, nama);
            pstmt.setDate(3, Date.valueOf(tglLahir));
            pstmt.setString(4, alamat);
            pstmt.setString(5, noHpOrtu);
            pstmt.setString(6, password);
            pstmt.executeUpdate();

            System.out.println("Data siswa berhasil disimpan.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSimpanJadwal() {
        String kelas = kelasCombo.getValue();
        String hari = hariCombo.getValue();
        String jam = jamField.getText();
        String guru = guruCombo.getValue();
        String mapel = mapelCombo.getValue();

        String sql = "INSERT INTO jadwal_kelas (hari, jam, id_kelas, nip_guru, id_mapel) VALUES (?, ?, (SELECT id_kelas FROM kelas WHERE nama_kelas = ?),(SELECT nip_guru FROM guru WHERE nama_guru = ?),(SELECT id_mapel FROM mata_pelajaran WHERE nama_mapel = ?))";

        try (Connection conn = MainDataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, hari);
            pstmt.setString(2, jam);
            pstmt.setString(3, kelas);
            pstmt.setString(4, guru);
            pstmt.setString(5, mapel);
            pstmt.executeUpdate();

            System.out.println("Jadwal kelas berhasil disimpan.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handlePindahKelas() {
        String siswa = siswaCombo.getValue();
        String kelasTujuan = kelasTujuanCombo.getValue();
        String waliKelas = waliKelasCombo.getValue();

        try (Connection conn = MainDataSource.getConnection()) {
            conn.setAutoCommit(false);

            // Update siswa_kelas
            String insertSK = "INSERT INTO siswa_kelas (nrp, id_kelas) VALUES ((SELECT nrp FROM siswa WHERE nama_siswa = ?), (SELECT id_kelas FROM kelas WHERE nama_kelas = ?))";
            try (PreparedStatement psSk = conn.prepareStatement(insertSK)) {
                psSk.setString(1, siswa);
                psSk.setString(2, kelasTujuan);
                psSk.executeUpdate();
            }

            // Update wali kelas
            String updateWali = "UPDATE kelas SET nip_wali = (SELECT nip_guru FROM guru WHERE nama_guru = ?) WHERE nama_kelas = ?";
            try (PreparedStatement psWk = conn.prepareStatement(updateWali)) {
                psWk.setString(1, waliKelas);
                psWk.setString(2, kelasTujuan);
                psWk.executeUpdate();
            }

            conn.commit();
            System.out.println("Siswa berhasil dipindahkan ke kelas baru dan wali kelas diset.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

