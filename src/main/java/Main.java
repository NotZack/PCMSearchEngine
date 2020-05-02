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
        UISocket interfaceSocket = new UISocket();
        interfaceSocket.openSocket();
        interfaceSocket.openCommunication(new FileIndex("D:\\MasterIndex\\PlasmaCamFiles"));
    }

    /** Entry point into the application. Starts the application.
     * @param args command line arguments
     */
    public static void main(String[] args) {
        Main app = new Main();
        app.startApp();
    }
}
