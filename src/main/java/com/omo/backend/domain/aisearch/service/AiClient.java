package com.omo.backend.domain.aisearch.service;

import java.util.Map;

public interface AiClient {
    <T> T callWithSchema(String prompt, Class<T> responseType);
    <T> T callWithSchema(String prompt, Map<String, Object> schema, Class<T> responseType);

}
