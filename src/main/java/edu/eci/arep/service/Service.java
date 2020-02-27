package edu.eci.arep.service;

import edu.eci.arep.request.RequestServer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author eduardo.ocampo
 */

public class Service {

    private static ExecutorService pool = Executors.newFixedThreadPool(10);

    public static void main( String[] args ) throws Exception{

        RequestServer requestServer=new RequestServer();
        requestServer.initializer();
        pool.submit(requestServer);
    }
}
