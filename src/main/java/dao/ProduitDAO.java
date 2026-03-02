package dao;

import model.Produit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ProduitDAO {

    private Connection conn;

    public ProduitDAO(Connection conn) {
        this.conn = conn;
    }

    /**
     * Insere un produit en base et met a jour son id auto-genere.
     */
    public void insert(Produit p) throws SQLException {
        String sql = "INSERT INTO Produits(nom, prix, quantite) VALUES (?, ?, ?)";
        PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ps.setString(1, p.getNom());
        ps.setDouble(2, p.getPrix()*2); // Le prix d'un produit inséré est le double de celui du fournisseur
        ps.setInt(3, p.getQuantite());
        ps.executeUpdate();

        ResultSet keys = ps.getGeneratedKeys();
        if (keys.next()) {
            p.setId(keys.getInt(1));
        }
    }

    /**
     * Recherche un produit par nom. Retourne null si introuvable.
     */
    public Produit findByName(String nom) throws SQLException {
        String sql = "SELECT * FROM Produits WHERE nom = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, nom);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            Produit p = new Produit(
                    rs.getString("nom"),
                    rs.getDouble("prix"),
                    rs.getInt("quantite")
            );
            p.setId(rs.getInt("id"));
            return p;
        }
        return null;
    }

    /**
     * Décrémente la quantité d'un produit en base de façon atomique.
     * Retourne true si la mise à jour a eu lieu (stock suffisant), false sinon.
     */
    public boolean decrementQuantite(int produitId, int qte) throws SQLException {
        String sql = "UPDATE Produits SET quantite = quantite - ? WHERE id = ? AND quantite >= ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, qte);
        ps.setInt(2, produitId);
        ps.setInt(3, qte);
        int updated = ps.executeUpdate();
        return updated > 0;
    }
}
