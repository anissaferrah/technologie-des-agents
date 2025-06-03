
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.Random;

public class BuyerAgent extends Agent {
    private int maxBid;
    private Random rand;

    protected void setup() {
        rand = new Random();
        maxBid = rand.nextInt(100) + 20;

        System.out.println("Buyer agent " + getAID().getName() + " is ready with max bid " + maxBid);

        addBehaviour(new CyclicBehaviour() {
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    int currentHighestBid = Integer.parseInt(msg.getContent());
                    System.out.println(getAID().getName() + ": Received current highest bid: " + currentHighestBid);

                    if (currentHighestBid < maxBid && rand.nextBoolean()) {
                        int newBid = currentHighestBid + rand.nextInt(10) + 1;
                        ACLMessage bidMsg = new ACLMessage(ACLMessage.PROPOSE);
                        bidMsg.setContent(String.valueOf(newBid));
                        bidMsg.addReceiver(new AID("auction-manager", AID.ISLOCALNAME));
                        send(bidMsg);
                        System.out.println(getAID().getName() + ": Bidding " + newBid);
                    }
                } else {
                    block();
                }
            }
        });
    }

    protected void takeDown() {
        System.out.println("Buyer agent " + getAID().getName() + " terminating.");
    }
}
