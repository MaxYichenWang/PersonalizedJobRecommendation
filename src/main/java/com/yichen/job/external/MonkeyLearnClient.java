package com.yichen.job.external;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yichen.job.entity.ExtractRequestBody;
import com.yichen.job.entity.ExtractResponseItem;
import com.yichen.job.entity.Extraction;
import org.apache.http.HttpEntity;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

public class MonkeyLearnClient {
    // KEY EXTRACTION POST API address and User Personal Token
    private static final String EXTRACT_URL =
            "https://api.monkeylearn.com/v3/extractors/ex_YCya9nrn/extract/";
    private static final String AUTH_TOKEN = "80e31c44d2fb3f7f99f6a47a183e057c9a82cd8d";

    // OUTPUT: List<Set<String>, each article returns a set of keywords, use set because we don't care about order of keywords
    // INPUT: List<String>
    public List<Set<String>> extract(List<String> articles) {
        // Create new Jackson ObjectMapper for json implementation
        ObjectMapper mapper = new ObjectMapper();
        // Create new Http client
        CloseableHttpClient httpClient = HttpClients.createDefault();

        // implement POST request
        HttpPost request = new HttpPost(EXTRACT_URL);
        // set POST request header according to MonkeyLearn API
        request.setHeader("Content-Type", "application/json");
        request.setHeader("Authorization", "Token " + AUTH_TOKEN);
        // set POST request body
        ExtractRequestBody body = new ExtractRequestBody(articles, 3);

        // create a new json request body
        String jsonBody;

        // user mapper to create json body
        try {
            jsonBody = mapper.writeValueAsString(body);
        } catch (JsonProcessingException e) {
            return Collections.emptyList();
        }

        // set entity of json body
        try {
            request.setEntity(new StringEntity(jsonBody));
        } catch (UnsupportedEncodingException e) {
            return Collections.emptyList();
        }

        // implement ResponseHandler
        ResponseHandler<List<Set<String>>> responseHandler = response -> {
            if (response.getStatusLine().getStatusCode() != 200) {
                return Collections.emptyList();
            }
            // get entity from response
            HttpEntity entity = response.getEntity();
            if (entity == null) {
                return Collections.emptyList();
            }
            // return entity's content as ExtractResponseItem[]
            ExtractResponseItem[] results = mapper.readValue(entity.getContent(), ExtractResponseItem[].class);

            // put each keyword to final result
            List<Set<String>> keywordList = new ArrayList<>();

            // iterate over each keyword in results
            for (ExtractResponseItem result : results) {
                Set<String> keywords = new HashSet<>();
                for (Extraction extraction : result.extractions) {
                    keywords.add(extraction.parsedValue);
                }
                keywordList.add(keywords);
            }
            return keywordList;
        };

        try {
            return httpClient.execute(request, responseHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    // Test Case
    public static void main(String[] args) {
        List<String> articles = Arrays.asList("Elon Musk has shared a photo of the spacesuit designed by SpaceX. This is the second image shared of the new design and the first to feature the spacesuit’s full-body look.",
                "Former Auburn University football coach Tommy Tuberville defeated ex-US Attorney General Jeff Sessions in Tuesday nights runoff for the Republican nomination for the U.S. Senate. ",
                "The NEOWISE comet has been delighting skygazers around the world this month – with photographers turning their lenses upward and capturing it above landmarks across the Northern Hemisphere."
        );
        MonkeyLearnClient client = new MonkeyLearnClient();
        List<Set<String>> keywordsList = client.extract(articles);
        System.out.println(keywordsList);
    }
}
