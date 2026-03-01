package dao;

import model.Commande;
import model.Produit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;

import static XML.CommandesXML.calculerTotal;

public class CommandeDAO {

    private Connection conn;

    public CommandeDAO(Connection conn) {
        this.conn = conn;
    }

    /**
     * Insere une commande (table Commandes) et met a jour son id auto-genere.
     * Le client doit deja avoir un id valide (insere au prealable).
     */
    public void insert(Commande c) throws SQLException {
        String sql = "INSERT INTO Commandes(client_id, date_commande, total) VALUES (?, ?, ?)";
        PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ps.setInt(1, c.getClient().getId());

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        ps.setString(2, sdf.format(c.getDate()));

        // Calculer le total a partir des produits de la commande
        double total = calculerTotal(c);
        ps.setDouble(3, total);

        ps.executeUpdate();

        ResultSet keys = ps.getGeneratedKeys();
        if (keys.next()) {
            c.setId(keys.getInt(1));
        }
    }

    /**
     * Insere les lignes de commande (table Lignes_Commande) pour chaque produit.
     * La commande et les produits doivent deja avoir des ids valides.
     */
    public void insertLignes(Commande c) throws SQLException {
        String sql = "INSERT INTO Lignes_Commande(commande_id, produit_id, prix, quantite) VALUES (?, ?, ?, ?)";
        PreparedStatement ps = conn.prepareStatement(sql);

        for (Produit p : c.getProduits()) {
            ps.setInt(1, c.getId());
            ps.setInt(2, p.getId());
            ps.setDouble(3, p.getPrix());
            ps.setInt(4, p.getQuantite());
            ps.addBatch();
        }
        ps.executeBatch();
    }
}