import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

class AuctionMain {
    public static void main(String[] args) {
        Runtime rt = Runtime.instance();
        Profile p = new ProfileImpl();
        AgentContainer mainContainer = rt.createMainContainer(p);

        try {
            AgentController auctionManager = mainContainer.createNewAgent("auction-manager", AuctionManagerAgent.class.getName(), null);
            auctionManager.start();

            AgentController seller = mainContainer.createNewAgent("seller", SellerAgent.class.getName(), null);
            seller.start();

            AgentController buyer1 = mainContainer.createNewAgent("buyer1", BuyerAgent.class.getName(), null);
            buyer1.start();

            AgentController buyer2 = mainContainer.createNewAgent("buyer2", BuyerAgent.class.getName(), null);
            buyer2.start();
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
    }
}
