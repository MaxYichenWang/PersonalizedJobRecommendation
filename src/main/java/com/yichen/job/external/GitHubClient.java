package com.yichen.job.external;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yichen.job.entity.Item;
import org.apache.http.HttpEntity;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

public class GitHubClient {
    // template and default
    private static final String URL_TEMPLATE = "https://jobs.github.com/positions.json?description=%s&lat=%s&long=%s";
    private static final String DEFAULT_KEYWORD = "developer";

    // search jobs from GitHub
    public List<Item> search(double lat, double lon, String keyword) {
        // if keyword not valid, set key as DEFAULT
        if (keyword == null) {
            keyword = DEFAULT_KEYWORD;
        }
        // encode:  eddy yang => eddy%20yang
        try {
            keyword = URLEncoder.encode(keyword, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        // create a url, reformat by replacing parameters in the URL_TEMPLATE with user-defined parameters
        String url = String.format(URL_TEMPLATE, keyword, lat, lon);

        // create a new httpClient to request info from GitHub using self-defined url
        CloseableHttpClient httpClient = HttpClients.createDefault();

        // use a responseHandler to process the response we get from GitHub
        // lambda expression
        ResponseHandler<List<Item>> responseHandler = response -> {
            // if not success (code is not 200)
            if (response.getStatusLine().getStatusCode() != 200) {

                //*** use "Collections.emptyList();" instead of "new ArrayList<>()" ***
                // because we want to return an immutable final empty list, user cannot add anything to it
                // no need to return a new instance every time
                return Collections.emptyList();
            }
            //
            HttpEntity entity = response.getEntity();
            if (entity == null) {
                return Collections.emptyList();
            }

            // create a mapper to process the response we get from GitHub
            ObjectMapper mapper = new ObjectMapper();
            InputStream inputStream = entity.getContent();
//            Item[] items = mapper.readValue(inputStream, Item[].class);
//            return Arrays.asList(items);
            List<Item> items = Arrays.asList(mapper.readValue(entity.getContent(), Item[].class));
            extractKeywords(items);
            return items;
        };

        // request from GitHub using url, get response from GitHub, use responseHandler to
        try {
            return httpClient.execute(new HttpGet(url), responseHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Collections.emptyList();
    }

    // extract keywords in the results from GitHub API
    private void extractKeywords(List<Item> items) {
        MonkeyLearnClient monkeyLearnClient = new MonkeyLearnClient();

        // Method 1, use for loop
        List<String> descriptions1 = new ArrayList<>();
        for (Item item : items) {
            descriptions1.add(item.getDescription());
        }

        // Method 2, use stream()
        List<String> descriptions = items.stream()
                .map(Item::getDescription)
                .collect(Collectors.toList());

        List<Set<String>> keywordList = monkeyLearnClient.extract(descriptions);
        for (int i = 0; i < items.size(); i++) {
            items.get(i).setKeywords(keywordList.get(i));
        }
    }
}
