package main.api.responseAndAnswers.profile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileChange {
    private String name;
    private int removePhoto;
    private String email;
    private String password;
    private String photo;
}
