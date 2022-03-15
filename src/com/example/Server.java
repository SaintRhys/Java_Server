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

    public static final int PORT = 8005;

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.err.println("Usage: java Server <port number>");
            System.exit(1);
        }
        try {
            int port = Integer.parseInt(args[0]);
            Server server = new Server();
            server.start(port);
        } catch (NumberFormatException e) {
            System.err.println("Usage: invalid port number: " + args[0]);
            System.exit(1);
        }

    }

    // public Server() throws Exception {
    // ServerSocket serverSocket = new ServerSocket(PORT);
    // System.out.println("Server is up and running on port: " + PORT);
    // Socket socket = serverSocket.accept();

    // // input/output streams
    // ObjectOutputStream outStream = new
    // ObjectOutputStream(socket.getOutputStream());
    // ObjectInputStream inStream = new ObjectInputStream(socket.getInputStream());

    // Packet recvPacket = (Packet) inStream.readObject();
    // System.out.println(recvPacket);

    // if (recvPacket.message.equals("Hello!")) {
    // Packet packet = new Packet("Hi! - From the server :)");
    // outStream.writeObject(packet);
    // }

    // serverSocket.close();
    // }

    public void start(int port) throws Exception {
        System.out.println("Server started. Listening on Port " + port);
        ExecutorService executor = null;

        try (ServerSocket serverSocket = new ServerSocket(port);) {
            executor = Executors.newFixedThreadPool(5);
            System.out.println("Waiting for clients");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                Runnable worker = new RequestHandler(clientSocket);
                executor.execute(worker);
            }
        } catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port "
                    + port + " or listening for a connection");
            System.out.println(e.getMessage());
        } finally {
            if (executor != null) {
                executor.shutdown();
            }
        }

    }

    public void stop() throws Exception {
        inObjStream.close();
        outObjStream.close();
        clientSocket.close();
        serverSocket.close();
        System.out.println("Server stopped");
    }
}
