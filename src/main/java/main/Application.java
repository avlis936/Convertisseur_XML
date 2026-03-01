package main;

import XML.CommandesXML;
import XML.ProduitsXML;

public class Application {

    public static void main(String[] args) {
        try {
            // --- Produits ---
            System.out.println("=== Lecture des produits depuis le XML ===");
            ProduitsXML produits = new ProduitsXML();
            produits.afficherProduits();

            System.out.println();
            System.out.println(produits.afficherProduitsXML());

            System.out.println();
            System.out.println("=== Insertion des produits en base ===");
            produits.insertProduitsDansSQL();

            // --- Commandes ---
            System.out.println();
            System.out.println("=== Lecture de la commande depuis le XML ===");
            CommandesXML commandes = new CommandesXML();
            commandes.afficherCommande();
            System.out.println();
            System.out.println(commandes.afficherCommandeXML());

            System.out.println();
            System.out.println("=== Insertion de la commande en base ===");
            commandes.insertCommandeDansSQL();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
