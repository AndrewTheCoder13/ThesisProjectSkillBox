package main.controller;
import lombok.AllArgsConstructor;
import main.api.responseAndAnswers.auth.LoginRequest;
import main.api.responseAndAnswers.auth.LoginResponse;
import main.api.responseAndAnswers.auth.*;
import main.service.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;

@RestController("/api/auth")
@RequestMapping("/api/auth")
@AllArgsConstructor
public class ApiAuthController {

    private final AuthService authService;

    @PostMapping("login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        return authService.login(loginRequest);
    }

    @GetMapping("check")
    public ResponseEntity<LoginResponse> check(Principal principal) {
        if(principal == null){
            return ResponseEntity.ok().body(new LoginResponse());
        }
        return ResponseEntity.ok().body(authService.getLoginResponse(principal.getName()));
    }

    @PostMapping("restore")
    public ResponseEntity<RestoreResponse> restore(@RequestBody RestoreRequest restoreRequest){
        return authService.restore(restoreRequest);
    }

    @PostMapping("password")
    public ResponseEntity<PasswordAnswer> password(@RequestBody PasswordRequest request) {
        return authService.password(request);
    }

    @PostMapping("register")
    private ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest request){
        return authService.register(request);
    }

    @GetMapping("captcha")
    private ResponseEntity<CaptchaResponse> captcha(){
       return authService.captcha();
    }

    @GetMapping("logout")
    public String logout(){
       return authService.logout();
    }



}
