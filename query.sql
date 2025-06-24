
-- 3) Create tables

CREATE TABLE siswa (
  nrp         SERIAL PRIMARY KEY,
  nama_siswa  VARCHAR(100) NOT NULL,
  tgl_lahir   DATE       NOT NULL,
  alamat      TEXT,
  no_hp_ortu  VARCHAR(20),
  password    VARCHAR(100) NOT NULL
);

CREATE TABLE biodata (
  id_bio  SERIAL PRIMARY KEY,
  nrp     INTEGER NOT NULL REFERENCES siswa(nrp),
  content TEXT,
  CONSTRAINT one_bio_per_siswa UNIQUE(nrp)
);


-- 4) Tahun Ajaran
CREATE TABLE tahun_ajaran (
  id_tahun    SERIAL      PRIMARY KEY,
  tahun       VARCHAR(9)  NOT NULL,    -- e.g. '2024/2025'
  semester    VARCHAR(10) NOT NULL     -- e.g. 'Ganjil' or 'Genap'
);

-- 5) Guru
CREATE TABLE guru (
  nip_guru    VARCHAR(20) PRIMARY KEY,
  nama_guru   VARCHAR(100) NOT NULL,
  email       VARCHAR(100) NOT NULL UNIQUE,
  password VARCHAR(100) NOT NULL
);

-- 6) Mata Pelajaran
CREATE TABLE mata_pelajaran (
  id_mapel    SERIAL      PRIMARY KEY,
  nama_mapel  VARCHAR(100) NOT NULL
);

-- 7) Kelas
CREATE TABLE kelas (
  id_kelas    SERIAL      PRIMARY KEY,
  nama_kelas  VARCHAR(50) NOT NULL,    -- e.g. 'X IPA 1'
  start_date  DATE        NOT NULL,
  end_date    DATE        NOT NULL,
  id_tahun    INTEGER     NOT NULL REFERENCES tahun_ajaran(id_tahun),
  nip_wali	  VARCHAR(20) REFERENCES guru(nip_guru)
);

-- 8) Jadwal Kelas
CREATE TABLE jadwal_kelas (
  id_jadwal   SERIAL      PRIMARY KEY,
  hari        VARCHAR(10) NOT NULL,      -- e.g. 'Senin'
  jam         VARCHAR(20) NOT NULL,      -- e.g. '07:00-08:30'
  id_kelas    INTEGER     NOT NULL REFERENCES kelas(id_kelas),
  nip_guru    VARCHAR(20) NOT NULL REFERENCES guru(nip_guru),
  id_mapel    INTEGER     NOT NULL REFERENCES mata_pelajaran(id_mapel)
);

-- 9) Absensi
CREATE TABLE absensi (
  id_absensi  SERIAL      PRIMARY KEY,
  tanggal     DATE        NOT NULL,
  status      VARCHAR(10) NOT NULL,     -- e.g. 'Hadir','Izin','Alpha'
  nrp         INTEGER     NOT NULL REFERENCES siswa(nrp),
  id_jadwal   INTEGER     NOT NULL REFERENCES jadwal_kelas(id_jadwal)
);

-- 10) Nilai Ujian
CREATE TABLE nilai_ujian (
  id_nilai    SERIAL      PRIMARY KEY,
  jenis_ujian VARCHAR(20) NOT NULL,     -- e.g. 'Ulangan 1','UTS','UAS'
  nilai       INTEGER     NOT NULL,
  start_date  DATE        NOT NULL,
  end_date    DATE        NOT NULL,
  nrp         INTEGER     NOT NULL REFERENCES siswa(nrp),
  id_mapel    INTEGER     NOT NULL REFERENCES mata_pelajaran(id_mapel)
);

-- 11) Rapor
CREATE TABLE rapor (
  id_rapor      SERIAL      PRIMARY KEY,
  tanggal_cetak DATE        NOT NULL,
  nrp           INTEGER     NOT NULL REFERENCES siswa(nrp),
  id_tahun      INTEGER     NOT NULL REFERENCES tahun_ajaran(id_tahun)
);

-- 12) (Optional) Relasi Siswa–Kelas (keanggotaan kelas)
CREATE TABLE siswa_kelas (
  id_sk         SERIAL      PRIMARY KEY,
  nrp           INTEGER     NOT NULL REFERENCES siswa(nrp),
  id_kelas      INTEGER     NOT NULL REFERENCES kelas(id_kelas),
  UNIQUE(nrp, id_kelas)
);

CREATE TABLE users (
  id  		 SERIAL       PRIMARY KEY,
  username   VARCHAR(50)  NOT NULL,
  password   VARCHAR(100) NOT NULL,
  role       VARCHAR(20)  NOT NULL
);

INSERT INTO users (username, password, role) VALUES
  ('waka_kelas_001', 'Waka@2025',       'wali_kelas'),
  ('waka_kelas_002', 'Waka@2025b',      'wali_kelas'),
  ('admin',          'Admin#2025',      'admin');

-- 1) Tahun Ajaran
INSERT INTO tahun_ajaran (tahun, semester) VALUES
  ('2024/2025', 'Ganjil'),
  ('2024/2025', 'Genap');

-- 2) Guru
INSERT INTO guru (nip_guru, nama_guru, email, password) VALUES
  ('G001', 'Budi Santoso',   'budi.santoso@sekolah.sch.id', 'Guru@1001'),
  ('G002', 'Siti Aminah',    'siti.aminah@sekolah.sch.id', 'Guru@1002'),
  ('G003', 'Andi Wijaya',    'andi.wijaya@sekolah.sch.id', 'Guru@1003');

