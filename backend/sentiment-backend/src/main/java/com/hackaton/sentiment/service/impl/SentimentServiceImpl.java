package com.hackaton.sentiment.service.impl;

import com.hackaton.sentiment.client.SentimentMlClient;
import com.hackaton.sentiment.dto.request.SentimentRequestDTO;
import com.hackaton.sentiment.dto.response.SentimentResponseDTO;
import com.hackaton.sentiment.dto.response.SentimentStatsResponseDTO;
import com.hackaton.sentiment.entity.SentimentAnalysis;
import com.hackaton.sentiment.repository.SentimentAnalysisRepository;
import com.hackaton.sentiment.service.SentimentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SentimentServiceImpl implements SentimentService {

    private final SentimentMlClient mlClient;
    private final SentimentAnalysisRepository repository;

    @Override
    public SentimentResponseDTO analyzeSentiment(SentimentRequestDTO request) {
        SentimentResponseDTO mlResponse = mlClient.predict(request.getText());

        SentimentAnalysis analysis = SentimentAnalysis.builder()
                .text(request.getText())
                .label(mlResponse.getPrediction())
                .probability(mlResponse.getProbability())
                .build();

        repository.save(analysis);
        return mlResponse;
    }

    @Override
    public SentimentStatsResponseDTO getStats() {
        long total = repository.count();
        long positive = repository.countByLabel("Positivo");
        long negative = repository.countByLabel("Negativo");
        long neutral = repository.countByLabel("Neutro");

        return SentimentStatsResponseDTO.builder()
                .total(total)
                .positive(positive)
                .negative(negative)
                .neutral(neutral)
                .build();
    }
}