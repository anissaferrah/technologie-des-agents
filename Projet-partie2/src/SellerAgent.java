import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SellerAgent extends Agent {
    // Déclaration des variables de critères d'offre
    private double minPrice;
    private double maxPrice;
    private double minQuality;
    private double maxQuality;
    private double minDeliveryCost;
    private double maxDeliveryCost;
    // Liste pour stocker toutes les offres du vendeur avec toutes les informations
    private List<SellerOffer> allOffers = new ArrayList<>();

    protected void setup() {
        // Comportement cyclique pour traiter les demandes des acheteurs
        addBehaviour(new CyclicBehaviour(this) {
            public void action() {
                // Réception du message
                ACLMessage msg = receive();
                if (msg != null && msg.getPerformative() == ACLMessage.REQUEST) {
                    try {
                        // Extraction des valeurs du message
                        String[] values = msg.getContent().split(",");
                        String sellerId = values[0];
                        minPrice = Double.parseDouble(values[1]);
                        maxPrice = Double.parseDouble(values[2]);
                        minQuality = Double.parseDouble(values[3]);
                        maxQuality = Double.parseDouble(values[4]);
                        minDeliveryCost = Double.parseDouble(values[5]);
                        maxDeliveryCost = Double.parseDouble(values[6]);

                        // Création de l'offre basée sur les valeurs extraites
                        SellerOffer offer = createOffer(sellerId);

                        // Stockage de l'offre avec toutes les informations
                        allOffers.add(offer);

                        // Envoi de l'offre dans un message de réponse
                        ACLMessage reply = msg.createReply();
                        reply.setPerformative(ACLMessage.PROPOSE);
                        reply.setContentObject(offer);
                        send(reply);
                    } catch (IOException | NumberFormatException e) {
                        e.printStackTrace();
                    }
                } else {
                    block();
                }
            }
        });
    }

    // Méthode pour créer une offre
    private SellerOffer createOffer(String sellerName) {
        // Génération aléatoire des valeurs de l'offre dans les plages spécifiées
        double price = Math.random() * (maxPrice - minPrice) + minPrice;
        double quality = Math.random() * (maxQuality - minQuality) + minQuality;
        double deliveryCost = Math.random() * (maxDeliveryCost - minDeliveryCost) + minDeliveryCost;

        // Création de l'objet SellerOffer avec les valeurs générées
        return new SellerOffer(sellerName, price, quality, deliveryCost);
    }

    // Méthode pour récupérer les offres d'un vendeur par son nom
    public List<SellerOffer> getOffersBySellerName(String sellerName) {
        // Liste pour stocker les offres du vendeur spécifié
        List<SellerOffer> offersBySeller = new ArrayList<>();
        // Parcours de toutes les offres pour trouver celles du vendeur spécifié
        for (SellerOffer offer : allOffers) {
            if (offer.getSellerName().equals(sellerName)) {
                offersBySeller.add(offer);
            }
        }
        // Retourne la liste des offres du vendeur spécifié
        return offersBySeller;
    }
}
