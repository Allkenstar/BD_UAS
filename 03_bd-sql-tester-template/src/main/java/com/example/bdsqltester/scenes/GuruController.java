package com.example.bdsqltester.scenes;

import com.example.bdsqltester.datasources.MainDataSource;
import com.example.bdsqltester.dtos.Jadwal;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.*;
import java.time.LocalDate;

public class GuruController {

    @FXML private TableView<Jadwal> tableJadwal;
    @FXML private TableColumn<Jadwal, String> colHari;
    @FXML private TableColumn<Jadwal, String> colJam;
    @FXML private TableColumn<Jadwal, String> colKelas;
    @FXML private TableColumn<Jadwal, String> colMapel;

    @FXML private TextField fieldNRP;
    @FXML private ComboBox<String> comboJenisUjian;
    @FXML private TextField fieldNilai;
    @FXML private DatePicker pickerStartDate;
    @FXML private DatePicker pickerEndDate;
    @FXML private Button btnSubmitNilai;

    private int userId;   // NIP guru
    private String nameId;   // Nama guru



    public void setUserId(int userId) {
        this.userId = userId;
        loadJadwalKelas();
    }

    public void setNameId(String nameId) {
        this.nameId = nameId;
        // kamu bisa pakai nanti jika ingin tampilkan nama guru
    }


    @FXML
    private void initialize() {
        comboJenisUjian.setItems(FXCollections.observableArrayList("Ulangan 1", "UTS", "UAS"));

        colHari.setCellValueFactory(new PropertyValueFactory<>("hari"));
        colJam.setCellValueFactory(new PropertyValueFactory<>("jam"));
        colKelas.setCellValueFactory(new PropertyValueFactory<>("kelas"));
        colMapel.setCellValueFactory(new PropertyValueFactory<>("mataPelajaran"));
    }

    private void loadJadwalKelas() {
        ObservableList<Jadwal> jadwalList = FXCollections.observableArrayList();
        String query = """
            SELECT jk.id_jadwal, jk.hari, jk.jam, k.nama_kelas, m.nama_mapel
            FROM jadwal_kelas jk
            JOIN kelas k ON jk.id_kelas = k.id_kelas
            JOIN mata_pelajaran m ON jk.id_mapel = m.id_mapel
            WHERE jk.nip_guru = ?
        """;

        try (Connection conn = MainDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, nameId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Jadwal jadwal = new Jadwal(
                        rs.getLong("id_jadwal"),
                        rs.getString("hari"),
                        rs.getString("jam"),
                        rs.getString("nama_mapel"),
                        rs.getString("nama_kelas")
                );
                jadwalList.add(jadwal);
            }

            tableJadwal.setItems(jadwalList);

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Gagal memuat jadwal.");
        }
    }

    @FXML
    private void handleSubmitNilai() {
        try {
            int nrp = Integer.parseInt(fieldNRP.getText());
            String jenisUjian = comboJenisUjian.getValue();
            int nilai = Integer.parseInt(fieldNilai.getText());
            LocalDate startDate = pickerStartDate.getValue();
            LocalDate endDate = pickerEndDate.getValue();

            if (jenisUjian == null || startDate == null || endDate == null) {
                showAlert("Pastikan semua data sudah diisi dengan lengkap.");
                return;
            }

            Jadwal selected = tableJadwal.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert("Silakan pilih jadwal terlebih dahulu.");
                return;
            }

            String mapel = colMapel.getCellObservableValue(selected).getValue();


            String getIdMapelQuery = "SELECT id_mapel FROM mata_pelajaran WHERE nama_mapel = ?";
            String insertNilaiQuery = """
                INSERT INTO nilai_ujian (jenis_ujian, nilai, start_date, end_date, nrp, id_mapel)
                VALUES (?, ?, ?, ?, ?, ?)
            """;

            try (Connection conn = MainDataSource.getConnection();
                 PreparedStatement getMapelStmt = conn.prepareStatement(getIdMapelQuery);
                 PreparedStatement insertStmt = conn.prepareStatement(insertNilaiQuery)) {

                getMapelStmt.setString(1, mapel);
                ResultSet rs = getMapelStmt.executeQuery();

                if (!rs.next()) {
                    showAlert("Mapel tidak ditemukan dalam database.");
                    return;
                }

                int idMapel = rs.getInt("id_mapel");

                insertStmt.setString(1, jenisUjian);
                insertStmt.setInt(2, nilai);
                insertStmt.setDate(3, Date.valueOf(startDate));
                insertStmt.setDate(4, Date.valueOf(endDate));
                insertStmt.setInt(5, nrp);
                insertStmt.setInt(6, idMapel);

                insertStmt.executeUpdate();
                showAlert("Nilai berhasil disimpan.");
                resetForm();

            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Gagal menyimpan nilai ke database.");
            }

        } catch (NumberFormatException e) {
            showAlert("NRP dan Nilai harus berupa angka.");
        }
    }

    private void resetForm() {
        fieldNRP.clear();
        comboJenisUjian.getSelectionModel().clearSelection();
        fieldNilai.clear();
        pickerStartDate.setValue(null);
        pickerEndDate.setValue(null);
        tableJadwal.getSelectionModel().clearSelection();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Informasi");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
