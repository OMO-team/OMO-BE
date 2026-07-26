package com.omo.backend.domain.aisearch.service;

import com.google.genai.types.HttpOptions;
import jakarta.annotation.PostConstruct;
import tools.jackson.databind.ObjectMapper;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import com.omo.backend.domain.aisearch.exception.AiSearchErrorCode;
import com.omo.backend.global.apiPayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class GeminiClient implements AiClient {

    private final ObjectMapper objectMapper;

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.model:gemini-3.1-flash-lite}")
    private String model;

    private static final int MAX_RETRY = 1;
    private static final int TIMEOUT_MS = 60_000;
    private Client client;

    @PostConstruct
    private void init() {
        this.client = Client.builder()
                .apiKey(apiKey)
                .httpOptions(HttpOptions.builder()
                        .timeout(TIMEOUT_MS)
                        .build())
                .build();
    }

    @Override
    public <T> T callWithSchema(String prompt, Class<T> responseType) {
        return callWithSchema(prompt, GeminiSchemas.get(responseType), responseType);
    }

    @Override
    public <T> T callWithSchema(String prompt, Map<String, Object> schema, Class<T> responseType) {
        GenerateContentConfig config = GenerateContentConfig.builder()
                .responseMimeType("application/json")
                .responseJsonSchema(schema)
                .build();

        Exception lastException = null;
        for (int attempt = 0; attempt <= MAX_RETRY; attempt++) {
            try {
                GenerateContentResponse response = client.models.generateContent(model, prompt, config);
                return objectMapper.readValue(response.text(), responseType);
            } catch (Exception e) {
                lastException = e;
                log.warn("[Gemini Call 실패] attempt={}, type={}, error={}",
                        attempt, responseType.getSimpleName(), e.getMessage());

                if (attempt < MAX_RETRY) {
                    sleepBeforeRetry(attempt);
                }
            }
        }
        log.error("[Gemini Call 오류] type={}", responseType.getSimpleName(), lastException);
        throw new GeneralException(AiSearchErrorCode.AI_ANALYSIS_FAILED);
    }

    private void sleepBeforeRetry(int attempt) {
        try {
            long baseDelayMs = 500L * (attempt + 1);
            long jitterMs = (long) (Math.random() * 200);
            Thread.sleep(baseDelayMs + jitterMs);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }
}