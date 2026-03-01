package XML;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;


public class ProduitsXML {

    // Nom du fichier dans resources (voir src/main/resources)
    private static final String RESOURCE_NAME = "produitsExemple.xml"; // fichier present dans src/main/resources

    public ProduitsXML() throws IOException, JDOMException {
        SAXBuilder builder = new SAXBuilder();

        Document doc;

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


}
