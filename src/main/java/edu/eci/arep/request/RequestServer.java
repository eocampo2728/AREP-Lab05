/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arep.request;

import edu.eci.arep.annotations.Web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

/**
 * This acts as a server that manage requests
 * @author eduardo.ocampo
 */
public class RequestServer implements Runnable{

    /**
     * This method initialize the service
     * @throws IOException
     */
    public void initializer() throws IOException {

        int port = getPort();

        while (true) {

            ServerSocket serverSocket = null;
            try {
                serverSocket = new ServerSocket(port);
            } catch (IOException e) {
                System.err.println("Cant enter to port: " + Integer.toString(port));
                System.exit(1);
            }
            Socket clientSocket = null;
            try {
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                System.err.println("Accept failed.");
                System.exit(1);
            }
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            clientSocket.getInputStream()));
            String inputLine;
            inputLine = in.readLine();

            try {
                String[] URL = inputLine.split(" ");
                if (URL[1].contains("/WebService")) {
                    String [] list = split(URL);
                    Class<?> c = Class.forName("edu.eci.arep.webService." + list[0]);
                    for (Method method : c.getMethods()) {
                        if (method.isAnnotationPresent(Web.class)) {
                            if (method.getName().equals(list[2])) {
                                method.invoke(c, "/src/main/resources/" + list[1], clientSocket.getOutputStream());
                            }
                            if (!in.ready()) {
                                break;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            out.close();
            in.close();
            clientSocket.close();
            serverSocket.close();
        }
    }

    static int getPort(){
        if (System.getenv("PORT") != null) {
            return Integer.parseInt(System.getenv("PORT"));
        }
        return 4567;
    }

    @Override
    public void run() {

    }

    /**
     * Private method that split a list
     * @param list list that the inicialicer need to split
     * @return list organised with the format
     */
    private String[] split(String[] list){
        String[] res = list[1].split("/");
        String[] ans = res[2].split("[, ?.@]+");
        String[] ret = {res[1],res[2],ans[1]};
        return ret;
    }
}
