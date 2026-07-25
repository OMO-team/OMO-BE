package com.omo.backend.domain.aisearch.service;

public interface AiClient {
    <T> T callWithSchema(String prompt, Class<T> responseType);

}
