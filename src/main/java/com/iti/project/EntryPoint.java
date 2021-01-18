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
    }


}
