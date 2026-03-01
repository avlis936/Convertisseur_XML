package XML;

import dao.ProduitDAO;
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


public class ProduitsXML {

    // Nom du fichier dans resources (voir src/main/resources)
    private static final String RESOURCE_NAME = "produitsExemple.xml"; // fichier present dans src/main/resources
    private SAXBuilder builder = new SAXBuilder();
    private Document doc;

    public ProduitsXML() throws IOException, JDOMException {
        // 1) Essayer de charger depuis le classpath (fonctionne dans IntelliJ, maven, jar, etc.)
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(RESOURCE_NAME)) {
            if (is != null) {
                doc = builder.build(is);
            } else {
                // 2) Fallback : chemin relatif du projet
                String fallbackPath = "src/main/resources/" + RESOURCE_NAME;
                File f = new File(fallbackPath);
                if (!f.exists()) {
                    throw new IOException("Fichier XML introuvable : ni dans le classpath (" + RESOURCE_NAME + ") ni dans " + f.getAbsolutePath());
                }
                doc = builder.build(f);
            }
        }
    }

    public void afficherProduits() {
        Element racine = doc.getRootElement();
        int index = 0;
        for (Element produit : racine.getChildren("produit")) {
            index++;
            String nom = produit.getChildText("nom");
            String prixText = produit.getChildText("prix");
            // Accepte aussi "quantite" sans accent pour la compatibilité avec d'autres fichiers
            String quantiteText = produit.getChildText("quantite");
            if (quantiteText == null) quantiteText = produit.getChildText("quantité");

            if (nom == null) nom = "<nom absent>";

            if (prixText == null || prixText.trim().isEmpty()) {
                System.err.println("[WARN] Produit #" + index + " ('" + nom + "') : balise <prix> manquante ou vide - entree ignoree.");
                continue;
            }
            if (quantiteText == null || quantiteText.trim().isEmpty()) {
                System.err.println("[WARN] Produit #" + index + " ('" + nom + "') : balise <quantite> manquante ou vide - entree ignoree.");
                continue;
            }

            double prix;
            int quantite;
            try {
                prix = Double.parseDouble(prixText.trim());
            } catch (NumberFormatException e) {
                System.err.println("[WARN] Produit #" + index + " ('" + nom + "') : valeur de <prix> invalide='" + prixText + "' - entree ignoree.");
                continue;
            }
            try {
                quantite = Integer.parseInt(quantiteText.trim());
            } catch (NumberFormatException e) {
                System.err.println("[WARN] Produit #" + index + " ('" + nom + "') : valeur de <quantite> invalide='" + quantiteText + "' - entree ignoree.");
                continue;
            }

            System.out.println("Produit : " + nom + ", Prix : " + prix + ", Quantite : " + quantite);
        }
    }

    public String afficherProduitsXML() {
        StringBuilder sb = new StringBuilder();
        Element racine = doc.getRootElement();
        sb.append("<produits>\n");
        for (Element produit : racine.getChildren("produit")) {
            String nom = produit.getChildText("nom");
            String prix = produit.getChildText("prix");
            String quantite = produit.getChildText("quantite");
            if (quantite == null) quantite = produit.getChildText("quantité");

            if (nom == null) nom = "<nom absent>";
            if (prix == null) prix = "<prix absent>";
            if (quantite == null) quantite = "<quantite absente>";

            sb.append("  <Produit>\n");
            sb.append("    <Nom>").append(nom).append("</Nom>\n");
            sb.append("    <Prix>").append(prix).append("</Prix>\n");
            sb.append("    <Quantite>").append(quantite).append("</Quantite>\n");
            sb.append("  </Produit>\n");
        }
        sb.append("</Produits>\n");
        return sb.toString();
    }

    /**
     * Lit les produits du XML charge et les insere dans la table Produits en base.
     * Si un produit du meme nom existe deja, il est ignore.
     */
    public void insertProduitsDansSQL() {
        try (Connection conn = SQLConnection.getConnection()) {
            ProduitDAO dao = new ProduitDAO(conn);
            Element racine = doc.getRootElement();

            for (Element elem : racine.getChildren("produit")) {
                String nom = elem.getChildText("nom");
                String prixText = elem.getChildText("prix");
                String quantiteText = elem.getChildText("quantite");
                if (quantiteText == null) quantiteText = elem.getChildText("quantité");

                if (nom == null || prixText == null || quantiteText == null) {
                    System.err.println("[WARN] Produit incomplet dans le XML, ignore.");
                    continue;
                }

                double prix = Double.parseDouble(prixText.trim());
                int quantite = Integer.parseInt(quantiteText.trim());

                // Verifier si le produit existe deja en base
                Produit existant = dao.findByName(nom);
                if (existant != null) {
                    System.out.println("Produit '" + nom + "' existe deja en base (id=" + existant.getId() + "), ignore.");
                    continue;
                }

                Produit p = new Produit(nom, prix, quantite);
                dao.insert(p);
                System.out.println("Produit insere : " + p.getNom() + " (id=" + p.getId() + ")");
            }

            System.out.println("Insertion des produits terminee.");
        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de l'insertion des produits :");
            e.printStackTrace();
        } catch (NumberFormatException e) {
            System.err.println("Erreur de format dans le XML :");
            e.printStackTrace();
        }
    }

}
