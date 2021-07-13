package com.geo.meeting;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import com.sun.net.httpserver.HttpServer;

public class MeetingServer {
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8081), 0);
        server.setExecutor(Executors.newCachedThreadPool());
        server.createContext("/meet", exchange -> {
            try {
                System.out.println("Meeting server called");
                Thread.sleep(250L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            exchange.sendResponseHeaders(200, 0);
            exchange.close();
        });
        server.start();
        
    }
}