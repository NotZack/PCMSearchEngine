package UIConnection;

import Indexing.FileIndex;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class UISocket {

    private static final int SOCKET_PORT = 5119;

    private PrintWriter out;
    private BufferedReader in;

    /**
     * Opens a new socket used to communicate with the user interface on the constant port.
     */
    public void openSocket() {
        try {
            System.out.println("Opening socket on port " + SOCKET_PORT);
            ServerSocket serverSocket = new ServerSocket(SOCKET_PORT);
            System.out.println("New socket opened! Awaiting connection ...");

            acceptConnection(serverSocket);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Accepts any pending socket connection request.
     * @param serverSocket The server socket to accept requests to
     */
    private void acceptConnection(@NotNull ServerSocket serverSocket) {
        try {
            Socket clientSocket = serverSocket.accept();
            clientSocket.setTcpNoDelay(true);
            out = new PrintWriter(new BufferedOutputStream(clientSocket.getOutputStream()), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            System.out.println("Client connected");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Cleans and collects any data sent from the connected user then makes a query out of it.
     * @param fileIndex The lucene index to send queries to
     */
    public void openCommunication(FileIndex fileIndex) {
        try {
            String clientQuery;
            while ((clientQuery = in.readLine()) != null) {
                //Checks for an exact query request
                System.out.println(clientQuery);
                if (clientQuery.startsWith("Exact_Query:")) {
                    String exactPath = fileIndex.parseExactQuery(clientQuery.substring(clientQuery.indexOf(":") + 1));
                    System.out.println(exactPath);
                    out.println(exactPath);
                    File file = new File(fileIndex.getBaseDirectory() + "/" + exactPath);


                    Desktop desktop = Desktop.getDesktop();
                    if(file.exists()) {
                        try {
                            desktop.open(file);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }
                else if (clientQuery.equals("Base_path_directory_request")) {
                    out.println(fileIndex.getBaseDirectory());
                }
                //Executes an inexact query request
                else {
                    clientQuery = clientQuery.replaceAll(" +", " ");
                    if (clientQuery.length() > 0 && !clientQuery.equals(" ")) {
                        if (clientQuery.contains("Folder_names: [")) {
                            // Split on the cleaned trailing string array from the client query
                            String[] folderNames = clientQuery.substring(clientQuery.indexOf("Folder_names: [") + 15).
                                    replaceAll("'", "").replaceAll(", ", ",").split(",");

                            // Remove the last character from the last string, its a ]
                            folderNames[folderNames.length - 1] = folderNames[folderNames.length - 1].substring(0, folderNames[folderNames.length - 1].length() - 1);
                            out.println(
                                fileIndex.parseInexactQuery(
                                    clientQuery.substring(0, clientQuery.indexOf("Folder_names: [")), folderNames
                                )
                            );
                        }
                        else {
                            out.println(fileIndex.parseInexactQuery(clientQuery, (String[]) null));
                        }
                    }
                    else {
                        out.println("Invalid query");
                    }
                }
            }
        }
        catch (IOException e) {
            System.out.println(e + " and/or client disconnected");
            openSocket();
        }
    }
}
