package dao;

import model.Client;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ClientDAO {

    Connection conn;

    public ClientDAO(Connection conn) {
        this.conn = conn;
    }

    /**
     * Insere un client
     */
    public void insert(Client c) throws SQLException {
        String sql = "INSERT INTO Clients(nom, email, ville) VALUES (?, ?, ?)";
        PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ps.setString(1, c.getNom_client());
        ps.setString(2, c.getEmail());
        ps.setString(3, c.getVille());
        ps.executeUpdate();

        ResultSet keys = ps.getGeneratedKeys();
        if (keys.next()) {
            c.setId(keys.getInt(1));
        }
    }

    /**
     * Recherche un client par email
     */
    public Client findByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM Clients WHERE email = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, email);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            Client c = new Client(
                    rs.getString("nom"),
                    rs.getString("email"),
                    rs.getString("ville")
            );
            c.setId(rs.getInt("id"));
            return c;
        }
        return null;
    }

    /**
     * Recherche un client par id. Retourne null si introuvable.
     */
    public Client findById(int id) throws SQLException {
        String sql = "SELECT * FROM Clients WHERE id = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            Client c = new Client(
                    rs.getString("nom"),
                    rs.getString("email"),
                    rs.getString("ville")
            );
            c.setId(rs.getInt("id"));
            return c;
        }
        return null;
    }
}
