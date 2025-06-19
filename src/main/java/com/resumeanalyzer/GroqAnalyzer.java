package com.resumeanalyzer;

import okhttp3.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;

public class GroqAnalyzer {
    private static final String GROQ_API_URL = "https://api.groq.com/openai/v1/chat/completions";
    private static final MediaType JSON = MediaType.get("application/json");
    private final OkHttpClient client;
    private final String apiKey;
    private final ObjectMapper objectMapper;

    public GroqAnalyzer(String apiKey) {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new IllegalArgumentException("Groq API key cannot be null or empty");
        }
        this.apiKey = apiKey;
        this.client = new OkHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public String analyzeResume(String resumeText, String jobRole) throws IOException {
        String prompt = buildPrompt(resumeText, jobRole);
        
        ObjectNode requestBody = objectMapper.createObjectNode();
        requestBody.put("model", "llama-3.3-70b-versatile");
        requestBody.putArray("messages")
            .add(objectMapper.createObjectNode()
                .put("role", "system")
                .put("content", "You are an expert resume analyzer and career counselor. Analyze the resume for the specified job role and provide detailed, actionable feedback."))
            .add(objectMapper.createObjectNode()
                .put("role", "user")
                .put("content", prompt));

        Request request = new Request.Builder()
            .url(GROQ_API_URL)
            .addHeader("Authorization", "Bearer " + apiKey)
            .addHeader("Content-Type", "application/json")
            .post(RequestBody.create(requestBody.toString(), JSON))
            .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "No error details";
                throw new IOException("API request failed with code " + response.code() + ": " + errorBody);
            }

            String responseBody = response.body().string();
            return extractAnalysis(responseBody);
        }
    }

    private String buildPrompt(String resumeText, String jobRole) {
        return String.format("""
            Please analyze this resume for a %s position. Provide a comprehensive analysis including:
            1. Overall assessment of the resume's strength and suitability for the role
            2. Key qualifications and achievements that stand out
            3. Critical skills or experiences that are missing for this role
            4. Specific suggestions for improvement
            5. Format and presentation feedback
            6. Industry-specific recommendations
            
            Resume text:
            %s
            
            Please provide detailed, actionable feedback that will help improve the resume for this specific role.
            Format your response in clear sections with bullet points for better readability.
            """, jobRole, resumeText);
    }

    private String extractAnalysis(String responseJson) throws IOException {
        try {
            ObjectNode root = (ObjectNode) objectMapper.readTree(responseJson);
            return root.path("choices")
                      .path(0)
                      .path("message")
                      .path("content")
                      .asText();
        } catch (Exception e) {
            throw new IOException("Failed to parse API response: " + e.getMessage());
        }
    }
} 