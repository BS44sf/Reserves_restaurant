package reserves;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class Main {
    static final String DB_URL = "jdbc:mysql://localhost/gestio reserves";
    static final String USER = "root";
    static final String PASS = "";
    public static void main(String[] args) {
        Scanner lector = new Scanner(System.in);
        try {
            // Cargar el driver JDBC (no es necesario desde JDBC 4.0)
            // Class.forName("com.mysql.cj.jdbc.Driver");

            // Establecer la conexión
            Connection conexion = DriverManager.getConnection(DB_URL, USER, PASS);

            // Verificar si la conexión es exitosa
            if (conexion != null) {
                System.out.println("Conexión exitosa a la base de datos.");
                conexion.close(); // Cerrar la conexión
            }

        } catch (SQLException e) {
            System.err.println("Error al conectar a la base de datos: " + e.getMessage());
        }

    }
}