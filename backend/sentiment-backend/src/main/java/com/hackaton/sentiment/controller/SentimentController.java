package com.hackaton.sentiment.controller;

import com.hackaton.sentiment.dto.request.SentimentRequestDTO;
import com.hackaton.sentiment.dto.response.SentimentResponseDTO;
import com.hackaton.sentiment.dto.response.SentimentStatsResponseDTO;
import com.hackaton.sentiment.service.SentimentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sentiment")
@RequiredArgsConstructor
public class SentimentController {

    private final SentimentService sentimentService;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public SentimentResponseDTO analyze(
            @Valid @RequestBody SentimentRequestDTO request) {

        return sentimentService.analyzeSentiment(request);
    }

    @GetMapping("/stats")
    public SentimentStatsResponseDTO stats() {
        return sentimentService.getStats();
    }
}
