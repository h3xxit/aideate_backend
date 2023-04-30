package com.makeathontumai.aideate.repository;

import com.makeathontumai.aideate.entities.GptMessage;
import com.makeathontumai.aideate.entities.Rating;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;



@Repository
public class RatingRepository {

    private HashMap<Integer, Rating> allRatings = new HashMap<>();

    public void save(Integer sessionId, Integer rating) {
        if(!allRatings.containsKey(sessionId)) {
            allRatings.put(sessionId, new Rating(0.0, 0, 0));
        }
        Rating old = allRatings.get(sessionId);
        old.avg = (old.avg * old.nrOf + rating) / (old.nrOf + 1);
        old.nrOf++;
        old.latest = rating;
    }

    public Rating get(Integer sessionId) {
        if(!allRatings.containsKey(sessionId))
            return new Rating(1.0, 0, 1);
        return allRatings.get(sessionId);
    }

    public Double getAvg(Integer sessionId) {
        if(!allRatings.containsKey(sessionId))
            return 0.0;
        return allRatings.get(sessionId).getAvg();
    }

    public Integer getLatest(Integer sessionId) {
        if(!allRatings.containsKey(sessionId))
            return 0;
        return allRatings.get(sessionId).latest;
    }
}
