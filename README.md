**Convertisseur_XML**

Application Java Maven permettant d’importer, valider, stocker et exporter des données XML via JDOM2 et JDBC.

🎯 Objectif du projet

L’application gère l’intégration de données XML dans une base MySQL et leur réexportation sous forme XML.
Elle couvre trois opérations principales :

    Import des produits depuis un fichier XML.

    Import des commandes avec validation métier et mise à jour du stock.

    Export de toutes les commandes présentes en base vers un fichier XML structuré.

L’architecture du projet sépare clairement :

    la logique métier (model)

    l’accès aux données (dao)

    la gestion SQL (sql)

    le traitement XML (XML)

    le point d’entrée (main.Application)

📁 Structure du projet

<img width="526" height="923" alt="image" src="https://github.com/user-attachments/assets/855d88ca-a96d-4e85-9f17-2c5ce5e2e722" />

