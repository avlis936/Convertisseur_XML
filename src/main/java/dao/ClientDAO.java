package dao;

import model.Client;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ClientDAO {

    Connection conn;

    public ClientDAO(Connection conn) {
        this.conn = conn;
    }
}
