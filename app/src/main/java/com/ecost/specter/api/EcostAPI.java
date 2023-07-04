package com.ecost.specter.api;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Callable;

public class EcostAPI implements Callable<Response> {

    private final String[] strings;

    public EcostAPI(String... strings) {
        this.strings = strings;
        this.strings[1] = this.strings[1].replaceAll("\n", "%0A");
    }

    @Override
    public Response call() throws IOException {
        HttpURLConnection connection = null;

        try {
            // TODO: ПРИ ПУБЛИКАЦИИ ИЗМЕНИТЬ ПОРТ НА 3500
            URL url = new URL("http://thespecterlife.com:4000/api/auth/method/" + strings[0] + "?v=1.0" + strings[1]);
            connection = (HttpURLConnection) url.openConnection();
            if (strings.length == 3) connection.setRequestProperty("Authorization", strings[2]);
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