package model;

import java.util.ArrayList;
import java.util.Date;

public class Commande {
    private int id;
    private Client client;
    private Date date;
    private ArrayList<Produit> produits;

    public Commande(int id, Client client, Date date) {
        this.id = id;
        this.client = client;
        this.date = date;
        this.produits = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public ArrayList<Produit> getProduits() {
        return produits;
    }

    public void setProduits(ArrayList<Produit> produits) {
        this.produits = produits;
    }

    public void addProduit(Produit produit) {
        this.produits.add(produit);
    }

    public void removeProduit(Produit produit) {
        this.produits.remove(produit);
    }

    public void clearProduits() {
        this.produits.clear();
    }

    public String showProduits() {
        String val = "";
        for (Produit produit : produits) {
            val += produit.toString() + "\n";
        }
        return val;
    }

    @Override
    public String toString() {
        return "Commande{" +
                "id=" + id +
                ", client=" + client +
                ", date=" + date +
                ", produits{" + showProduits() + '}' +
                '}';
    }
}
