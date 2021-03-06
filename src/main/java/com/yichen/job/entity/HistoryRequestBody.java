package com.yichen.job.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

// POJO for request favorite servlet
public class HistoryRequestBody {

    @JsonProperty("user_id")
    public String userId;

    public Item favorite;
}
