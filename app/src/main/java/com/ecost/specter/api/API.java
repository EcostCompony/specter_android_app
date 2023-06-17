package com.ecost.specter.api;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Callable;

public class API implements Callable<Response> {

    private final String[] strings;

    public API(String... strings) {
        this.strings = strings;
        this.strings[0] = this.strings[0].replaceAll("\n", "%0A");
    }

    @Override
    public Response call() throws IOException {
        HttpURLConnection connection = null;

        try {
            URL url = new URL(strings[0]);
            connection = (HttpURLConnection) url.openConnection();
            if (strings.length == 2) connection.setRequestProperty("Authorization", strings[1]);
            connection.connect();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder stringBuilder = new StringBuilder();

            String line;
            while ((line = bufferedReader.readLine()) != null) stringBuilder.append(line);
            bufferedReader.close();

            return new ObjectMapper().readValue(stringBuilder.toString(), Response.class);
        } finally {
            if (connection != null) connection.disconnect();
        }
    }

}