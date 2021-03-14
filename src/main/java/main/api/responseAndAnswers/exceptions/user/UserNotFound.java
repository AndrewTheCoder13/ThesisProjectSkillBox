package main.api.responseAndAnswers.exceptions.user;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserNotFound {
    private boolean result;
}
