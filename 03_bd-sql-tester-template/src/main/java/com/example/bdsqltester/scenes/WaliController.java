package com.example.bdsqltester.scenes;

import com.example.bdsqltester.datasources.MainDataSource;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.*;

public class WaliController {

    @FXML private TextField siswaIdField;
    @FXML private TableView<String> nilaiTable;
    private int userId;

    @FXML
    private void handleCetakRapor() {
        ObservableList<String> nilaiData = FXCollections.observableArrayList();
        try (Connection conn = MainDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT m.nama as mata_pelajaran, n.nilai " +
                             "FROM nilai_ujian n " +
                             "JOIN mata_pelajaran m ON n.mata_pelajaran_id = m.id " +
                             "WHERE n.siswa_id = ?")) {
            stmt.setLong(1, Long.parseLong(siswaIdField.getText()));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String line = rs.getString("mata_pelajaran") + " - " + rs.getDouble("nilai");
                nilaiData.add(line);
            }
            nilaiTable.setItems(nilaiData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void setUserId(int id) {
        this.userId = id;
    }
}
