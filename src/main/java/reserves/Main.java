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
            client(dni);
            // Redirigir al apartado de clientes
        } else {
            String consultaAdmins = "SELECT * FROM admins WHERE dni = ?";
            PreparedStatement sentenciaAdmins = conexion.prepareStatement(consultaAdmins);
            sentenciaAdmins.setString(1, dni);
            ResultSet resultadoAdmins = sentenciaAdmins.executeQuery();

            if (resultadoAdmins.next()) {
                admin();
                // Redirigir al apartado de administradores
            } else {
                System.out.println("DNI no encontrado. ¿Desea registrarse?.");
            }
        }
    }

    public void client(String dni) throws SQLException {
        Scanner lector = new Scanner(System.in);

        String opcio;
        do {
            System.out.println("Benvingut, seleccioneu una de les opcions:");
            System.out.println("1. Realitzar una reserva.");
            System.out.println("2. Realitzar una comanda.");
            System.out.println("3. Veure el menú.");
            System.out.println("4. Sortir.");
            opcio = lector.nextLine();

            switch (opcio) {
                case "1":
                    Connection conexion = DriverManager.getConnection(DB_URL, USER, PASS);
                    System.out.println("Aqui puedes ver las mesas disponibles.");
                    String taulasDisp = "SELECT * FROM taules WHERE ocupada = false";
                    PreparedStatement stmt17 = conexion.prepareStatement(taulasDisp);
                    ResultSet result = stmt17.executeQuery();
                    if (result.next()) {
                        System.out.print(" Número de taula: " + result.getInt("id_taula"));
                        System.out.println(" Capacitat: " + result.getInt("capacitat"));
                    }


            }
        }while(!opcio.equals("4"));
    }

    public void admin() throws SQLException {
        Scanner lector = new Scanner(System.in);
    }
}
