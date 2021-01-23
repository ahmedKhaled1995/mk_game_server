package com.iti.project;

import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class EntryPoint {

    private static Logger logger = LoggerFactory.getLogger(EntryPoint.class);

    public static void main(String[] args) {
        // To configure the logger
        BasicConfigurator.configure();
        new GameServer();

        /*try {
            String hashed = Password.getSaltedHash("foo");
            boolean check = Password.check("foo", hashed);
            System.out.println(check);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }
}
