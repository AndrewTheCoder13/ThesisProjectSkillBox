package main.api.responseAndAnswers.profile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Configuration;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfileChangeAnswer {
    private boolean result;
    private ProfileErrors errors;
}
