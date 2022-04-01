package com.example;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private ObjectOutputStream outObjStream;
    private ObjectInputStream inObjStream;

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            // if no args given, exit system
            System.err.println("Usage: java Server <port number>");
            System.exit(1);
        }
        try {
            int port = Integer.parseInt(args[0]);
            Server server = new Server(); // initialise server
            server.start(port); // run start with given port
        } catch (NumberFormatException e) {
            // port number must be an int
            System.err.println("Usage: invalid port number: " + args[0]);
            System.exit(1);
        }

    }

    public void start(int port) throws Exception {
        System.out.println("Server started. Listening on Port " + port);
        ExecutorService executor = null;

        try (ServerSocket serverSocket = new ServerSocket(port);) { // create end-point socket with given port number
            executor = Executors.newFixedThreadPool(5); // create pool of threads for handling multiple tasks
            System.out.println("Waiting for clients");
            while (true) {
                Socket clientSocket = serverSocket.accept(); // waiting for client to join and accept
                Runnable worker = new RequestHandler(clientSocket); // initialise request handler with client
                executor.execute(worker); // send client to different thread
            }
        } catch (IOException e) {
            // error connecting or during connection of client
            System.out.println("Exception caught when trying to listen on port "
                    + port + " or listening for a connection");
            System.out.println(e.getMessage());
        } finally {
            if (executor != null) {
                executor.shutdown(); // shut down multiple threads pool
            }
        }

    }

    public void stop() throws Exception {
        // shutdown server and close all streams and sockets
        inObjStream.close();
        outObjStream.close();
        clientSocket.close();
        serverSocket.close();
        System.out.println("Server stopped");
    }
}
