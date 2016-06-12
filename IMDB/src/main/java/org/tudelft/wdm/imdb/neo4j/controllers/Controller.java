package org.tudelft.wdm.imdb.neo4j.controllers;

import org.neo4j.driver.v1.*;

/**
 * @author Tom
 * 
 * Controller for Neo4j movie API
 */
public class Controller {
    
    private static final String ADDRESS = "bolt://localhost";
    private static final String USERNAME = "neo4j";
    private static final String PASSWORD = "1234";
    
    private static Driver driver = null;
    private static Session session = null;
    
    private static boolean keepOpen = false;
    
    public static void establishConnection(){
        if (isConnected()) return;
        else closeConnection();
        driver = GraphDatabase.driver(ADDRESS, AuthTokens.basic(USERNAME, PASSWORD));
        session = driver.session();
    }
    
    public static void closeConnection(){
        if (session!=null && (!keepOpen || !session.isOpen())){
            session.close();
            session = null;
            driver.close();
            driver = null;
        }
    }
    
    public static StatementResult query(Statement q){
        establishConnection();
        StatementResult result = session.run(q);
        return result;
    }
    
    public static void keepOpen(){
        establishConnection();
        keepOpen = true;
    }
    
    public static void forceClose(){
        keepOpen = false;
        closeConnection();
    }
    
    public static boolean isConnected(){
        return session!=null && session.isOpen();
    }
}
