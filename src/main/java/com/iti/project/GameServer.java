package com.iti.project;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class GameServer {

    public static final int PORT = 5000;

    private final AtomicBoolean running = new AtomicBoolean(false);

    private Thread listeningToClientsThread;

    private static final ArrayList<String> users = new ArrayList<>();

    static {
        users.add("foo");
        users.add("bar");
        users.add("baz");
        users.add("dav");
        users.add("jak");
    }

    public GameServer() {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("Server Started");
            System.out.println("Waiting for clients...");

            this.listeningToClientsThread = new Thread(()->{
                this.running.set(true);
                while(this.running.get()) {
                    Socket s = null;
                    try {
                        s = serverSocket.accept();
                        new GameHandler(s);
                        System.out.println("Client accepted");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            this.listeningToClientsThread.start();

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    public static ArrayList<String> getUsers(){
        return users;
    }

    private void stopServer(){
        this.running.set(false);
    }
}
