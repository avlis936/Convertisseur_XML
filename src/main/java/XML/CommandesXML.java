package XML;

import dao.ClientDAO;
import dao.CommandeDAO;
import dao.ProduitDAO;
import model.Client;
import model.Commande;
import model.Produit;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import sql.SQLConnection;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CommandesXML {

    private static final String RESOURCE_NAME = "commandeExemple.xml";
    private Document doc;

    public CommandesXML() throws IOException, JDOMException {
        SAXBuilder builder = new SAXBuilder();
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(RESOURCE_NAME)) {
            if (is != null) {
                doc = builder.build(is);
            } else {
                String fallbackPath = "src/main/resources/" + RESOURCE_NAME;
                File f = new File(fallbackPath);
                if (!f.exists()) {
                    throw new IOException("Fichier XML introuvable : ni dans le classpath (" + RESOURCE_NAME + ") ni dans " + f.getAbsolutePath());
                }
                doc = builder.build(f);
            }
        }
    }

    /**
     * Affiche le contenu de la commande lue depuis le XML.
     */
    public void afficherCommande() {
        Element racine = doc.getRootElement();

        // Client
        Element clientElem = racine.getChild("client");
        String nomClient = clientElem.getChildText("nom-client");
        String email = clientElem.getChildText("email");
        String ville = clientElem.getChildText("ville");
        System.out.println("Client : " + nomClient + ", Email : " + email + ", Ville : " + ville);

        // Date
        String dateStr = racine.getChildText("date");
        System.out.println("Date : " + dateStr);

        // Produits
        Element produitsElem = racine.getChild("produits");
        System.out.println("Produits commandes :");
        for (Element produit : produitsElem.getChildren("produit")) {
            String nom = produit.getChildText("nom");
            String prix = produit.getChildText("prix");
            String quantite = produit.getChildText("quantite");
            if (quantite == null) quantite = produit.getChildText("quantité");
            System.out.println("  - " + nom + " | prix=" + prix + " | qte=" + quantite);
        }
    }

    public String afficherCommandeXML() {
        StringBuilder commandes = new StringBuilder();
        Element racine = doc.getRootElement();
        commandes.append("<commande>\n");
        for (Element client : racine.getChildren("client")) {
            String nomClient = client.getChildText("nom-client");
            String email = client.getChildText("email");
            String ville = client.getChildText("ville");
            commandes.append("  <client>\n");
            commandes.append("    <nom-client>").append(nomClient).append("</nom-client>\n");
            commandes.append("    <email>").append(email).append("</email>\n");
            commandes.append("    <ville>").append(ville).append("</ville>\n");
            commandes.append("  </client>\n");
            StringBuilder produitsStr = new StringBuilder("  <produits>\n");
            for (Element produit : racine.getChild("produits").getChildren("produit")) {
                String nom = produit.getChildText("nom");
                String prix = produit.getChildText("prix");
                String quantite = produit.getChildText("quantite");
                if (quantite == null) quantite = produit.getChildText("quantité");
                produitsStr.append("    <produit>\n");
                produitsStr.append("      <nom>" + nom + "</nom>\n");
                produitsStr.append("      <prix>" + prix + "</prix>\n");
                produitsStr.append("      <quantite>" + quantite + "</quantite>\n");
                produitsStr.append("    </produit>\n");
            }
            produitsStr.append("  </produits>\n");
            commandes.append(produitsStr);
        }
        commandes.append("</commande>\n");
        return commandes.toString();
    }

    /**
     * Insere la commande en base :
     *   1) Insere ou retrouve le client (par email)
     *   2) Insere la commande (table Commandes)
     *   3) Pour chaque produit de la commande, retrouve le produit en base (par nom)
     *      et insere la ligne de commande (table Lignes_Commande)
     */
    public void insertCommandeDansSQL() {
        try (Connection conn = SQLConnection.getConnection()) {
            ClientDAO clientDAO = new ClientDAO(conn);
            CommandeDAO commandeDAO = new CommandeDAO(conn);
            ProduitDAO produitDAO = new ProduitDAO(conn);

            Element racine = doc.getRootElement();

            // --- 1) Client ---
            Element clientElem = racine.getChild("client");
            String nomClient = clientElem.getChildText("nom-client");
            String email = clientElem.getChildText("email");
            String ville = clientElem.getChildText("ville");

            Client client = clientDAO.findByEmail(email);
            if (client == null) {
                client = new Client(nomClient, email, ville);
                clientDAO.insert(client);
                System.out.println("Client insere : " + nomClient + " (id=" + client.getId() + ")");
            } else {
                System.out.println("Client '" + nomClient + "' existe deja avec cette adresse mail: " + email + "(id client=" + client.getId() + ")");
            }

            // --- 2) Commande ---
            String dateStr = racine.getChildText("date");
            SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy");
            Date date = sdf.parse(dateStr);

            Commande commande = new Commande(0, client, date);

            // --- 3) Produits de la commande ---
            Element produitsElem = racine.getChild("produits");
            for (Element elem : produitsElem.getChildren("produit")) {
                String nom = elem.getChildText("nom");
                String prixText = elem.getChildText("prix");
                String quantiteText = elem.getChildText("quantite");
                if (quantiteText == null) quantiteText = elem.getChildText("quantité");

                double prix = Double.parseDouble(prixText.trim());
                int quantite = Integer.parseInt(quantiteText.trim());

                // Chercher le produit en base par son nom
                Produit produit = produitDAO.findByName(nom);
                if (produit == null) {
                    // Le produit n'existe pas encore en base, on l'insere
                    produit = new Produit(nom, prix, quantite);
                    produitDAO.insert(produit);
                    System.out.println("  Produit '" + nom + "' insere a la volee (id=" + produit.getId() + ")");
                } else {
                    // On utilise le prix/quantite de la commande (pas ceux du catalogue)
                    produit.setPrix(prix);
                    produit.setQuantite(quantite);
                }

                commande.addProduit(produit);
            }

            // Inserer la commande puis ses lignes
            commandeDAO.insert(commande);
            System.out.println("Commande inseree (id=" + commande.getId() + ", total=" + calculerTotal(commande) + ")");

            commandeDAO.insertLignes(commande);
            System.out.println("Lignes de commande inserees (" + commande.getProduits().size() + " produits)");

            System.out.println("Insertion de la commande terminee.");

        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de l'insertion de la commande :");
            e.printStackTrace();
        } catch (ParseException e) {
            System.err.println("Erreur de parsing de la date :");
            e.printStackTrace();
        }
    }

    public static double calculerTotal(Commande c) {
        double total = 0;
        for (Produit p : c.getProduits()) {
            total += p.getPrix() * p.getQuantite();
        }
        return total;
    }
}

