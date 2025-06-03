import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

public class AuctionMain {
    public static void main(String[] args) {
        Runtime rt = Runtime.instance();
        Profile p = new ProfileImpl();
        p.setParameter(Profile.GUI, "true"); // Activer l'interface graphique JADE

        AgentContainer mainContainer = rt.createMainContainer(p);

        try {
            // Créez et démarrez l'agent vendeur
            AgentController seller = mainContainer.createNewAgent("seller", Selleragent.class.getName(), null);
            seller.start();

            // Créez et démarrez les agents acheteurs
            for (int i = 0; i < 3; i++) {
                AgentController buyer = mainContainer.createNewAgent("buyer" + i, BuyerAgent.class.getName(), null);
                buyer.start();
            }

            // Créez une instance de l'interface d'enchères
           // SellerAgent sellerAgent = new SellerAgent();
           // AuctionInterface auctionInterface = new AuctionInterface(sellerAgent);

            // Rendez l'interface d'enchères visible
          //  auctionInterface.setVisible(true);
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
    }
}
