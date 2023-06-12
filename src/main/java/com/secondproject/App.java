package com.secondproject;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URL;
import java.net.HttpURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class App {
    public static void main(String[] args) throws IOException {
        // Create a new HTTP server on port 1800
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        // Create a context for the weather endpoint
        server.createContext("/weather", new WeatherHandler());

         server.createContext("/health", new HelloHandler());

        // Start the server
        server.start();
        System.out.println("Server started on port 8080");
    }

    // health 


     static class HelloHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // Set the response content type
            exchange.getResponseHeaders().set("Content-Type", "text/plain");

            // Set the response status code
            exchange.sendResponseHeaders(200, 0);

            // Get the response body stream
            OutputStream responseBody = exchange.getResponseBody();

            // Write the response message
            String responseMessage = "Health Status Running..........";
            responseBody.write(responseMessage.getBytes());

            // Close the response body
            responseBody.close();
        }
    }

    // weather

    static class WeatherHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // Set the response content type
            exchange.getResponseHeaders().set("Content-Type", "application/json");

            // Set the response status code
            exchange.sendResponseHeaders(200, 0);

            // Get the response body stream
            OutputStream responseBody = exchange.getResponseBody();

            // Fetch weather data from API
            String apiKey = "c578f0821fa308a5321ca2ee2a22f44b";
            String city = "hyderabad";

            URL url = new URL("https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + apiKey);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuilder response = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            // Parse JSON response
            JSONObject jsonObject = new JSONObject(response.toString());
            String weatherDescription = jsonObject.getJSONArray("weather").getJSONObject(0).getString("description");
            double temperature = jsonObject.getJSONObject("main").getDouble("temp");

            // Create JSON response
            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("city", city); 
            jsonResponse.put("weatherDescription", weatherDescription);
            jsonResponse.put("temperature", temperature);

            // Write the JSON response
            String responseMessage = jsonResponse.toString();
            responseBody.write(responseMessage.getBytes());

            // Close the response body
            responseBody.close();
        }
    }
}
