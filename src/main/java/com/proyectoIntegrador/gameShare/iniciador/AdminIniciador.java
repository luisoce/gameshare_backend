package com.proyectoIntegrador.gameShare.iniciador;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class AdminIniciador implements CommandLineRunner {
    @Autowired
    private PasswordEncoder encriptarContrasenia;
    @Autowired
    private DataSource dataSource;

    @Override
    public void run(String... args) throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            // Verificar si el usuario ya existe
            String checkUserSql = "SELECT COUNT(*) FROM usuarios WHERE email = ?";
            try (PreparedStatement checkStatement = connection.prepareStatement(checkUserSql)) {
                checkStatement.setString(1, "admin@admin.cl");
                try (ResultSet rs = checkStatement.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        System.out.println("El usuario admin@admin.cl ya existe.");
                        return;
                    }
                }
            }

            // Insertar el usuario si no existe
            String hashedPassword = encriptarContrasenia.encode("Hola123$");
            String insertUserSql = "INSERT INTO usuarios (nombre, apellido, email, contrasenia, roles_id_rol) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement insertStatement = connection.prepareStatement(insertUserSql)) {
                insertStatement.setString(1, "admin");
                insertStatement.setString(2, "admin");
                insertStatement.setString(3, "admin@admin.cl");
                insertStatement.setString(4, hashedPassword);
                insertStatement.setInt(5, 2);
                insertStatement.executeUpdate();
                System.out.println("Usuario admin@admin.cl creado exitosamente.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
