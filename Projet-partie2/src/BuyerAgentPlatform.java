import jade.core.AID;
import jade.core.Agent;
import jade.core.ContainerID;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import jade.wrapper.AgentContainer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BuyerAgentPlatform extends Agent {
    private Map<String, List<SellerOffer>> sellerOffersMap = new HashMap<>();
    private List<String> targetSellers = new ArrayList<>();
    private String targetPlatformHost = "192.168.133.1"; // Updated to the IP of the target platform
    private int targetPlatformPort = 1099; // Updated to the port of the target platform
    private String targetContainer = "container2"; // The name of the target container

    private double minPrice = 100;
    private double maxPrice = 200;
    private double minQuality = 100;
    private double maxQuality = 200;
    private double minDeliveryCost = 100;
    private double maxDeliveryCost = 200;

    protected void setup() {
        // Adding target sellers
        targetSellers.add("seller1");
        targetSellers.add("seller2");

        // Sending offer requests to target sellers
        for (String seller : targetSellers) {
            sendOfferRequest(seller);
        }

        // Adding behavior to wait for seller offers
        addBehaviour(new WaitSellerOffersBehaviour());
    }

    private void sendOfferRequest(String seller) {
        addBehaviour(new OneShotBehaviour(this) {
            public void action() {
                ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
                msg.setContent(seller + "," + minPrice + "," + maxPrice + "," + minQuality + "," + maxQuality + "," + minDeliveryCost + "," + maxDeliveryCost);
                AID sellerAID = new AID(seller, AID.ISLOCALNAME);
                sellerAID.addAddresses("http://" + targetPlatformHost + ":" + targetPlatformPort + "/acc");
                msg.addReceiver(sellerAID);
                send(msg);
                System.out.println("Demande de création d'offre envoyée au vendeur " + seller + ".");
            }
        });
    }


    private class WaitSellerOffersBehaviour extends Behaviour {
        private int receivedOffers = 0;

        public void action() {
            ACLMessage msg = receive();
            if (msg != null && msg.getPerformative() == ACLMessage.PROPOSE) {
                try {
                    SellerOffer offer = (SellerOffer) msg.getContentObject();
                    String sellerName = offer.getSellerName();
                    if (!sellerOffersMap.containsKey(sellerName)) {
                        sellerOffersMap.put(sellerName, new ArrayList<>());
                    }
                    sellerOffersMap.get(sellerName).add(offer);

                    receivedOffers++;
                    System.out.println("Offre reçue de " + sellerName + ": " + offer);
                } catch (UnreadableException e) {
                    e.printStackTrace();
                }
            } else {
                block();
            }
        }

        public boolean done() {
            return receivedOffers == targetSellers.size();
        }

        public int onEnd() {
            if (done()) {
                addBehaviour(new PlatformMigrationBehaviour());
            }
            return super.onEnd();
        }
    }

    private class PlatformMigrationBehaviour extends OneShotBehaviour {
        public void action() {
            ContainerID destination = new ContainerID(targetContainer, null);
            destination.setAddress("http://" + targetPlatformHost + ":" + targetPlatformPort + "/acc");
            doMove(destination);
            System.out.println("Migration vers " + targetContainer + " sur la plateforme " + targetPlatformHost);
            addBehaviour(new CollectAndEvaluateOffersBehaviour());
        }
    }

    private class CollectAndEvaluateOffersBehaviour extends OneShotBehaviour {
        public void action() {
            System.out.println("Collecte et évaluation des offres après migration...");
            List<SellerOffer> allOffers = new ArrayList<>();
            for (String seller : targetSellers) {
                List<SellerOffer> sellerOffers = sellerOffersMap.get(seller);
                if (sellerOffers != null) {
                    allOffers.addAll(sellerOffers);
                    System.out.println("Offres du vendeur " + seller + " : " + sellerOffers);
                }
            }
            SellerOffer bestOffer = chooseBestOffer(allOffers);
            System.out.println("Meilleure offre parmi tous les vendeurs : " + bestOffer);
        }

        private double normalize(double value, double min, double max) {
            return (value - min) / (max - min);
        }

        private double calculateTotalValue(SellerOffer offer) {
            double priceValue = normalize(offer.getPrice(), minPrice, maxPrice) * (-1);
            double qualityValue = normalize(offer.getQuality(), minQuality, maxQuality) * 1;
            double deliveryCostValue = normalize(offer.getDeliveryCost(), minDeliveryCost, maxDeliveryCost) * (-1);
            return priceValue + qualityValue + deliveryCostValue;
        }

        private SellerOffer chooseBestOffer(List<SellerOffer> offers) {
            SellerOffer bestOffer = null;
            double bestValue = Double.NEGATIVE_INFINITY;
            for (SellerOffer offer : offers) {
                double value = calculateTotalValue(offer);
                if (value > bestValue) {
                    bestValue = value;
                    bestOffer = offer;
                }
            }
            return bestOffer;
        }
    }

}
