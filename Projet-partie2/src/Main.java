import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

public class Main {
    public static void main(String[] args) {
        // Obtenez l'instance de l'environnement d'exécution JADE
        Runtime rt = Runtime.instance();
        // Créez le profil du conteneur du vendeur
        Profile pSeller = new ProfileImpl();
      
        pSeller.setParameter(Profile.MAIN_HOST, "localhost"); // Définissez l'hôte principal
        pSeller.setParameter(Profile.MAIN_PORT, "1099"); // Définissez le port principal (Assurez-vous qu'il est correct)
        pSeller.setParameter(Profile.CONTAINER_NAME, "seller-container"); // Définissez le nom du conteneur

        // Créez le profil du conteneur de l'acheteur
        Profile pBuyer = new ProfileImpl();
        
        pBuyer.setParameter(Profile.MAIN_HOST, "localhost"); // Définissez l'hôte principal
        pBuyer.setParameter(Profile.MAIN_PORT, "1099"); // Définissez le port principal (Assurez-vous qu'il est correct)
        pBuyer.setParameter(Profile.CONTAINER_NAME, "buyer-container"); // Définissez le nom du conteneur

        // Créez le conteneur du vendeur
        AgentContainer sellerContainer = rt.createAgentContainer(pSeller);

        // Créez le conteneur de l'acheteur
        AgentContainer buyerContainer = rt.createAgentContainer(pBuyer);

        int numSellers = 3;

        try {
            // Créez les agents vendeurs
            for (int i = 0; i < numSellers; i++) {
                AgentController sellerAgent = sellerContainer.createNewAgent("seller" + i, SellerAgent.class.getName(), null);
                sellerAgent.start(); // Démarrez l'agent vendeur
            }

            // Créez l'agent acheteur
            AgentController buyerAgent = buyerContainer.createNewAgent("buyer", BuyerAgent.class.getName(), null);
            buyerAgent.start(); // Démarrez l'agent acheteur
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
    }
}
