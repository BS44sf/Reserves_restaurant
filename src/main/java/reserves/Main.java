package reserves;

import java.sql.*;
import java.util.Scanner;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.time.LocalDate;


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
                admin(dni);// Redirigir al apartado de administradores
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
            System.out.println("3. Sortir.");
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
                    conexion.close();
                break;
                case "2":
                    Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
                    System.out.println("Aqui tienes el Menu. ");
                    String mostrarM = "SELECT * FROM menu WHERE quantitat_disponible >= 1";
                    PreparedStatement stmt4 = conn.prepareStatement(mostrarM);
                    ResultSet resultadoM = stmt4.executeQuery();
                    while (resultadoM.next()) {
                        System.out.print("Número del plat: " + resultadoM.getInt("id_menu"));
                        System.out.print(", Nom del plat: " + resultadoM.getString("nom_plat"));
                        System.out.print(", Descripcio: " + resultadoM.getString("descripcio"));
                        System.out.print(", Preu: " + resultadoM.getBigDecimal("preu"));
                        System.out.println(", Quantitat Disponible: " + resultadoM.getInt("quantitat_disponible"));
                    }
                    System.out.println("¿Que deseas pedir? Introduce el numero del plato.");
                    Integer numP = lector.nextInt();
                    lector.nextLine();
                    System.out.println("Introduce la cantidad a pedir. ");
                    Integer quanP = lector.nextInt();
                    String hacerP = "INSERT INTO comandes(dni, fecha) VALUES (?, ?)";
                    PreparedStatement stmt5 = conn.prepareStatement(hacerP, Statement.RETURN_GENERATED_KEYS);
                    stmt5.setString(1, dni);
                    LocalDate localDate = LocalDate.now();
                    Date fechaActual = Date.valueOf(localDate);
                    stmt5.setDate(2, fechaActual);
                    stmt5.executeUpdate();

                    ResultSet generatedKeys = stmt5.getGeneratedKeys();
                    int id_comanda = 0;
                    if (generatedKeys.next()) {
                        id_comanda = generatedKeys.getInt(1);
                    }

                    String aDP = "INSERT INTO detalls_comandes(id_comanda, id_menu, quantitat) VALUES (?, ?, ?)";
                    PreparedStatement stmt6 = conn.prepareStatement(aDP);
                    stmt6.setInt(1, id_comanda);
                    stmt6.setInt(2, numP);
                    stmt6.setInt(3, quanP);
                    int fAc = stmt6.executeUpdate();

                    if (fAc > 0) {
                        System.out.println("Pedido realizado con éxito.");
                    } else {
                        System.out.println("Error al realizar la reserva.");
                    }
                    String actQm= "UPDATE menu set quantitat_disponible = quantitat_disponible - ? WHERE id_menu = ?";
                    PreparedStatement stmt7 = conn.prepareStatement(actQm);
                    stmt7.setInt(1, quanP);
                    stmt7.setInt(2, numP);
                    stmt7.executeUpdate();
                    stmt7.close();
                    conn.close();
                break;
                case "3":
                    System.out.println("Sortint...");
                    break;
                default:
                    System.out.println("Error no has seleccionado ninguna opcion correcta.");
                    break;
            }
        }while(!opcio.equals("3"));
    }

    public void admin(String dni) throws SQLException {
        Scanner lector = new Scanner(System.in);
        String opcio2;
        do {
            System.out.println("Benvingut Admin, seleccioneu una de les opcions:");
            System.out.println("1. Ver cantidad de platos restantes. ");
            System.out.println("2. Ver mesas disponibles. ");
            System.out.println("3. Ver estado de entrega de los pedidos. ");
            System.out.println("4. Cambiar disponibilidad de las mesas. ");
            System.out.println("5. Salir.");
            opcio2 = lector.nextLine();

            switch (opcio2) {
                case "1":
                    System.out.println();
                    System.out.println("Cantidad de platos restantes. ");
                    Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
                    String plDisp = "SELECT nom_plat, quantitat_disponible FROM menu";
                    PreparedStatement stmt1 = conn.prepareStatement(plDisp);
                    ResultSet resultadoM = stmt1.executeQuery();
                    while (resultadoM.next()) {
                        System.out.print("Nombre del plato: " + resultadoM.getString("nom_plat"));
                        System.out.println(", Quantitat Disponible : " + resultadoM.getInt("quantitat_disponible"));
                    }
                    System.out.println();
                    stmt1.close();
                    conn.close();
                break;
                case "2":
                    System.out.println();
                    System.out.println("Mesas disponibles. ");
                    Connection conn2 = DriverManager.getConnection(DB_URL, USER, PASS);
                    String mesDisp = "SELECT * FROM taules WHERE ocupada = false";
                    PreparedStatement stmt3 = conn2.prepareStatement(mesDisp);
                    ResultSet resultadoM2 = stmt3.executeQuery();
                    while (resultadoM2.next()) {
                        System.out.print(" Numero de taula: " + resultadoM2.getInt("id_taula"));
                        System.out.println(", capacitat: " + resultadoM2.getInt("capacitat"));
                    }
                    System.out.println();
                    stmt3.close();
                    conn2.close();
                break;
                case "3":
                    System.out.println();
                    System.out.println("Estado de entrega de los pedidos. ");
                    Connection conn3 = DriverManager.getConnection(DB_URL, USER, PASS);
                    String estatP = "SELECT * FROM comandes";
                    PreparedStatement stmt4 = conn3.prepareStatement(estatP);
                    ResultSet resultadoM3 = stmt4.executeQuery();
                    while (resultadoM3.next()) {
                        System.out.print(" Numero del pedido: " + resultadoM3.getInt("id_comanda"));
                        System.out.print(", DNI Cliente: " + resultadoM3.getString("dni"));
                        System.out.print(", Fecha: " + resultadoM3.getDate("fecha"));
                        System.out.println(", Hora: " + resultadoM3.getTime("hora"));
                    }
                    System.out.println();
                    stmt4.close();
                    conn3.close();
                break;
                case "4":


            }
        }while(!opcio2.equals("5"));




    }
}
