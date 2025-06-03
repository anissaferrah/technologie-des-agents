
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

public class SellerAgent extends Agent {
    private int startPrice = 10;
    private int reservePrice = 50;

    protected void setup() {
        System.out.println("Seller agent " + getAID().getName() + " is ready.");

        addBehaviour(new OneShotBehaviour() {
            public void action() {
                ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
                cfp.setContent(String.valueOf(startPrice));
                cfp.addReceiver(new AID("auction-manager", AID.ISLOCALNAME));
                send(cfp);
                System.out.println("Seller: Starting auction with start price " + startPrice);
            }
        });
    }

    protected void takeDown() {
        System.out.println("Seller agent " + getAID().getName() + " terminating.");
    }
}
