package main.api.response.user;

import lombok.Data;
import main.model.User;

@Data
public class UserForComment {
    private int id;
    private String name;
    private String photo;

    public UserForComment(User user){
        id = user.getId();
        name = user.getName();
        if(user.getPhoto() != null){
            photo = user.getPhoto();
        } else {
            photo = "";
        }
    }
}
