import java.io.Serializable;

// La classe SellerOffer représente une offre faite par un vendeur
class SellerOffer implements Serializable {
    // Attributs de l'offre
    private double price; // Prix de l'offre
    private double quality; // Qualité de l'offre
    private double deliveryCost; // Coût de livraison de l'offre
    private String sellerName; // Nom du vendeur qui fait l'offre

    // Constructeur de la classe SellerOffer
    public SellerOffer(String sellerName, double price, double quality, double deliveryCost) {
        // Initialisation des attributs avec les valeurs fournies
        this.price = price;
        this.quality = quality;
        this.deliveryCost = deliveryCost;
        this.sellerName = sellerName;
    }

    // Getters pour accéder aux valeurs des attributs
    public double getPrice() {
        return price;
    }

    public double getQuality() {
        return quality;
    }

    public double getDeliveryCost() {
        return deliveryCost;
    }

    public String getSellerName() {
        return sellerName;
    }

    // Méthode toString pour obtenir une représentation textuelle de l'offre
    @Override
    public String toString() {
        return "Vendeur: " + sellerName + ", Prix: " + price + ", Qualité: " + quality + ", Frais de livraison: " + deliveryCost;
    }
}
