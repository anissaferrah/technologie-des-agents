import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

public class MainPlatform {
    public static void main(String[] args) {
        // Démarrer le runtime JADE
        Runtime rt = Runtime.instance();

        // Profil pour la première plateforme (localhost)
        Profile p1 = new ProfileImpl();
        p1.setParameter(Profile.GUI, "true"); // Activer l'interface graphique Jade Profiler
        p1.setParameter(Profile.MAIN_HOST, "localhost"); // Utiliser localhost pour la première plateforme
        p1.setParameter(Profile.MAIN_PORT, "1099");
        p1.setParameter(Profile.CONTAINER_NAME, "container1");

        // Profil pour la deuxième plateforme (192.168.133.1)
        Profile p2 = new ProfileImpl();
        p2.setParameter(Profile.GUI, "true"); // Activer l'interface graphique Jade Profiler
        p2.setParameter(Profile.MAIN_HOST, "192.168.133.1"); // IP de la deuxième plateforme
        p2.setParameter(Profile.MAIN_PORT, "1099");
        p2.setParameter(Profile.CONTAINER_NAME, "container2");

        try {
            // Créer les conteneurs pour les plateformes
            AgentContainer container1 = rt.createMainContainer(p1);
            AgentContainer container2 = rt.createMainContainer(p2);

            // Ajouter l'agent Buyer à la première plateforme
            AgentController buyerAgent1 = container1.createNewAgent("buyer1", BuyerAgentPlatform.class.getName(), null);
            buyerAgent1.start();
            System.out.println("Agent Buyer créé et démarré sur container1.");

            // Ajouter les agents Seller à la deuxième plateforme
            AgentController sellerAgent1 = container2.createNewAgent("seller1", SellerAgent2.class.getName(), null);
            sellerAgent1.start();
            System.out.println("Agent Seller1 créé et démarré sur container2.");

            AgentController sellerAgent2 = container2.createNewAgent("seller2", SellerAgent2.class.getName(), null);
            sellerAgent2.start();
            System.out.println("Agent Seller2 créé et démarré sur container2.");
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
    }
}
