package main.api.response.user;

import lombok.Data;
import main.model.User;

@Data
public class UserForPost {
    private int id;
    private String name;

    public UserForPost(User user){
        id = user.getId();
        name = user.getName();
    }
}
