package XML;

import dao.CommandeDAO;
import model.Commande;
import model.Produit;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import sql.SQLConnection;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

public class ExportCommandesXML {

    private static final String OUTPUT_FILE = "commandes_exportees.xml";

    /**
     * Lit toutes les commandes depuis la base de donnees,
     * construit un document XML et l'ecrit dans un fichier.
     *
     * Format produit (identique a exportCommandeExemple.xml) :
     * <commandes>
     *   <commande id="C1" nb-produit="3">
     *     <nom-client>...</nom-client>
     *     <email>...</email>
     *     <ville>...</ville>
     *     <date>...</date>
     *     <total>...</total>
     *     <produits>
     *       <produit>
     *         <nom>...</nom>
     *         <prix>...</prix>
     *         <quantite>...</quantite>
     *       </produit>
     *     </produits>
     *   </commande>
     * </commandes>
     */
    public void exporter() {
        try (Connection conn = SQLConnection.getConnection()) {

            // 1) Lire les commandes depuis la base
            CommandeDAO commandeDAO = new CommandeDAO(conn);
            List<Commande> commandes = commandeDAO.findAll();

            if (commandes.isEmpty()) {
                System.out.println("Aucune commande en base, rien a exporter.");
                return;
            }

            // 2) Construire le document XML
            Element racine = new Element("commandes");
            Document document = new Document(racine);

            SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy");

            for (Commande cmd : commandes) {
                Element commandeElem = new Element("commande");
                commandeElem.setAttribute("id", "C" + cmd.getId());
                commandeElem.setAttribute("nb-produit", String.valueOf(cmd.getProduits().size()));

                // Client
                commandeElem.addContent(new Element("nom-client").setText(cmd.getClient().getNom_client()));
                commandeElem.addContent(new Element("email").setText(cmd.getClient().getEmail()));
                commandeElem.addContent(new Element("ville").setText(cmd.getClient().getVille()));

                // Date
                commandeElem.addContent(new Element("date").setText(sdf.format(cmd.getDate())));

                // Total
                double total = CommandesXML.calculerTotal(cmd);
                commandeElem.addContent(new Element("total").setText(String.valueOf(total)));

                // Produits
                Element produitsElem = new Element("produits");
                for (Produit p : cmd.getProduits()) {
                    Element produitElem = new Element("produit");
                    produitElem.addContent(new Element("nom").setText(p.getNom()));
                    produitElem.addContent(new Element("prix").setText(String.valueOf(p.getPrix())));
                    produitElem.addContent(new Element("quantite").setText(String.valueOf(p.getQuantite())));
                    produitsElem.addContent(produitElem);
                }
                commandeElem.addContent(produitsElem);

                racine.addContent(commandeElem);
            }

            // 3) Ecrire le document dans un fichier XML
            XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat().setEncoding("UTF-8"));
            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(OUTPUT_FILE), "UTF-8");
            outputter.output(document, writer);
            writer.close();

            System.out.println("Export termine : " + commandes.size() + " commande(s) exportee(s) dans '" + OUTPUT_FILE + "'");

        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de l'export des commandes :");
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("Erreur d'ecriture du fichier XML :");
            e.printStackTrace();
        }
    }
}
