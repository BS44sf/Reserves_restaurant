package reserves;

import java.sql.*;
import java.util.Scanner;

public class Main {
    static final String DB_URL = "jdbc:mysql://localhost/gestio reserves";
    static final String USER = "root";
    static final String PASS = "";
    public static void main(String[] args) throws SQLException {
        Main program = new Main();
        program.inici();
    }

    public void inici() throws SQLException {
        Scanner lector = new Scanner(System.in);

        Connection conexion = DriverManager.getConnection(DB_URL, USER, PASS);
        Scanner scanner = new Scanner(System.in);
        System.out.print("Introduce tu DNI: ");
        String dni = scanner.nextLine();

        String consultaClientes = "SELECT * FROM clients WHERE dni = ?";
        PreparedStatement sentenciaClientes = conexion.prepareStatement(consultaClientes);
        sentenciaClientes.setString(1, dni);
        ResultSet resultadoClientes = sentenciaClientes.executeQuery();

        if (resultadoClientes.next()) {
            System.out.println("Bienvenido, cliente.");
            client();
            // Redirigir al apartado de clientes
        } else {
            String consultaAdmins = "SELECT * FROM admins WHERE dni = ?";
            PreparedStatement sentenciaAdmins = conexion.prepareStatement(consultaAdmins);
            sentenciaAdmins.setString(1, dni);
            ResultSet resultadoAdmins = sentenciaAdmins.executeQuery();

            if (resultadoAdmins.next()) {
                System.out.println("Bienvenido, administrador.");
                admin();
                // Redirigir al apartado de administradores
            } else {
                System.out.println("DNI no encontrado.");
            }
        }
    }

    public void client() throws SQLException {
        Scanner lector = new Scanner(System.in);

    }

    public void admin() throws SQLException {
        Scanner lector = new Scanner(System.in);
    }
}
