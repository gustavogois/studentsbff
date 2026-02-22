package com.studentsbff.ai;

public interface LLMProvider {

    String complete(String systemPrompt, String userMessage);
}
