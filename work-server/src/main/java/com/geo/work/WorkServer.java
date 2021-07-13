package com.geo.work;

import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.Executors;

import com.sun.net.httpserver.HttpServer;

public class WorkServer {
    static final String[] TRACE_HEADERS=new String[]{"x-b3-request-id","x-b3-traceid","x-b3-spanid","x-b3-parentspanid","x-b3-sampled","x-b3-flags"};
    public static void main(String[] args) throws IOException {
        String endpoint = System.getenv().getOrDefault("ENDPOINT", "http://localhost:8081/meet");
        String versionText=System.getenv().getOrDefault("VERSION", "v1");
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.setExecutor(Executors.newCachedThreadPool());
        String hostName=InetAddress.getLocalHost().getHostName();
        server.createContext("/work", exchange -> {
            System.out.println("Calling work service");
            int meetings = 0;
            long start = System.currentTimeMillis();
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create(endpoint));
            Arrays.stream(TRACE_HEADERS).map(name->new HeaderEntry(name,exchange.getRequestHeaders().getFirst(name)))
            .filter(header->header.value!=null)
            .forEach(header->builder.setHeader(header.name,header.value));
            

            HttpRequest request = builder.build();
            for (int i = 0; i < 4; i++) {
                try {
                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                    System.out.println("Calling with status "+response.statusCode());
                    if(response.statusCode()==200){
                        meetings++;
                    }
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
             long end=System.currentTimeMillis();    
                String response="Worked for "+(end-start)+"Attended "+meetings+" meetings at"+hostName +" and version "+versionText;
                respondAndCloseExchange(exchange,response);
        });
        server.start();
    }
    private static void respondAndCloseExchange(HttpExchange exchange,String response) throws IOException {
         byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(200, bytes.length);
        OutputStream outputStream = exchange.getResponseBody();
        outputStream.write(bytes);
        outputStream.flush();
        outputStream.close();
        exchange.close();
    }
    private static class HeaderEntry{
        private String name;
        private String value;
        HeaderEntry(String name,String value){
            this.name=name;
            this.value=value;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
        
    }
}