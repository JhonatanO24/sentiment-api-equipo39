package com.hackaton.sentiment.service;

import com.hackaton.sentiment.dto.request.SentimentRequestDTO;
import com.hackaton.sentiment.dto.response.SentimentResponseDTO;
import com.hackaton.sentiment.dto.response.SentimentStatsResponseDTO;

public interface SentimentService {

    SentimentResponseDTO analyzeSentiment(SentimentRequestDTO request);
    SentimentStatsResponseDTO getStats();
}
