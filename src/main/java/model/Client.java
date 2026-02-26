package model;

import java.util.ArrayList;

public class Client {
    private int id;
    private String nom_client;
    private String email;
    private String ville;


    public Client(String nom_client, String email, String ville) {
        this.nom_client = nom_client;
        this.email = email;
        this.ville = ville;
    }

    // ID
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    // Nom client
    public String getNom_client() {
        return nom_client;
    }

    public void setNom_client(String nom_client) {
        this.nom_client = nom_client;
    }

    // Email
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // Ville
    public String getVille() {
        return ville;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }

     @Override
    public String toString() {
         return "Client{" +
                 "id=" + id +
                 ", nom_client='" + nom_client + '\'' +
                 ", email='" + email + '\'' +
                 ", ville='" + ville + '\'' +
                 '}';
    }
}
