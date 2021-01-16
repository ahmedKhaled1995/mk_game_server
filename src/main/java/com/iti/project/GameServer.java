package com.iti.project;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class GameServer {

    public static final int PORT = 5000;

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
            while(true) {
                Socket s = serverSocket.accept();
                System.out.println("Client accepted");
                new GameHandler(s);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    public static ArrayList<String> getUsers(){
        return users;
    }
}
