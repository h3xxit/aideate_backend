package com.makeathontumai.aideate.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.makeathontumai.aideate.PromptConstants;
import com.makeathontumai.aideate.entities.GptMessage;
import com.makeathontumai.aideate.entities.MessageDto;
import com.makeathontumai.aideate.entities.Rating;
import com.makeathontumai.aideate.repository.MessageRepository;
import com.makeathontumai.aideate.repository.RatingRepository;
import com.makeathontumai.aideate.service.GptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@CrossOrigin(originPatterns = "*")
@RestController
public class ChatController {
    GptService service;
    MessageRepository repository;
    RatingRepository ratingRepository;

    public ChatController(GptService service, MessageRepository repository, RatingRepository ratingRepository) {
        this.service = service;
        this.repository = repository;
        this.ratingRepository = ratingRepository;
    }

    @GetMapping("/init-consultant")
    public MessageDto initializeConsultant() {
        int session = service.generateSessionId();
        service.addUserMessage(session, PromptConstants.initialPrompt);
        service.addGPTMessage(session, PromptConstants.FirstAIPrompt);
        return new MessageDto(session, PromptConstants.FirstAIPrompt);

        /*return service.sendTextMessage(session, PromptConstants.initialPrompt, null)
                .map(text ->{
                    service.addGPTMessage(session, PromptConstants.FirstAIPrompt);
                    return new MessageDto(session, PromptConstants.FirstAIPrompt);
                });*/
    }

    @GetMapping("/generate-solution")
    public Mono<String> generateSolution(@RequestParam int sessionId) {
        int session = service.generateSessionId();
        service.douplicateSession(sessionId, session);
        return service.sendTextMessage(session, PromptConstants.SolutionPrompt, null)
                .map(text -> {
                    MessageDto solution = new MessageDto(sessionId, text);
                    String s = solution.getText();

                    if(s.contains("{")){
                        s = s.substring(s.indexOf("{") );
                        s = s.substring(0, s.indexOf("}") + 1);
                    }else{
                        s = "\"problem\": \"I am really sorry, I have too little information so far on your company and business model, so I cannot produce a meaning output yet\n \" + " +
                                "\"description\": \"Description of how AI can be applied\"\n" +
                                "\"analysis\": \"Qualitative or quantitative evaluation of the expected business value enabled by AI (be detailed)\"\n"+
                                "\"risks\": \"Potential costs and risks\"\n" +
                                "\"data\": \"Data sources required by this use cases\"\n }";
                    }
                    System.out.println(solution.getText());
                    
                    return solution.getText();
                });
    }

    @GetMapping("/rate-answer")
    public Mono<Integer> rateAnswer(@RequestParam int sessionId) {
        int session = service.generateSessionId();
        service.douplicateSession(sessionId, session);
        return service.sendTextMessage(session, PromptConstants.RatePrompt, null)
                .map(text -> {
                    MessageDto solution = new MessageDto(session, text);
                    String s = solution.getText();
                    System.out.println(s);
                    int nr = 0;
                    for(int i = 0; i < s.length(); i++){
                        if(s.charAt(i) >= '0' && s.charAt(i) <= '9'){
                            nr = nr * 10 + (s.charAt(i) - '0');
                        } else if(nr != 0) {
                            break;
                        }
                    }
                    System.out.println(nr);
                    ratingRepository.save(sessionId, nr);
                    return nr;
                }
        );
    }

    @GetMapping("/get-average")
    public Rating getAvg(@RequestParam int sessionId) {
        return ratingRepository.get(sessionId);
    }

    @PostMapping("/send-message")
    public Mono<MessageDto> sendMessage(@RequestBody MessageDto message, @RequestParam(required = false) String model) {
        if(message.getSessionId() == null) {
            int session = service.generateSessionId();
            message.setSessionId(session);
            service.addUserMessage(session, PromptConstants.initialPrompt);
            service.addGPTMessage(session, PromptConstants.FirstAIPrompt);
        }
        if(message.getSessionId() == 1 && repository.getMessagesFromSession(1).size() <= 1) {
            service.addUserMessage(message.getSessionId(), PromptConstants.initialPrompt);
            service.addGPTMessage(message.getSessionId(), PromptConstants.FirstAIPrompt);
        }
        return service.sendTextMessage(message.getSessionId(), message.getText(), model)
                .map(text -> new MessageDto(message.getSessionId(), text));
    }

    @GetMapping("/restore-session")
    public List<GptMessage> restoreSession(@RequestParam int sessionId) {
        List<GptMessage> list = new ArrayList<>(repository.getMessagesFromSession(sessionId));
        if(list.size() > 0){
            list.remove(0);
        }
        return list;
    }
}
