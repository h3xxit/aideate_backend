package com.makeathontumai.aideate.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.makeathontumai.aideate.entities.GptRequest;
import com.makeathontumai.aideate.entities.GptMessage;
import com.makeathontumai.aideate.repository.MessageRepository;
import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;

@Service
public class GptService {

    private final MessageRepository messageRepository;

    public GptService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public Integer generateSessionId(){
        return messageRepository.generateSessionId();
    }

    public void douplicateSession(Integer oldSession, Integer newSession){
        List<GptMessage> sessionHistory = messageRepository.getMessagesFromSession(oldSession);
        messageRepository.save(newSession, sessionHistory);
    }

    public String addUserMessage(Integer sessionID, String text){
        GptMessage new_message = new GptMessage("user", text);
        messageRepository.save(sessionID, new_message);
        return text;
    }

    public String addGPTMessage(Integer sessionID, String text){
        GptMessage new_message = new GptMessage("assistant", text);
        messageRepository.save(sessionID, new_message);
        return text;
    }

    public Mono<String> sendTextMessage(@NonNull Integer sessionId, String text, String model) {
        if(model == null)
            model = "gpt-3.5-turbo";
        WebClient client = WebClient.create("https://api.openai.com/v1/chat/");

        ObjectMapper mapper = Jackson2ObjectMapperBuilder.json().build();
        messageRepository.save(sessionId, new GptMessage("user", text));
        List<GptMessage> sessionHistory = messageRepository.getMessagesFromSession(sessionId);
        try
        {
            String body = mapper.writeValueAsString(new GptRequest(model, sessionHistory, 1000));
            return client
                    .post()
                    .uri("completions")
                    .bodyValue(body)
                    .header("Authorization", "Bearer KEY-GOES-HERE")
                    .header("Content-Type", "application/json")
                    .accept(MediaType.APPLICATION_FORM_URLENCODED)
                    .retrieve()
                    .bodyToMono(LinkedHashMap.class)
                    .map(responseBody -> {
                        ArrayList choices = (ArrayList) responseBody.get("choices");
                        LinkedHashMap entry = (LinkedHashMap) choices.get(0);
                        LinkedHashMap message = (LinkedHashMap) entry.get("message");
                        messageRepository.save(sessionId, new GptMessage(message.get("role").toString(), message.get("content").toString()));
                        return message.get("content").toString();
                    });
        } catch (Exception ex){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "External api call failed");
        }
    }
}
