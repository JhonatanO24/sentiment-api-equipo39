package com.hackaton.sentiment.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SentimentResponseDTO {

    @JsonProperty("prevision")
    private String prediction;

    @JsonProperty("probabilidad")
    private Double probability;
}
