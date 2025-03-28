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
        System.out.print("Introdueix el teu DNI: ");
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
                System.out.println("DNI no trobat. Voleu registrar-vos? S/N");
                String opcion = lector.nextLine().toLowerCase();
                if (opcion.equals("S")) {
                    System.out.println("Introdueix el teu DNI: ");
                    String dniRc = lector.nextLine();
                    System.out.println("Introdueix el teu nom: ");
                    String nomRc = lector.nextLine();
                    System.out.println("Introdueix el teu email: ");
                    String emailRc = lector.nextLine();
                    System.out.println("Introdueix el teu telèfon: ");
                    String telefonoRc = lector.nextLine();
                    String sqlRC = "INSERT INTO clients (dni, nom, email, telefon) VALUES (?, ?, ?, ?)";
                    PreparedStatement sentenciaCliente = conexion.prepareStatement(sqlRC);
                    sentenciaCliente.setString(1, dniRc);
                    sentenciaCliente.setString(2, nomRc);
                    sentenciaCliente.setString(3, emailRc);
                    sentenciaCliente.setString(4, telefonoRc);
                    sentenciaCliente.executeUpdate();

                    System.out.println("Client registrat amb èxit.");
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
            System.out.println("3. Cancel·lar una reserva. ");
            System.out.println("4. Sortir.");
            opcio = lector.nextLine();

            switch (opcio) {

                case "1":
                    Connection conexion = DriverManager.getConnection(DB_URL, USER, PASS);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
                    System.out.println("Aquí podeu veure les taules disponibles.");
                    String taulasDisp = "SELECT * FROM taules WHERE ocupada = false";
                    PreparedStatement stmt17 = conexion.prepareStatement(taulasDisp);
                    ResultSet resultado = stmt17.executeQuery();
                    if (!resultado.isBeforeFirst()) {
                        System.out.println("No es van trobar taules disponibles amb aquesta capacitat.");
                    } else {
                        while (resultado.next()) {
                            System.out.print("Número de taula: " + resultado.getString("id_taula"));
                            System.out.println(" Capacitat: " + resultado.getInt("capacitat"));
                        }
                    }
                    System.out.println("Si us plau introduïu el número de la taula que voleu reservar.");
                    Integer numM = lector.nextInt();
                    lector.nextLine();

                    System.out.println("Introduïu la data en què voleu reservar (YYYY-MM-DD):");
                    java.util.Date fechaUtil = dateFormat.parse(lector.nextLine());
                    java.sql.Date fechaSQL = new java.sql.Date(fechaUtil.getTime());

                    System.out.println("Introduïu l'hora en què voleu reservar (HH:mm:ss):");
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
                        System.out.println("Reserva feta amb èxit.");
                    } else {
                        System.out.println("Error en fer la reserva.");
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
                    System.out.println("Aquí tens el Menu. ");
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
                    System.out.println("Què vols demanar? Introdueix el número del plat.");
                    Integer numP = lector.nextInt();
                    lector.nextLine();
                    System.out.println("Introduïu la quantitat a demanar. ");
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
                        System.out.println("Comanda realitzada amb èxit.");
                    } else {
                        System.out.println("Error en fer la reserva.");
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
                    Connection conexion5 = DriverManager.getConnection(DB_URL, USER, PASS);
                    System.out.println("Vols eliminar la reserva?");
                    String respuesta = lector.nextLine();
                    if (respuesta.equalsIgnoreCase("si")){
                        String consSql = "SELECT * FROM reservas WHERE dni = ?";
                        PreparedStatement pstmt5 = conexion5.prepareStatement(consSql);
                        pstmt5.setString(1, dni);
                        ResultSet rs = pstmt5.executeQuery();
                        while (rs.next()) {
                            System.out.print("Id reserva: " + rs.getInt("id"));
                            System.out.print(", DNI: " + rs.getString("dni"));
                            System.out.print(", Numero de taula: " + rs.getInt("id_taula"));
                            System.out.print(", Data: " + rs.getDate("fecha"));
                            System.out.println(", Hora: " + rs.getTime("horas"));
                        }
                        String deleteRs = "DELETE FROM reservas WHERE dni = ?";
                        PreparedStatement pstmt6 = conexion5.prepareStatement(deleteRs);
                        pstmt6.setString(1, dni);
                        pstmt6.executeUpdate();
                        System.out.println("Reserva eliminada.");
                        pstmt6.close();
                        conexion5.close();
                    }
                break;

                case "4":
                    System.out.println("Sortint...");
                    break;
                default:
                    System.out.println("Error no has seleccionat cap opció correcta.");
                    break;
            }
        }while(!opcio.equals("4"));
    }

    public void admin(String dni) throws SQLException {
        Scanner lector = new Scanner(System.in);
        String opcio2;
        do {
            System.out.println();
            System.out.println("Benvingut Admin, seleccioneu una de les opcions:");
            System.out.println("1. Vegeu quantitat de plats restants. ");
            System.out.println("2. Veure taules disponibles.");
            System.out.println("3. Veure estat de lliurament de les comandes. ");
            System.out.println("4. Canviar disponibilitat de les taules. ");
            System.out.println("5. Cancel·lar Reserva. ");
            System.out.println("6. Gestió menú i productes. ");
            System.out.println("7. Sortir.");
            opcio2 = lector.nextLine();

            switch (opcio2) {

                case "1":
                    System.out.println();
                    System.out.println("Quantitat de plats restants. ");
                    Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
                    String plDisp = "SELECT nom_plat, quantitat_disponible FROM menu";
                    PreparedStatement stmt1 = conn.prepareStatement(plDisp);
                    ResultSet resultadoM = stmt1.executeQuery();
                    while (resultadoM.next()) {
                        System.out.print("Nom del plat: " + resultadoM.getString("nom_plat"));
                        System.out.println(", Quantitat Disponible : " + resultadoM.getInt("quantitat_disponible"));
                    }
                    System.out.println();
                    stmt1.close();
                    conn.close();
                break;

                case "2":
                    System.out.println();
                    System.out.println("Taules disponibles. ");
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
                    System.out.println("Estat de lliurament de les comandes. ");
                    Connection conn3 = DriverManager.getConnection(DB_URL, USER, PASS);
                    String estatP = "SELECT * FROM comandes";
                    PreparedStatement stmt4 = conn3.prepareStatement(estatP);
                    ResultSet resultadoM3 = stmt4.executeQuery();
                    while (resultadoM3.next()) {
                        System.out.print(" Número de la comanda: " + resultadoM3.getInt("id_comanda"));
                        System.out.print(", DNI Client: " + resultadoM3.getString("dni"));
                        System.out.print(", Data: " + resultadoM3.getDate("fecha"));
                        System.out.println(", Hora: " + resultadoM3.getTime("hora"));
                    }
                    System.out.println();
                    stmt4.close();
                    conn3.close();
                break;

                case "4":
                    System.out.println();
                    System.out.println("Disponibilitat de les taules. ");
                    Connection conn4 = DriverManager.getConnection(DB_URL, USER, PASS);
                    String tauDisp = "SELECT * FROM taules";
                    PreparedStatement stmt5 = conn4.prepareStatement(tauDisp);
                    ResultSet resultadoM4 = stmt5.executeQuery();
                    while (resultadoM4.next()) {
                        System.out.print(" Numero de taula: " + resultadoM4.getInt("id_taula"));
                        System.out.print(", capacitat: " + resultadoM4.getInt("capacitat"));
                        System.out.println(", Ocupada: " + resultadoM4.getBoolean("ocupada"));
                    }
                    System.out.println();
                    System.out.println("Introdueix el número de la taula que vols canviar el seu estat. ");
                    Integer numT = lector.nextInt();
                    String cambDispT = "UPDATE taules SET ocupada = false WHERE id_taula = ?";
                    PreparedStatement stmt6 = conn4.prepareStatement(cambDispT);
                    stmt6.setInt(1, numT);
                    stmt6.executeUpdate();
                    int filasAfectadas = stmt6.executeUpdate();

                    if (filasAfectadas > 0) {
                        System.out.println("Taula actualitzada correctament.");
                    } else {
                        System.out.println("Error en actualitzar l'estat de taula.");
                    }
                    stmt6.close();
                    conn4.close();
                break;

                case "5":
                    System.out.println();
                    Connection conexion5 = DriverManager.getConnection(DB_URL, USER, PASS);
                        String consSql = "SELECT * FROM reserves";
                        PreparedStatement pstmt5 = conexion5.prepareStatement(consSql);
                        ResultSet rs = pstmt5.executeQuery();
                        while (rs.next()) {
                            System.out.print("Id reserva: " + rs.getInt("id"));
                            System.out.print(", DNI: " + rs.getString("dni"));
                            System.out.print(", Numero de taula: " + rs.getInt("id_taula"));
                            System.out.print(", Data: " + rs.getDate("fecha"));
                            System.out.println(", Hora: " + rs.getTime("hora"));
                        }
                        System.out.println("Introdueix el dni del client.");
                        String dniC = lector.nextLine();
                        String deleteRs = "DELETE FROM reserves WHERE dni = ?";
                        PreparedStatement pstmt6 = conexion5.prepareStatement(deleteRs);
                        pstmt6.setString(1, dniC);
                        pstmt6.executeUpdate();
                        System.out.println("Reserva eliminada.");
                        pstmt6.close();
                        conexion5.close();
                break;

                case "6":
                    System.out.println("Gestió del menú i productes:");
                    System.out.println("1. Afegir un nou producte.");
                    System.out.println("2. Comprar productes (actualitzar estoc).");
                    System.out.println("3. Crear un nou plat al menú.");
                    System.out.println("4. Tornar.");
                    String opcioMenu = lector.nextLine();

                    Connection conn6 = DriverManager.getConnection(DB_URL, USER, PASS);

                    switch (opcioMenu) {
                        case "1":
                            System.out.print("Nom del producte: ");
                            String nomProducte = lector.nextLine();
                            System.out.print("Preu: ");
                            double preuProducte = Double.parseDouble(lector.nextLine());
                            System.out.print("ID del proveïdor: ");
                            int idProveidor = Integer.parseInt(lector.nextLine());

                            String sqlInsertProducte = "INSERT INTO productes (nom, preu, id_proveidor) VALUES (?, ?, ?)";
                            PreparedStatement stmtInsertProducte = conn6.prepareStatement(sqlInsertProducte);
                            stmtInsertProducte.setString(1, nomProducte);
                            stmtInsertProducte.setDouble(2, preuProducte);
                            stmtInsertProducte.setInt(3, idProveidor);
                            stmtInsertProducte.executeUpdate();
                            System.out.println("Producte afegit correctament!");
                            stmtInsertProducte.close();
                        break;

                        case "2":
                            String consulta = "SELECT * FROM productes";
                            PreparedStatement pstmt = conn6.prepareStatement(consulta);
                            ResultSet rS = pstmt.executeQuery();
                            while (rS.next()) {
                                System.out.print("Id producte: " + rS.getInt("id_producte"));
                                System.out.print(", Nom: " + rS.getString("nom"));
                                System.out.print(", Preu: " + rS.getDouble("preu"));
                                System.out.print(", ID_proveidor: " + rS.getInt("id_proveidor"));
                                System.out.println(", Stock: " + rS.getInt("stock"));
                            }

                            System.out.print("ID del producte a comprar: ");
                            int idProducteCompra = Integer.parseInt(lector.nextLine());
                            System.out.print("Quantitat a afegir: ");
                            int quantitatAfegir = Integer.parseInt(lector.nextLine());

                            String sqlUpdateStock = "UPDATE productes SET stock = stock + ? WHERE id_producte = ?";
                            PreparedStatement stmtUpdateStock = conn6.prepareStatement(sqlUpdateStock);
                            stmtUpdateStock.setInt(1, quantitatAfegir);
                            stmtUpdateStock.setInt(2, idProducteCompra);
                            stmtUpdateStock.executeUpdate();
                            System.out.println("Stock actualitzat correctament!");
                            stmtUpdateStock.close();
                        break;

                        case "3":
                            System.out.print("Nom del nou plat: ");
                            String nomPlat = lector.nextLine();
                            System.out.print("Descripció: ");
                            String descripcio = lector.nextLine();
                            System.out.print("Preu: ");
                            double preuPlat = Double.parseDouble(lector.nextLine());
                            System.out.print("Quantitat disponible: ");
                            int quantitatDisponible = Integer.parseInt(lector.nextLine());

                            String sqlInsertPlat = "INSERT INTO menu (nom_plat, descripcio, preu, quantitat_disponible) VALUES (?, ?, ?, ?)";
                            PreparedStatement stmtInsertPlat = conn6.prepareStatement(sqlInsertPlat, Statement.RETURN_GENERATED_KEYS);
                            stmtInsertPlat.setString(1, nomPlat);
                            stmtInsertPlat.setString(2, descripcio);
                            stmtInsertPlat.setDouble(3, preuPlat);
                            stmtInsertPlat.setInt(4, quantitatDisponible);
                            stmtInsertPlat.executeUpdate();

                            ResultSet generatedKeys = stmtInsertPlat.getGeneratedKeys();
                            int idNouPlat = -1;
                            if (generatedKeys.next()) {
                                idNouPlat = generatedKeys.getInt(1);
                            }
                            stmtInsertPlat.close();

                            String continuar;
                            do {
                                System.out.print("ID del producte per afegir al plat: ");
                                int idProducte = Integer.parseInt(lector.nextLine());
                                System.out.print("Quantitat d'aquest producte en el plat: ");
                                int quantitatProducte = Integer.parseInt(lector.nextLine());

                                String sqlRelacionar = "INSERT INTO menu_productes (id_menu, id_producte, quantitat) VALUES (?, ?, ?)";
                                PreparedStatement stmtRelacionar = conn6.prepareStatement(sqlRelacionar);
                                stmtRelacionar.setInt(1, idNouPlat);
                                stmtRelacionar.setInt(2, idProducte);
                                stmtRelacionar.setInt(3, quantitatProducte);
                                stmtRelacionar.executeUpdate();
                                stmtRelacionar.close();

                                System.out.print("Afegir un altre producte al plat? (si/no): ");
                                continuar = lector.nextLine();
                            } while (continuar.equalsIgnoreCase("si"));

                            System.out.println("Nou plat afegit correctament!");
                        break;

                        case "4":
                            System.out.println("Tornant al menú principal...");
                        break;

                        default:
                            System.out.println("Opció no vàlida!");
                        break;
                    }

                    conn6.close();
                break;



                case "7":
                    System.out.println("Sortint....");
                break;

                default:
                    System.out.println("Error no has seleccionat cap opció correcta.");
                break;
            }
        }while(!opcio2.equals("7"));
    }
}
