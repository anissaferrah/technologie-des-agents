import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.core.AID;
import jade.core.ContainerID;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BuyerAgent extends Agent {
    // Map pour stocker les offres de chaque vendeur
    private Map<String, List<SellerOffer>> sellerOffersMap = new HashMap<>();
    // Plages pour la normalisation des valeurs pour offre de buyer
    private double minPrice = 100;
    private double maxPrice = 200;
    private double minQuality = 50;
    private double maxQuality = 80;
    private double minDeliveryCost = 40;
    private double maxDeliveryCost = 60;
    
    
    // Conteneur cible pour la migration
    private String targetContainer = "seller-container";
    // Liste des vendeurs ciblés
    private List<String> targetSellers = new ArrayList<>();

    protected void setup() {
        // Ajoutez les identifiants des vendeurs ciblés à la liste
        targetSellers.add("seller0");
        targetSellers.add("seller1");
        targetSellers.add("seller2");

        // Envoyez une demande de création d'offre à chaque vendeur
        for (String seller : targetSellers) {
            sendOfferRequest(seller);
        }

        // Attendez que tous les vendeurs aient créé leurs offres
        addBehaviour(new WaitSellerOffersBehaviour());
    }

    private void sendOfferRequest(String seller) {
        // Comportement OneShot pour envoyer une demande de création d'offre à un vendeur
        addBehaviour(new OneShotBehaviour(this) {
            public void action() {
                // Affichage du message de demande envoyé
                System.out.println("Envoi d'une demande de création d'offre au vendeur " + seller + "...");
                System.out.println("");
                System.out.println("Send price, quality, and delivery cost ranges to sellers " + "\n min price " + minPrice + "\n max price :" + maxPrice + "\n max Quality :" + minQuality + "\n max Quality :"  + maxQuality + "\n min DeliveryCost :"  + minDeliveryCost + "\n max DeliveryCost :"  + maxDeliveryCost);
                System.out.println("");
                // Création du message ACL
                ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
                // Contenu du message : spécification des critères
                msg.setContent(seller + "," + minPrice + "," + maxPrice + "," + minQuality + "," + maxQuality + "," + minDeliveryCost + "," + maxDeliveryCost);
                // Ajout du destinataire
                msg.addReceiver(new AID(seller, AID.ISLOCALNAME));
                // Envoi du message
                send(msg);
                // Confirmation d'envoi du message
                System.out.println("");
                System.out.println("Demande de création d'offre envoyée au vendeur " + seller + ".");
            }
        });
    }

    // Comportement pour attendre les offres des vendeurs
    private class WaitSellerOffersBehaviour extends Behaviour {
        // Nombre d'offres reçues
        private int receivedOffers = 0;

        public void action() {
            // Réception du message
            ACLMessage msg = receive();
            if (msg != null && msg.getPerformative() == ACLMessage.PROPOSE) {
                try {
                    // Extraction de l'offre du contenu du message
                    SellerOffer offer = (SellerOffer) msg.getContentObject();
                    String sellerName = offer.getSellerName();
                    
                    // Si le vendeur n'a pas encore d'offres enregistrées, créez une nouvelle liste
                    if (!sellerOffersMap.containsKey(sellerName)) {
                        sellerOffersMap.put(sellerName, new ArrayList<>());
                    }
                    // Ajout de l'offre à la liste correspondante
                    sellerOffersMap.get(sellerName).add(offer);

                    receivedOffers++;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                // Bloquer le comportement s'il n'y a pas de message
                block();
            }
        }

        public boolean done() {
            // Vérifie si toutes les offres ont été reçues
            return receivedOffers == targetSellers.size();
        }

        public int onEnd() {
            // Si toutes les offres ont été reçues, migration vers le conteneur des vendeurs
            if (done()) {
                addBehaviour(new ContainerMigrationBehaviour(targetContainer));
            }
            return super.onEnd();
        }
    }

    // Comportement pour migrer vers un autre conteneur après la réception de toutes les offres
    private class ContainerMigrationBehaviour extends OneShotBehaviour {
        private String targetContainer;

        public ContainerMigrationBehaviour(String targetContainer) {
            this.targetContainer = targetContainer;
        }

        public void action() {
            try {
                // Migration de l'agent vers le conteneur des vendeurs
                System.out.println("");
                
                System.out.println("Migration de l'agent acheteur vers le conteneur des vendeurs...");
                System.out.println("");
                ContainerID containerID = new ContainerID(targetContainer, null);
                doMove(containerID);
                // Confirmation de la migration
                System.out.println("");
                System.out.println("Migration terminée.");
                System.out.println("");
                // Collecte et évaluation des offres après la migration
                collectAndEvaluateOffers();
            } catch (Exception e) {
                System.out.println("");
                System.out.println("Erreur lors de la migration : " + e.getMessage());
                System.out.println("");
                e.printStackTrace();
            }
        }

        // Fonction pour collecter et évaluer les offres après la migration
        private void collectAndEvaluateOffers() {
            System.out.println("");
            System.out.println("Collecte et évaluation des offres après migration...");
            System.out.println("");
            // Liste pour stocker toutes les offres
            List<SellerOffer> allOffers = new ArrayList<>();
            // Pour chaque vendeur, récupérer ses offres et les évaluer
      
            for (String seller : targetSellers) {
                List<SellerOffer> sellerOffers = sellerOffersMap.get(seller);
                allOffers.addAll(sellerOffers);
                System.out.println("");
                System.out.println("Offres du vendeur " + seller + " : " + sellerOffers);
                System.out.println("");
                System.out.println("");
            }
            // Sélectionner la meilleure offre parmi toutes les offres
          
         
            SellerOffer bestOffer = chooseBestOffer(allOffers);
            System.out.println("Meilleure offre parmi tous les vendeurs : " + bestOffer);
        }

        // Fonction pour normaliser une valeur en fonction de sa plage
        private double normalize(double value, double min, double max) {
            // Calcul de la valeur normalisée
        	
            double normalizedValue = (value - min) / (max - min);// rendre 
            // Affichage de la valeur normalisée
            System.out.println("Valeur normalisée : " + normalizedValue);
            return normalizedValue;
            
        }

        // Fonction pour calculer la valeur totale d'une offre (fonction d'évaluation)
        private double calculateTotalValue(SellerOffer offer) {
            // Normalisation des critères
        
        	 
            double priceValue = normalize(offer.getPrice(), minPrice, maxPrice) * (-1);
            double qualityValue = normalize(offer.getQuality(), minQuality, maxQuality) * 1;
            double deliveryCostValue = normalize(offer.getDeliveryCost(), minDeliveryCost, maxDeliveryCost) * (-1);
            // Calcul de la valeur totale de l'offre en combinant les valeurs normalisées des critères
            double totalValue = priceValue + qualityValue + deliveryCostValue;
            // Affichage des valeurs avant normalisation
            
            System.out.println("Prix : " + offer.getPrice() + ", Qualité : " + offer.getQuality() + ", Frais de livraison : " + offer.getDeliveryCost());
            // Affichage de la valeur totale de l'offre
            System.out.println("");
            System.out.println("Valeur totale de l'offre : " + totalValue);
            
            System.out.println("");
            return totalValue;
        }

        // Fonction pour choisir la meilleure offre parmi une liste d'offres
        private SellerOffer chooseBestOffer(List<SellerOffer> offers) {
            SellerOffer bestOffer = null;
            double bestValue = Double.NEGATIVE_INFINITY;
            // Parcours de toutes les offres pour trouver la meilleure
            int i=1;
        
            for (SellerOffer offer : offers) {
                System.out.println("offre  "+ i+':');
                double value = calculateTotalValue(offer);
                // Si la valeur actuelle est meilleure que la meilleure valeur actuelle, mettez à jour la meilleure offre
                if (value > bestValue) {
                    bestValue = value;
                    bestOffer = offer;
                }
                i++;
            }
            // Retourne la meilleure offre
            return bestOffer;
        }
    }}