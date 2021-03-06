package com.yichen.job.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

// implement request body which will be sent to the MonkeyLearn KeywordsExtraction API
public class ExtractRequestBody {

    public List<String> data;

    // if field name is different from the key defined in the API
    // add Jackson annotation
    @JsonProperty("max_keywords")
    public int maxKeywords;

    public ExtractRequestBody(List<String> data, int maxKeywords) {
        this.data = data;
        this.maxKeywords = maxKeywords;
    }
}
