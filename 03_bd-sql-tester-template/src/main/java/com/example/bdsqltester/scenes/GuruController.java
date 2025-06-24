package com.example.bdsqltester.scenes;

import com.example.bdsqltester.datasources.MainDataSource;
import com.example.bdsqltester.dtos.Jadwal;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GuruController {

    @FXML private TableView<Jadwal> jadwalTable;
    @FXML private TextField siswaIdField;
    @FXML private TextField mapelIdField;
    @FXML private TextField nilaiField;
    @FXML private Button submitButton;
    private int userId;

    @FXML
    public void initialize() {
        loadJadwalGuru(userId);
    }

    public void setUserId(int id) {
        this.userId = id;
    }

    private void loadJadwalGuru(long guruId) {
        ObservableList<Jadwal> data = FXCollections.observableArrayList();
        try (Connection conn = MainDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT j.id, j.hari, j.jam, m.nama as mata_pelajaran, k.nama as kelas " +
                             "FROM jadwal j " +
                             "JOIN mata_pelajaran m ON j.mata_pelajaran_id = m.id " +
                             "JOIN kelas k ON j.kelas_id = k.id " +
                             "WHERE j.guru_id = ?")) {
            stmt.setLong(1, guruId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                data.add(new Jadwal(rs));
            }
            jadwalTable.setItems(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleInputNilai() {
        try (Connection conn = MainDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO nilai_ujian (siswa_id, guru_id, mata_pelajaran_id, nilai) VALUES (?, ?, ?, ?)")) {
            stmt.setLong(1, Long.parseLong(siswaIdField.getText()));
            stmt.setLong(2, 1); // guru_id sementara
            stmt.setLong(3, Long.parseLong(mapelIdField.getText()));
            stmt.setDouble(4, Double.parseDouble(nilaiField.getText()));
            stmt.executeUpdate();
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Nilai berhasil ditambahkan");
            alert.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

