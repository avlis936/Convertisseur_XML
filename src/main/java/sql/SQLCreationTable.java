package sql;

import java.sql.Connection;
import java.sql.Statement;

public class SQLCreationTable {

    public static void main(String[] args) {
        try (Connection conn = SQLConnection.getConnection();
             Statement st = conn.createStatement()) {

            String sqlProduits = "CREATE TABLE IF NOT EXISTS Produits (" +
                    "\n    id INT AUTO_INCREMENT PRIMARY KEY," +
                    "\n    nom VARCHAR(100)," +
                    "\n    prix DOUBLE," +
                    "\n    quantite INT" +
                    "\n)";

            String sqlClients = "CREATE TABLE IF NOT EXISTS Clients (" +
                    "\n    id INT AUTO_INCREMENT PRIMARY KEY," +
                    "\n    nom VARCHAR(100)," +
                    "\n    email VARCHAR(100) UNIQUE," +
                    "\n    ville VARCHAR(100)" +
                    "\n)";

            String sqlCommandes = "CREATE TABLE IF NOT EXISTS Commandes (" +
                    "\n    id INT AUTO_INCREMENT PRIMARY KEY," +
                    "\n    client_id INT," +
                    "\n    date_commande VARCHAR(20)," +
                    "\n    total DOUBLE" +
                    "\n)";

            String sqlCommandesAlter = "ALTER TABLE Commandes ADD CONSTRAINT fk_commandes_client_id " +
                    "FOREIGN KEY (client_id) REFERENCES Clients(id)";

            String sqlLignes = "CREATE TABLE IF NOT EXISTS Lignes_Commande (" +
                    "\n    id INT AUTO_INCREMENT PRIMARY KEY," +
                    "\n    commande_id INT," +
                    "\n    produit_id INT," +
                    "\n    prix DOUBLE," +
                    "\n    quantite INT" +
                    "\n)";

            String sqlLignesAlter1 = "ALTER TABLE Lignes_Commande " +
                    "ADD CONSTRAINT fk_lcommande_commande_id " +
                    "FOREIGN KEY (commande_id) REFERENCES Commandes(id)";

            String sqlLignesAlter2 = "ALTER TABLE Lignes_Commande " +
                    "ADD CONSTRAINT fk_lcommande_produit_id " +
                    "FOREIGN KEY (produit_id) REFERENCES Produits(id)";


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
