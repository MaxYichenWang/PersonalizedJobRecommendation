package com.yichen.job.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

// implement extraction class which the fields matches the response from MonkeyLearn
@JsonIgnoreProperties(ignoreUnknown = true)
public class Extraction {

    // if field name is different from the key defined in the API
    // add Jackson annotation
    @JsonProperty("tag_name")
    public String tagName;

    @JsonProperty("parsed_value")
    public String parsedValue;

    public int count;

    public String relevance;

}
