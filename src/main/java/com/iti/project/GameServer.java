package com.iti.project;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class GameServer {

    private static Logger logger = LoggerFactory.getLogger(GameServer.class);
    public static final int PORT = 5000;
    private  final Thread listenerThread; // main thread to listen on socket connect requests
    private volatile boolean isInterrupted = false; // will check if the client is trying to shutdown the program
    private static final List<String> users = new ArrayList<>(); // Always prefer to use the abstract interface in the definition
    private List<GameHandler> handlers = new ArrayList<>();

    static {
        users.add("foo");
        users.add("bar");
        users.add("baz");
        users.add("dav");
        users.add("jak");
    }

    public GameServer() {
        listenerThread = new Thread(()->{
            mainEvenLoop();
        });
        listenerThread.start();
    }

    private void mainEvenLoop()
    {
        logger.info("Starting Game server...........");
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            logger.info("accepting socket connections on PORT {}",PORT);
            logger.info("Waiting for clients to connect");
            while(!isInterrupted) { // keep executing the program if the user has not shutdown
                Socket s = serverSocket.accept();
                System.out.println("Client accepted");
                GameHandler handler= new GameHandler(s); // we add all the handlers that we have started to keep track of them and also shut them down if necessary
                handlers.add(handler);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
        finally {
            logger.info("Server shutting down.....");
        }

    }
    public void interrupt()
    {
        logger.info("Main server received the shutdown command ........");
        // will go over each GameHandler thread and tell it to shutdown
        handlers.forEach(handler -> handler.interruptCustom());
        this.isInterrupted=true; // stop the main thread from accepting new threads
    }
    public static List<String> getUsers(){
        return users;
    }
}
