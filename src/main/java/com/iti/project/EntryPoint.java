package com.iti.project;

import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;

public class EntryPoint {
    private static Logger logger = LoggerFactory.getLogger(EntryPoint.class);
    public static void main(String[] args) {
        BasicConfigurator.configure();
        //System.out.println("Hello MK!");
        logger.info("I have started!");
        GameServer gameServer= new GameServer();
        // Let us create task that is going to
        // wait for four threads before it starts
        CountDownLatch latch = new CountDownLatch(1);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> { // listens to ctrl+C events that shuts down the server
           logger.info("Shutdown Hook is running !");
            gameServer.interrupt();
            try {
                Thread.sleep(2000); // TODO: Ideally we would do long polling to check if the main server has indeed shutdown and then after a specified timeOut
                // If it has not shutdown still we would force exit
            }
            catch (InterruptedException ex)
            {

            }
            finally {
                logger.info("Main server is shutting down!");
            }
            latch.countDown(); // signal to the main thread that we have shutdown successfully

        }));

        try {
            latch.await();
        } catch (InterruptedException e) {
            logger.error("Caught error while waiting for the program to shutdown, error: {} and stackTrace: {}",e.getMessage(), e.getStackTrace());
        }
        // main program exits

    }


}
