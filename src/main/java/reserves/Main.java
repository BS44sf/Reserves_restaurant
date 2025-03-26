package reserves;

import java.sql.*;
import java.util.Scanner;
import java.text.SimpleDateFormat;
import java.text.ParseException;


public class Main {
    static final String DB_URL = "jdbc:mysql://localhost/gestio reserves";
    static final String USER = "root";
    static final String PASS = "";
    public static void main(String[] args) throws SQLException, ParseException {
        Main program = new Main();
        program.inici();
    }

    public void inici() throws SQLException, ParseException {
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
            client(dni);// Redirigir al apartado de clientes
        } else {
            String consultaAdmins = "SELECT * FROM admins WHERE dni = ?";
            PreparedStatement sentenciaAdmins = conexion.prepareStatement(consultaAdmins);
            sentenciaAdmins.setString(1, dni);
            ResultSet resultadoAdmins = sentenciaAdmins.executeQuery();

            if (resultadoAdmins.next()) {
                admin();// Redirigir al apartado de administradores
            } else {
                System.out.println("DNI no encontrado. ¿Desea registrarse? S/N");
                String opcion = lector.nextLine();
                if (opcion.equals("S")) {
                    System.out.println("Introduce tu DNI: ");
                    String dniRc = lector.nextLine();
                    System.out.println("Introduce tu nombre: ");
                    String nomRc = lector.nextLine();
                    System.out.println("Introduce tu email: ");
                    String emailRc = lector.nextLine();
                    System.out.println("Introduce tu telefono: ");
                    String telefonoRc = lector.nextLine();
                    String sqlRC = "INSERT INTO clients (dni, nom, email, telefon) VALUES (?, ?, ?, ?)";
                    PreparedStatement sentenciaCliente = conexion.prepareStatement(sqlRC);
                    sentenciaCliente.setString(1, dniRc);
                    sentenciaCliente.setString(2, nomRc);
                    sentenciaCliente.setString(3, emailRc);
                    sentenciaCliente.setString(4, telefonoRc);
                    sentenciaCliente.executeUpdate();

                    System.out.println("Cliente registrado exitosamente.");
                    sentenciaCliente.close();
                }
            }
        }
    }

    public void client(String dni) throws SQLException, ParseException {
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
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
                    System.out.println("Aqui puedes ver las mesas disponibles.");
                    String taulasDisp = "SELECT * FROM taules WHERE ocupada = false";
                    PreparedStatement stmt17 = conexion.prepareStatement(taulasDisp);
                    ResultSet resultado = stmt17.executeQuery();
                    if (!resultado.isBeforeFirst()) {
                        System.out.println("No se encontraron mesas disponibles con esa capacidad.");
                    } else {
                        while (resultado.next()) {
                            System.out.print("Número de mesa: " + resultado.getString("id_taula"));
                            System.out.println(" Capacidad: " + resultado.getInt("capacitat"));
                        }
                    }
                    System.out.println("Por favor ingrese el numero de la mesa que desea reservar.");
                    Integer numM = lector.nextInt();
                    lector.nextLine();

                    System.out.println("Introduzca la fecha en la que quiere reservar (YYYY-MM-DD):");
                    java.util.Date fechaUtil = dateFormat.parse(lector.nextLine());
                    java.sql.Date fechaSQL = new java.sql.Date(fechaUtil.getTime());

                    System.out.println("Introduzca la hora en la que quiere reservar (HH:mm:ss):");
                    java.util.Date horaUtil = timeFormat.parse(lector.nextLine());
                    java.sql.Time horaSQL = new java.sql.Time(horaUtil.getTime());

                    String realitzarR = "INSERT INTO reserves(dni, id_taula, fecha, hora) VALUES (?, ?, ?, ?)";
                    PreparedStatement stmt2 = conexion.prepareStatement(realitzarR);
                    stmt2.setString(1, dni);
                    stmt2.setInt(2, numM);
                    stmt2.setDate(3, fechaSQL);
                    stmt2.setTime(4, horaSQL);

                    int filasAfectadas = stmt2.executeUpdate();

                    if (filasAfectadas > 0) {
                        System.out.println("Reserva realizada con éxito.");
                    } else {
                        System.out.println("Error al realizar la reserva.");
                    }
                    String ocuparT = "UPDATE taules SET ocupada = true WHERE id_taula = ?";
                    PreparedStatement stmt3 = conexion.prepareStatement(ocuparT);
                    stmt3.setInt(1, numM);
                    stmt3.executeUpdate();
                    stmt3.close();
                break;
                case "2":


            }
        }while(!opcio.equals("4"));
    }

    public void admin() throws SQLException {
        Scanner lector = new Scanner(System.in);

        String opcio2;
            System.out.println("Benvingut Admin, seleccioneu una de les opcions:");
            System.out.println();


    }
}
