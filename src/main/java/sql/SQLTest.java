package sql;

import java.sql.Connection;
import java.sql.DriverManager;

public class SQLTest {
    public static void main(String[] args) {
        try (
                Connection conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/tp_xml?useSSL=false&serverTimezone=UTC", "root", "")) {
            System.out.println("Connexion OK !");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
// Si la connexion est réussie, le message "Connexion OK !" sera affiché. Sinon, une exception sera levée et son message d'erreur sera affiché.