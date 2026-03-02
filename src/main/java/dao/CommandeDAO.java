package dao;

import model.Client;
import model.Commande;
import model.Produit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

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
     * Vérifie la disponibilité et décrémente le stock en base avant d'insérer chaque ligne.
     * Opère dans une transaction : si une ligne échoue (stock insuffisant), tout est rollback.
     */
    public void insertLignes(Commande c) throws SQLException {
        ProduitDAO produitDAO = new ProduitDAO(conn);
        String sql = "INSERT INTO Lignes_Commande(commande_id, produit_id, prix, quantite) VALUES (?, ?, ?, ?)";
        PreparedStatement ps = null;

        boolean previousAutoCommit = conn.getAutoCommit();
        try {
            // démarrer une transaction
            conn.setAutoCommit(false);

            ps = conn.prepareStatement(sql);

            for (Produit p : c.getProduits()) {
                // Vérifier l'id du produit
                if (p.getId() <= 0) {
                    // pas d'id -> impossible d'ajouter
                    conn.rollback();
                    throw new SQLException("Produit sans id valide: " + p.getNom());
                }

                // tenter de décrémenter le stock de façon atomique
                boolean disponible = produitDAO.decrementQuantite(p.getId(), p.getQuantite());
                if (!disponible) {
                    // stock insuffisant -> rollback et exception
                    conn.rollback();
                    throw new SQLException("Stock insuffisant pour le produit id=" + p.getId() + " nom='" + p.getNom() + "'");
                }

                // stock ok -> ajouter la ligne
                ps.setInt(1, c.getId());
                ps.setInt(2, p.getId());
                ps.setDouble(3, p.getPrix());
                ps.setInt(4, p.getQuantite());
                ps.addBatch();
            }

            ps.executeBatch();

            // tout est bon -> commit
            conn.commit();
        } catch (SQLException ex) {
            // rollback en cas d'erreur
            try {
                conn.rollback();
            } catch (SQLException r) {
                // ignore
            }
            throw ex;
        } finally {
            // restaurer l'auto-commit et fermer le prepared statement
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) {
                // ignore
            }
            conn.setAutoCommit(previousAutoCommit);
        }
    }

    /**
     * Recupere toutes les commandes depuis la base, avec leur client et leurs produits (lignes de commande).
     */
    public List<Commande> findAll() throws SQLException {
        List<Commande> commandes = new ArrayList<Commande>();

        // Requete qui joint Commandes + Clients
        String sql = "SELECT c.id AS commande_id, c.date_commande, c.total, " +
                "cl.id AS client_id, cl.nom AS client_nom, cl.email, cl.ville " +
                "FROM Commandes c " +
                "JOIN Clients cl ON c.client_id = cl.id " +
                "ORDER BY c.id";
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            // Reconstruire le client
            Client client = new Client(
                    rs.getString("client_nom"),
                    rs.getString("email"),
                    rs.getString("ville")
            );
            client.setId(rs.getInt("client_id"));

            // Reconstruire la commande
            String dateStr = rs.getString("date_commande");
            java.util.Date date;
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                date = sdf.parse(dateStr);
            } catch (ParseException e) {
                // Si le format ne correspond pas, on essaie un autre format courant
                try {
                    SimpleDateFormat sdf2 = new SimpleDateFormat("dd-M-yyyy");
                    date = sdf2.parse(dateStr);
                } catch (ParseException e2) {
                    date = new java.util.Date(); // fallback
                }
            }

            Commande commande = new Commande(rs.getInt("commande_id"), client, date);

            // Charger les produits (lignes de commande) de cette commande
            String sqlLignes = "SELECT lc.prix, lc.quantite, p.id AS produit_id, p.nom " +
                    "FROM Lignes_Commande lc " +
                    "JOIN Produits p ON lc.produit_id = p.id " +
                    "WHERE lc.commande_id = ?";
            PreparedStatement psLignes = conn.prepareStatement(sqlLignes);
            psLignes.setInt(1, commande.getId());
            ResultSet rsLignes = psLignes.executeQuery();

            while (rsLignes.next()) {
                Produit produit = new Produit(
                        rsLignes.getString("nom"),
                        rsLignes.getDouble("prix"),
                        rsLignes.getInt("quantite")
                );
                produit.setId(rsLignes.getInt("produit_id"));
                commande.addProduit(produit);
            }

            commandes.add(commande);
        }

        return commandes;
    }
}