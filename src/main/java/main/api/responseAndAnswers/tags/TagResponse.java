package main.api.responseAndAnswers.tags;

import lombok.Data;

@Data
public class TagResponse {
    private String name;
    private double weight;

    public TagResponse(String name, double weight){
        this.name = name;
        this.weight = weight;
    }

}
