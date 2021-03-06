package com.yichen.job.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

// implement a list of Extraction
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExtractResponseItem {

    public List<Extraction> extractions;

}
