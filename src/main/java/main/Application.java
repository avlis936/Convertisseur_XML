package main;

public class Application {

    public static void main(String[] args) {
        try {
            new XML.ProduitsXML();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
