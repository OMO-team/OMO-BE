package com.omo.backend.domain.aisearch.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
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

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${gemini.api.model:gemini-2.5-flash-lite}")
    private String model;

    private static final int MAX_RETRY = 1;

    private final Client client = new Client();

    @Override
    public <T> T callWithSchema(String prompt, Class<T> responseType) {
        Map<String, Object> schema = GeminiSchemas.get(responseType);

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
            }
        }
        log.error("[Gemini Call 오류] type={}", responseType.getSimpleName(), lastException);
        throw new GeneralException(AiSearchErrorCode.AI_ANALYSIS_FAILED);
    }
}