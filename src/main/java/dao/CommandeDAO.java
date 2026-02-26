package dao;

import model.Commande;
import model.Produit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CommandeDAO {

    private Connection conn;

    public CommandeDAO(Connection conn) {
        this.conn = conn;
    }
}