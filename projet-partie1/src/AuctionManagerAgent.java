
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.HashMap;
import java.util.Map;

public class AuctionManagerAgent extends Agent {
    private Map<AID, Integer> bids = new HashMap<>();
    private int currentHighestBid = 0;
    private boolean auctionInProgress = true;

    protected void setup() {
        System.out.println("Auction manager agent " + getAID().getName() + " is ready.");

        addBehaviour(new CyclicBehaviour() {
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    if (msg.getPerformative() == ACLMessage.CFP) {
                        currentHighestBid = Integer.parseInt(msg.getContent());
                        notifyAllBuyers();
                    } else if (msg.getPerformative() == ACLMessage.PROPOSE) {
                        AID buyer = msg.getSender();
                        int bid = Integer.parseInt(msg.getContent());
                        bids.put(buyer, bid);
                        currentHighestBid = Math.max(currentHighestBid, bid);
                        notifyAllBuyers();
                    }
                } else {
                    block();
                }
            }

            private void notifyAllBuyers() {
                if (auctionInProgress) {
                    ACLMessage bidUpdate = new ACLMessage(ACLMessage.INFORM);
                    bidUpdate.setContent(String.valueOf(currentHighestBid));
                    for (AID buyer : bids.keySet()) {
                        bidUpdate.addReceiver(buyer);
                    }
                    send(bidUpdate);
                }
            }
        });
    }

    protected void takeDown() {
        System.out.println("Auction manager agent " + getAID().getName() + " terminating.");
    }
}
