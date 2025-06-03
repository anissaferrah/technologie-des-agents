import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.util.Random;

public class BuyerAgent extends Agent {
    private int maxPrice;  // Prix maximum que l'acheteur est prêt à payer

    protected void setup() {
        Random rand = new Random();
        // Génère un prix maximum aléatoire entre 50 et 250
        maxPrice = rand.nextInt(200) + 50;
        //System.out.println("Buyer agent " + getAID().getName() + " is ready. Max price: " + maxPrice);

        // Comportement cyclique pour recevoir les CFP et envoyer des propositions
        addBehaviour(new CyclicBehaviour() {
            public void action() {
                // Reçoit un message de type CFP (Call for Proposal)
                ACLMessage msg = myAgent.receive(MessageTemplate.MatchPerformative(ACLMessage.CFP));
                if (msg != null) {
                    // Convertit le contenu du message en entier (prix courant)
                    int currentPrice = Integer.parseInt(msg.getContent());
                    if (currentPrice < maxPrice) {
                        // Calcule une nouvelle offre en augmentant le prix courant de 1 à 20
                        int newOffer = currentPrice + rand.nextInt(20) + 1;
                        ACLMessage reply = msg.createReply();  // Crée une réponse
                        reply.setPerformative(ACLMessage.PROPOSE);  // Définit le type de message comme PROPOSE
                        reply.setContent(String.valueOf(newOffer));  // Ajoute la nouvelle offre au message
                        myAgent.send(reply);  // Envoie le message
                        // Affiche l'offre faite par cet acheteur
                        System.out.println(getAID().getName() + " offers " + newOffer);
                    }
                } else {
                    block();  // Bloque jusqu'à ce qu'un nouveau message soit reçu
                }
            }
        });
    }
}
