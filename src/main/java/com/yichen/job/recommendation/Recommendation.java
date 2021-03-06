package com.yichen.job.recommendation;

import com.yichen.job.db.MySQLConnection;
import com.yichen.job.entity.Item;
import com.yichen.job.external.GitHubClient;

import java.util.*;

// give user some recommendations
public class Recommendation {

    // given user_id and location and return recommended items
    public List<Item> recommendItems(String userId, double lat, double lon) {
        List<Item> recommendedItems = new ArrayList<>();

        // Step 1, get all favorite item_ids
        MySQLConnection connection = new MySQLConnection();
        Set<String> favoriteItemIds = connection.getFavoriteItemIds(userId);

        // Step 2, get all keywords, sort by count
        Map<String, Integer> allKeywords = new HashMap<>();
        for (String itemId : favoriteItemIds) {
            // get keywords by item id
            Set<String> keywords = connection.getKeywords(itemId);
            for (String keyword: keywords) {
                // add keywords to map, and count++
                allKeywords.put(keyword, allKeywords.getOrDefault(keyword,0) + 1);
            }
        }
        connection.close();

        // sort by count of keywords and save in a List
        List<Map.Entry<String, Integer>> keywordList = new ArrayList<>(allKeywords.entrySet());
        keywordList.sort(new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return o1.getValue().compareTo(o2.getValue());
            }
        });

        // cut down search list, only keep top 3
        if (keywordList.size() > 3) {
            keywordList = keywordList.subList(0, 3);
        }

        // Step 3, search item based on top 3 keywords, filter out favorite items

        // for dedup
        Set<String> visitedItems = new HashSet<>();

        GitHubClient client = new GitHubClient();
        for (Map.Entry<String, Integer> keyword : keywordList) {

            // search on GitHub based the given lat, lon and keyword
            List<Item> items = client.search(lat, lon, keyword.getKey());

            for (Item item : items) {
                // if item not in the favorite && not in the visited
                if (!favoriteItemIds.contains(item.getId()) && !visitedItems.contains(item.getId())) {
                    recommendedItems.add(item);
                    visitedItems.add(item.getId());
                }
            }
        }
        return recommendedItems;
    }
}