-- 3) Mata Pelajaran
INSERT INTO mata_pelajaran (nama_mapel) VALUES
  ('Matematika'),
  ('Bahasa Indonesia'),
  ('Fisika'),
  ('Biologi');

-- 4) Kelas
INSERT INTO kelas (nama_kelas, start_date, end_date, id_tahun, nip_wali) VALUES
  ('X IPA 1', '2024-07-01', '2025-06-30', 1, 'G002'),
  ('X IPS 1', '2024-07-01', '2025-06-30', 1, 'G001');

-- 5) Jadwal Kelas
INSERT INTO jadwal_kelas (hari, jam, id_kelas, nip_guru, id_mapel) VALUES
  ('Senin',    '07:00-08:30', 1, 'G001', 1),  -- Matematika di X IPA 1
  ('Senin',    '08:30-10:00', 1, 'G002', 2),  -- Bahasa Indonesia
  ('Selasa',   '07:00-08:30', 1, 'G003', 3),  -- Fisika
  ('Rabu',     '08:30-10:00', 2, 'G001', 1),  -- Matematika di X IPS 1
  ('Kamis',    '07:00-08:30', 2, 'G002', 2);  -- Bahasa Indonesia

INSERT INTO siswa (nama_siswa, tgl_lahir, alamat, no_hp_ortu, password) VALUES
('Ahmad Fikri',    '2006-03-15', 'Jl. Melati No. 12, Jakarta',      '081234567890', 'Fikri@2006'),
('Bella Sari',     '2005-07-22', 'Jl. Mawar No. 8, Bandung',        '082198765432', 'Sari!2005'),
('Cahya Putri',    '2006-11-05', 'Jl. Kenanga No. 45, Surabaya',     '081398765210', 'Cahya#2006'),
('Dedi Santoso',   '2007-01-30', 'Jl. Anggrek No. 3, Yogyakarta',    '082347891234', 'Dedi$2007'),
('Erika Dewi',     '2005-05-18', 'Jl. Melur No. 77, Semarang',       '081576543210', 'Erika%2005'),
('Fajar Pratama',  '2006-09-12', 'Jl. Cempaka No. 21, Medan',        '082176543987', 'Fajar^2006'),
('Gita Lestari',   '2007-02-27', 'Jl. Bunga No. 9, Makassar',        '081987654321', 'Gita&2007'),
('Hendra Wijaya',  '2006-06-03', 'Jl. Teratai No. 4, Palembang',     '082334556678', 'Hendra*2006'),
('Intan Maharani', '2005-12-17', 'Jl. Dahlia No. 11, Malang',        '081223344556', 'Intan(2005'),
('Joko Susilo',    '2007-04-09', 'Jl. Flamboyan No. 2, Balikpapan',  '082112233445', 'Joko)2007'),
('Kiki Amelia',    '2006-08-20', 'Jl. Soka No. 6, Pontianak',        '081334455667', 'Kiki_2006'),
('Lina Kartika',   '2005-10-25', 'Jl. Angsana No. 18, Palu',         '082445566778', 'Lina+2005'),
('Maman Nurjaman', '2007-03-14', 'Jl. Trembesi No. 5, Denpasar',     '081556677889', 'Maman=2007'),
('Nia Prasetyo',   '2006-01-29', 'Jl. Randu No. 23, Manado',         '082667788990', 'Nia-2006'),
('Oki Rahma',      '2005-11-08', 'Jl. Waru No. 14, Lampung',         '081778899001', 'Oki~2005'),
('Putri Wulandari','2007-07-21', 'Jl. Beringin No. 7, Solo',         '082889900112', 'Putri`2007'),
('Rudi Hartono',   '2006-02-02', 'Jl. Cemara No. 29, Pekanbaru',     '081990011223', 'Rudi|2006'),
('Sari Yuliana',   '2005-09-13', 'Jl. Pinus No. 16, Kupang',         '082101122334', 'Sari\\2005'),
('Tono Prabowo',   '2007-05-06', 'Jl. Randu No. 31, Banjarmasin',    '081211223344', 'Tono/2007'),
('Umi Kholifah',   '2006-12-24', 'Jl. Cemeti No. 10, Aceh',          '082322334455', 'Umi?2006');

INSERT INTO biodata (nrp) VALUES
  (1), (2), (3), (4), (5), (6), (7), (8), (9), (10),
  (11), (12), (13), (14), (15), (16), (17), (18), (19), (20);

-- 8) Absensi
INSERT INTO absensi (tanggal, status, nrp, id_jadwal) VALUES
  ('2025-01-15', 'Hadir', 1, 1),
  ('2025-01-15', 'Izin',  2, 1),
  ('2025-01-15', 'Alpha',  3, 1);

-- 9) Nilai Ujian
INSERT INTO nilai_ujian (jenis_ujian, nilai, start_date, end_date, nrp, id_mapel) VALUES
  ('Ulangan 1', 85, '2024-09-10', '2024-09-10', 1, 1),
  ('Ulangan 1', 78, '2024-09-10', '2024-09-10', 2, 1),
  ('Ulangan 1', 92, '2024-09-10', '2024-09-10', 3, 1),
  ('UTS',       88, '2024-11-15', '2024-11-15', 1, 1),
  ('UTS',       75, '2024-11-15', '2024-11-15', 2, 1),
  ('UTS',       90, '2024-11-15', '2024-11-15', 3, 1);

-- 10) Rapor
INSERT INTO rapor (tanggal_cetak, nrp, id_tahun) VALUES
  ('2024-12-20', 1, 1),
  ('2024-12-20', 2, 1),
  ('2024-12-20', 3, 1);