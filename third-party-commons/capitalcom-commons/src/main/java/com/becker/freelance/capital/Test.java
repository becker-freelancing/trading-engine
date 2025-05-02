package com.becker.freelance.capital;


import com.becker.freelance.capital.util.HttpRequestBuilder;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Test {

    public static void main(String[] args) throws Exception {
        HttpRequest request = HttpRequestBuilder.builder()
                .header("Content-Type", "application/json")
                .uri(new URI("https://demo-api-capital.backend-capital.com/api/v1/accounts/topUp"))
                .POST(HttpRequest.BodyPublishers.ofString("{\"amount\":-12890.42}"))
                .build();

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println(response.body());
    }

}
