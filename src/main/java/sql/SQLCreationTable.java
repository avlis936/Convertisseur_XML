package sql;

import java.sql.Connection;
import java.sql.Statement;

public class SQLCreationTable {

    public static void main(String[] args) {
        try (Connection conn = SQLConnection.getConnection();
             Statement st = conn.createStatement()) {

            String sqlProduits = """
                CREATE TABLE IF NOT EXISTS Produits (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    nom VARCHAR(100),
                    prix DOUBLE,
                    quantite INT
                )
            """;

            String sqlClients = """
                CREATE TABLE IF NOT EXISTS Clients (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    nom VARCHAR(100),
                    email VARCHAR(100) UNIQUE,
                    ville VARCHAR(100)
                )
            """;

            String sqlCommandes = """
                CREATE TABLE IF NOT EXISTS Commandes (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    client_id INT,
                    date_commande VARCHAR(20),
                    total DOUBLE
                )
            """;

            String sqlCommandesAlter = """
                ALTER TABLE Commandes ADD CONSTRAINT fk_commandes_client_id\s
                    FOREIGN KEY (client_id) REFERENCES Clients(id)
           \s""";

            String sqlLignes = """
                CREATE TABLE IF NOT EXISTS Lignes_Commande (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    commande_id INT,
                    produit_id INT,
                    prix DOUBLE,
                    quantite INT
                )
            """;

            String sqlLignesAlter1 = """
                ALTER TABLE Lignes_Commande
                    ADD CONSTRAINT fk_lcommande_commande_id
                    FOREIGN KEY (commande_id) REFERENCES Commandes(id)
            """;

            String sqlLignesAlter2 = """
                ALTER TABLE Lignes_Commande
                    ADD CONSTRAINT fk_lcommande_produit_id
                    FOREIGN KEY (produit_id) REFERENCES Produits(id)
            """;


            st.execute(sqlProduits);
            st.execute(sqlClients);
            st.execute(sqlCommandes);
            st.execute(sqlCommandesAlter);
            st.execute(sqlLignes);
            st.execute(sqlLignesAlter1);
            st.execute(sqlLignesAlter2);

            System.out.println("Tables créées avec succès !");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}

