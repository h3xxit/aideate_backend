package com.makeathontumai.aideate.repository;

import com.makeathontumai.aideate.entities.GptMessage;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class MessageRepository {
    private HashMap<Integer, List<GptMessage>> allMessages = new HashMap<>();

    public Integer generateSessionId(){
        Random random = new Random();
        int newSessionId = Math.abs(random.nextInt()) % 1000 + 2;
        while (allMessages.containsKey(newSessionId)) {
            newSessionId = Math.abs(random.nextInt()) % 1000 + 2;
        }
        return newSessionId;
    }

    public void save(Integer sessionId, List<GptMessage> messages) {
        if(!allMessages.containsKey(sessionId))
            allMessages.put(sessionId, messages);
        allMessages.put(sessionId, messages);
    }

    public GptMessage save(Integer sessionId, GptMessage message) {
        if(!allMessages.containsKey(sessionId))
            allMessages.put(sessionId, new ArrayList<>());
        allMessages.get(sessionId).add(message);
        return message;
    }

    public List<GptMessage> getMessagesFromSession(int sessionId) {
        if(!allMessages.containsKey(sessionId))
            return new ArrayList<GptMessage>();
        return new ArrayList<GptMessage>(allMessages.get(sessionId));
    }
}
