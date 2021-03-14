package main.api.responseAndAnswers.profile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileErrors {
    private String email;
    private String photo;
    private String name;
    private String password;

    public boolean allNull(){
        return (email == null) & (password == null) & (name == null) & (photo == null);
    }
}
