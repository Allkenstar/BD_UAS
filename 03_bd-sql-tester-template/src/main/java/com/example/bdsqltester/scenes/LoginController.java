package com.example.bdsqltester.scenes;

import com.example.bdsqltester.HelloApplication;
import com.example.bdsqltester.datasources.MainDataSource;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.sql.*;

import javafx.scene.Parent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginController {

    @FXML
    private TextField passwordField;

    @FXML
    private ChoiceBox<String> selectRole;

    @FXML
    private TextField usernameField;

    private int getUserIdByUsername(String username) throws SQLException {
        try (Connection c = MainDataSource.getConnection()) {
            PreparedStatement stmt = c.prepareStatement("SELECT id FROM users WHERE username = ?");
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        }
        return -1;
    }

    boolean verifyCredentials(String username, String password, String role) throws SQLException {
        // Call the database to verify the credentials
        // This is insecure as this stores the password in plain text.
        // In a real application, you should hash the password and store it securely.

        // Get a connection to the database
        try (Connection c = MainDataSource.getConnection()) {
            // Create a prepared statement to prevent SQL injection
            PreparedStatement stmt = c.prepareStatement("SELECT * FROM users WHERE username = ? AND role = ?");
            stmt.setString(1, username);
            stmt.setString(2, role.toLowerCase());

            // Execute the query
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // User found, check the password
                String dbPassword = rs.getString("password");

                if (dbPassword.equals(password)) {
                    return true; // Credentials are valid
                }
            }
        }

        // If we reach here, the credentials are invalid
        return false;
    }

    @FXML
    void initialize() {
        selectRole.getItems().addAll("admin", "user", "guru", "wali_kelas");
        selectRole.setValue("user");
    }

    @FXML
    void onLoginClick(ActionEvent event) {
        // Get the username and password from the text fields
        String username = usernameField.getText();
        String password = passwordField.getText();
        String role = selectRole.getValue();

        // Verify the credentials
        try {
            if (verifyCredentials(username, password, role)) {
                HelloApplication app = HelloApplication.getApplicationInstance();
                int userId = getUserIdByUsername(username);

                if (userId == -1) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Login Error");
                    alert.setHeaderText("User Not Found");
                    alert.setContentText("Cannot find user ID in database.");
                    alert.showAndWait();
                    return;
                }

                FXMLLoader loader;
                Parent root;
                Scene scene;

                switch (role) {
                    case "admin":
                        loader = new FXMLLoader(HelloApplication.class.getResource("admin-view.fxml"));
                        root = loader.load();
                        scene = new Scene(root);
                        app.getPrimaryStage().setTitle("Admin View");
                        break;

                    case "user":
                        loader = new FXMLLoader(HelloApplication.class.getResource("siswa-view.fxml"));
                        root = loader.load();
                        SiswaController siswaController = loader.getController();
                        siswaController.setUserId(userId);
                        scene = new Scene(root);
                        app.getPrimaryStage().setTitle("Siswa View");
                        break;

                    case "guru":
                        loader = new FXMLLoader(HelloApplication.class.getResource("guru-view.fxml"));
                        root = loader.load();
                        GuruController guruController = loader.getController();
                        guruController.setUserId(userId); // create setter
                        scene = new Scene(root);
                        app.getPrimaryStage().setTitle("Guru View");
                        break;

                    case "wali_kelas":
                        loader = new FXMLLoader(HelloApplication.class.getResource("wali-view.fxml"));
                        root = loader.load();
                        WaliController waliController = loader.getController();
                        waliController.setUserId(userId); // create setter
                        scene = new Scene(root);
                        app.getPrimaryStage().setTitle("Wali Kelas View");
                        break;

                    default:
                        throw new IllegalArgumentException("Unknown role: " + role);
                }

                app.getPrimaryStage().setScene(scene);

            } else {
                // Show an error message
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Login Failed");
                alert.setHeaderText("Invalid Credentials");
                alert.setContentText("Please check your username and password.");
                alert.showAndWait();
            }
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText("Database Connection Failed");
            alert.setContentText("Could not connect to the database. Please try again later.");
            alert.showAndWait();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
