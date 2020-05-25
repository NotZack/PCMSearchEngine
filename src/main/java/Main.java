import Indexing.Config;
import Indexing.FileIndex;
import UIConnection.UISocket;

/**
 * The entry point for starting the application.
 */
public class Main {

    /**
     * Starts the application. Indexes files and opens communications with user interface
     */
    private void startApp() {
        System.out.println("Working Directory = " + System.getProperty("user.dir"));
        UISocket interfaceSocket = new UISocket();
        interfaceSocket.openSocket();
        interfaceSocket.openCommunication(new FileIndex(Config.readConfigBasePath()));
    }

    /** Entry point into the application. Starts the application.
     * @param args command line arguments
     */
    public static void main(String[] args) {
        Main app = new Main();
        app.startApp();
    }
}
