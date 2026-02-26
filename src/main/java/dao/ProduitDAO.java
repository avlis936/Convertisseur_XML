package dao;

import model.Produit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProduitDAO {

    private Connection conn;

    public ProduitDAO(Connection conn) {
        this.conn = conn;
    }

    public void insert(Produit p) throws SQLException {
        String sql = "INSERT INTO Produits(nom, prix, quantite) VALUES (?, ?, ?)";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, p.getNom());
        ps.setDouble(2, p.getPrix());
        ps.setInt(3, p.getQuantite());
        ps.executeUpdate();
    }

    public Produit findByName(String nom) throws SQLException {
        String sql = "SELECT * FROM Produits WHERE nom = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, nom);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            return new Produit(
                    rs.getString("nom"),
                    rs.getDouble("prix"),
                    rs.getInt("quantite")
            );
        }
        return null;
    }
}
