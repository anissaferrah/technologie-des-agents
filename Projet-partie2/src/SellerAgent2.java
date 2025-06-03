import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SellerAgent2 extends Agent {
    private List<SellerOffer> allOffers = new ArrayList<>();

    protected void setup() {
        System.out.println("Agent Seller démarré : " + getLocalName());
        addBehaviour(new CyclicBehaviour(this) {
            public void action() {
                ACLMessage msg = receive();
                if (msg != null && msg.getPerformative() == ACLMessage.REQUEST) {
                    try {
                        String[] values = msg.getContent().split(",");
                        String sellerId = values[0];
                        double minPrice = Double.parseDouble(values[1]);
                        double maxPrice = Double.parseDouble(values[2]);
                        double minQuality = Double.parseDouble(values[3]);
                        double maxQuality = Double.parseDouble(values[4]);
                        double minDeliveryCost = Double.parseDouble(values[5]);
                        double maxDeliveryCost = Double.parseDouble(values[6]);

                        System.out.println("Requête d'offre reçue de l'acheteur : " + msg.getSender().getLocalName());

                        // Créer une offre basée sur les valeurs extraites
                        SellerOffer offer = createOffer(sellerId, minPrice, maxPrice, minQuality, maxQuality, minDeliveryCost, maxDeliveryCost);

                        // Stocker l'offre avec toutes les informations
                        allOffers.add(offer);

                        // Envoyer l'offre dans un message de réponse
                        ACLMessage reply = msg.createReply();
                        reply.setPerformative(ACLMessage.PROPOSE);
                        reply.setContentObject(offer);
                        send(reply);
                        System.out.println("Offre envoyée à " + msg.getSender().getLocalName());
                    } catch (IOException | NumberFormatException e) {
                        e.printStackTrace();
                    }
                } else {
                    block();
                }
            }
        });
    }

    private SellerOffer createOffer(String sellerName, double minPrice, double maxPrice, double minQuality, double maxQuality, double minDeliveryCost, double maxDeliveryCost) {
        double price = Math.random() * (maxPrice - minPrice) + minPrice;
        double quality = Math.random() * (maxQuality - minQuality) + minQuality;
        double deliveryCost = Math.random() * (maxDeliveryCost - minDeliveryCost) + minDeliveryCost;

        return new SellerOffer(sellerName, price, quality, deliveryCost);
    }

    // Méthode pour récupérer les offres d'un vendeur par son nom
    public List<SellerOffer> getOffersBySellerName(String sellerName) {
        List<SellerOffer> offersBySeller = new ArrayList<>();
        for (SellerOffer offer : allOffers) {
            if (offer.getSellerName().equals(sellerName)) {
                offersBySeller.add(offer);
            }
        }
        return offersBySeller;
    }
}
