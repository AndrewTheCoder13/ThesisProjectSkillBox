package main.api.responseAndAnswers.tags;

import lombok.Data;
import main.model.Tag;

import java.util.*;

@Data
public class Tags {

    private List<TagResponse> tags;

    public Tags(long postCount, HashMap<Tag, Integer> allTags){
        tags = new ArrayList<>();
        for(Tag tag : allTags.keySet()){
            TagResponse tagResponse = new TagResponse(tag.getName(), allTags.get(tag));
            tags.add(tagResponse);
        }
        Optional<TagResponse> max = tags.stream().max(Comparator.comparing(TagResponse::getWeight));
        double maxWeight = 0;
        if(max.isPresent()){
            maxWeight = max.get().getWeight();
        }
        double finalMaxWeight = maxWeight;
        tags.forEach(tag -> tag.setWeight(tag.getWeight() / finalMaxWeight));
    }

}
