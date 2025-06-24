package com.example.bdsqltester.scenes;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.sql.*;
import com.example.bdsqltester.datasources.MainDataSource;

public class SiswaController {

    // Biodata Fields
    @FXML private TextField nrpLable;
    @FXML private TextField namaLable;
    @FXML private TextField tglLable;
    @FXML private TextField almtLable;
    @FXML private TextField noLable;

    // Jadwal Table
    @FXML private TableView<JadwalItem> jadwalTab;
    @FXML private TableColumn<JadwalItem, Integer> idJadwalColumn;
    @FXML private TableColumn<JadwalItem, String> hariColumn;
    @FXML private TableColumn<JadwalItem, String> jamColumn;
    @FXML private TableColumn<JadwalItem, String> kelasColumn;
    @FXML private TableColumn<JadwalItem, String> guruColumn;
    @FXML private TableColumn<JadwalItem, String> semesterColumn;
    @FXML private TableColumn<JadwalItem, String> tahunColumn;
    @FXML private TableColumn<JadwalItem, String> mapelColumn;

    // Nilai Table
    @FXML private TableView<NilaiItem> nilaiTab;
    @FXML private TableColumn<NilaiItem, Integer> idNilaiColumn;
    @FXML private TableColumn<NilaiItem, String> tanggalColumn;
    @FXML private TableColumn<NilaiItem, String> ujianColumn;
    @FXML private TableColumn<NilaiItem, Integer> nilaiColumn;
    @FXML private TableColumn<NilaiItem, String> mapelNilaiColumn;

    private int userId;

    public void setUserId(int userId) {
        this.userId = userId;
        setupColumns();
        loadBiodata();
        loadJadwal();
        loadNilai();
    }

    private void setupColumns() {
        idJadwalColumn.setCellValueFactory(new PropertyValueFactory<>("idJadwal"));
        hariColumn.setCellValueFactory(new PropertyValueFactory<>("hari"));
        jamColumn.setCellValueFactory(new PropertyValueFactory<>("jam"));
        kelasColumn.setCellValueFactory(new PropertyValueFactory<>("kelas"));
        guruColumn.setCellValueFactory(new PropertyValueFactory<>("guru"));
        semesterColumn.setCellValueFactory(new PropertyValueFactory<>("semester"));
        tahunColumn.setCellValueFactory(new PropertyValueFactory<>("tahun"));
        mapelColumn.setCellValueFactory(new PropertyValueFactory<>("mapel"));

        idNilaiColumn.setCellValueFactory(new PropertyValueFactory<>("idNilai"));
        tanggalColumn.setCellValueFactory(new PropertyValueFactory<>("tanggal"));
        ujianColumn.setCellValueFactory(new PropertyValueFactory<>("ujian"));
        nilaiColumn.setCellValueFactory(new PropertyValueFactory<>("nilai"));
        mapelNilaiColumn.setCellValueFactory(new PropertyValueFactory<>("mapel"));
    }

    private void loadBiodata() {
        String query = """
            SELECT s.nrp, s.nama_siswa, s.tgl_lahir, s.alamat, s.no_hp_ortu
            FROM siswa s
            WHERE s.nrp = ?
        """;
        try (Connection conn = MainDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                nrpLable.setText(String.valueOf(rs.getInt("nrp")));
                namaLable.setText(rs.getString("nama_siswa"));
                tglLable.setText(rs.getDate("tgl_lahir").toString());
                almtLable.setText(rs.getString("alamat"));
                noLable.setText(rs.getString("no_hp_ortu"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadJadwal() {
        ObservableList<JadwalItem> data = FXCollections.observableArrayList();
        String query = """
            SELECT jk.id_jadwal, jk.hari, jk.jam, k.nama_kelas, g.nama_guru,
                   ta.semester, ta.tahun, m.nama_mapel
            FROM siswa_kelas sk
            JOIN kelas k ON sk.id_kelas = k.id_kelas
            JOIN tahun_ajaran ta ON k.id_tahun = ta.id_tahun
            JOIN jadwal_kelas jk ON jk.id_kelas = k.id_kelas
            JOIN guru g ON jk.nip_guru = g.nip_guru
            JOIN mata_pelajaran m ON jk.id_mapel = m.id_mapel
            WHERE sk.nrp = ?
        """;
        try (Connection conn = MainDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                data.add(new JadwalItem(
                        rs.getInt("id_jadwal"),
                        rs.getString("hari"),
                        rs.getString("jam"),
                        rs.getString("nama_kelas"),
                        rs.getString("nama_guru"),
                        rs.getString("semester"),
                        rs.getString("tahun"),
                        rs.getString("nama_mapel")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        jadwalTab.setItems(data);
    }

    private void loadNilai() {
        ObservableList<NilaiItem> data = FXCollections.observableArrayList();
        String query = """
            SELECT n.id_nilai, n.start_date, n.jenis_ujian, n.nilai, m.nama_mapel
            FROM nilai_ujian n
            JOIN mata_pelajaran m ON n.id_mapel = m.id_mapel
            WHERE n.nrp = ?
        """;
        try (Connection conn = MainDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                data.add(new NilaiItem(
                        rs.getInt("id_nilai"),
                        rs.getDate("start_date").toString(),
                        rs.getString("jenis_ujian"),
                        rs.getInt("nilai"),
                        rs.getString("nama_mapel")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        nilaiTab.setItems(data);
    }

    // DTO untuk Jadwal
    public static class JadwalItem {
        private final int idJadwal;
        private final String hari, jam, kelas, guru, semester, tahun, mapel;

        public JadwalItem(int idJadwal, String hari, String jam, String kelas,
                          String guru, String semester, String tahun, String mapel) {
            this.idJadwal = idJadwal;
            this.hari = hari;
            this.jam = jam;
            this.kelas = kelas;
            this.guru = guru;
            this.semester = semester;
            this.tahun = tahun;
            this.mapel = mapel;
        }

        public int getIdJadwal() { return idJadwal; }
        public String getHari() { return hari; }
        public String getJam() { return jam; }
        public String getKelas() { return kelas; }
        public String getGuru() { return guru; }
        public String getSemester() { return semester; }
        public String getTahun() { return tahun; }
        public String getMapel() { return mapel; }
    }

    // DTO untuk Nilai
    public static class NilaiItem {
        private final int idNilai;
        private final String tanggal, ujian, mapel;
        private final int nilai;

        public NilaiItem(int idNilai, String tanggal, String ujian, int nilai, String mapel) {
            this.idNilai = idNilai;
            this.tanggal = tanggal;
            this.ujian = ujian;
            this.nilai = nilai;
            this.mapel = mapel;
        }

        public int getIdNilai() { return idNilai; }
        public String getTanggal() { return tanggal; }
        public String getUjian() { return ujian; }
        public int getNilai() { return nilai; }
        public String getMapel() { return mapel; }
    }
}
