import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class Selleragent extends Agent {

    private int startingPrice = 100;
    private int currentPrice;
    private int reservePrice = 120;
    // Indicateur de vente de l'objet
    private boolean sold = false;
    // Meilleur acheteur (nom de l'agent)
    private String bestBuyer = null;

    protected void setup() {
        System.out.println("Seller agent " + getAID().getName() + " is ready.");

        // Initialise le prix courant au prix de départ
        currentPrice = startingPrice;

        // Comportement qui s'exécute toutes les 5 secondes pour envoyer une CFP (Call for Proposal)
        addBehaviour(new TickerBehaviour(this, 5000) {
            protected void onTick() {
                // Si l'objet n'est pas encore vendu
                if (!sold) {
                    // Affiche le prix courant
                    System.out.println("Current price: " + currentPrice);
                    // Crée un message CFP (Call for Proposal)
                    ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
                    // Ajoute le prix courant au message
                    cfp.setContent(String.valueOf(currentPrice));

                    // Envoie une CFP à tous les acheteurs
                    for (int i = 0; i < 3; i++) {
                        cfp.addReceiver(new jade.core.AID("buyer" + i, AID.ISLOCALNAME));
                    }
                    // Envoie le message CFP
                    myAgent.send(cfp);
                }
            }
        });

        // Comportement cyclique pour recevoir les propositions des acheteurs
        addBehaviour(new CyclicBehaviour() {
            public void action() {
                // Reçoit les messages de type PROPOSE
                ACLMessage reply = myAgent.receive(MessageTemplate.MatchPerformative(ACLMessage.PROPOSE));
                if (reply != null) {
                    // Convertit le contenu du message en entier (l'offre)
                    int offer = Integer.parseInt(reply.getContent());
                    // Si l'offre est supérieure au prix courant
                    if (offer > currentPrice) {
                        // Met à jour le prix courant avec la nouvelle offre
                        currentPrice = offer;
                        // Met à jour le meilleur acheteur
                        bestBuyer = reply.getSender().getLocalName();
                    }
                } else {
                    // Bloque jusqu'à ce qu'un nouveau message soit reçu
                    block();
                }
            }
        });

        // Comportement qui s'exécute après 1 minute pour terminer l'enchère
        addBehaviour(new TickerBehaviour(this, 60000) {
            protected void onTick() {
                if (currentPrice >= reservePrice && bestBuyer != null) {
                    // Affiche que l'objet est vendu avec le prix et le nom de l'acheteur
                    System.out.println("Item sold to " + bestBuyer + " for " + currentPrice);
                    // Marque l'objet comme vendu
                    sold = true;
                } else {
                    // Affiche que l'enchère s'est terminée sans gagnant
                    System.out.println("Auction ended with no winner.");
                }
                // Supprime l'agent
                doDelete();
            }
        });
    }
}
