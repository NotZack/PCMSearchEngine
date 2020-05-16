package UIConnection;

import Indexing.FileIndex;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

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
                if (clientQuery.startsWith("Exact_Query:")) {
                    out.println(fileIndex.parseExactQuery(clientQuery.substring(clientQuery.indexOf(":") + 1)));
                }
                //Executes an inexact query request
                else {
                    clientQuery = clientQuery.replaceAll(" +", " ");
                    if (clientQuery.length() > 0 && !clientQuery.equals(" ")) {
                        out.println(fileIndex.parseInexactQuery(clientQuery, null));
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

        Scanner scnr = new Scanner(System.in);
        System.out.println(fileIndex.parseInexactQuery(scnr.nextLine(), ""));
        File file = new File(
            fileIndex.getBaseDirectory() + "/" + fileIndex.parseExactQuery("bird feeder plans")
        );

        Desktop desktop = Desktop.getDesktop();
        if(file.exists()) {
            try {
                desktop.open(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
