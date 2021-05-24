package main.controller;

import com.github.cage.Cage;
import lombok.AllArgsConstructor;
import main.api.responseAndAnswers.auth.LoginRequest;
import main.api.responseAndAnswers.auth.LoginResponse;
import main.api.responseAndAnswers.auth.*;
import main.config.SecurityConfig;
import main.repository.UserRepository;
import main.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.security.Principal;
import java.util.Optional;

@RestController("/api/auth")
@RequestMapping("/api/auth")
@AllArgsConstructor
public class ApiAuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final AuthService authService;
    private final MailSender mailSender;
    private final RandomGenerator randomGenerator;
    private final ImageService imageService;
    private final SettingsService settingsService;

    @PostMapping("login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        main.model.User currentUser = userRepository.findByEmail(loginRequest.getEmail()).orElseThrow(() -> new UsernameNotFoundException(loginRequest.getEmail()));
        Authentication auth = authenticationManager.
                authenticate(
                        new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(auth);
        User user = (User) auth.getPrincipal();
        LoginResponse loginResponse = authService.getLoginResponse(user.getUsername());
        return ResponseEntity.ok().body(loginResponse);
    }

    @GetMapping("check")
    public ResponseEntity<LoginResponse> check(Principal principal) {
        if(principal == null){
            return ResponseEntity.ok().body(new LoginResponse());
        }
        return ResponseEntity.ok().body(authService.getLoginResponse(principal.getName()));
    }

    @PostMapping("restore")
    public ResponseEntity<RestoreResponse> restore(@RequestBody RestoreRequest restoreRequest) throws IOException {
        Optional<main.model.User> user = userRepository.findByEmail(restoreRequest.getEmail());
        if(user.isPresent()){
            return authService.letterSend(user, restoreRequest);
        } else {
            return authService.userNotFound();
        }
    }

    @PostMapping("password")
    public ResponseEntity<PasswordAnswer> password(@RequestBody PasswordRequest request) {
        return authService.password(request);
    }

    @PostMapping("register")
    private ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest request){
        if(!settingsService.getUserMode().getValue().equals("YES")){
            return ResponseEntity.notFound().build();
        }
        RegisterResponse response = authService.findErrors(request);
        if (response.isResult()) {
            authService.addUserToDB(request);
        }
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("captcha")
    private ResponseEntity<CaptchaResponse> captcha(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String randomString = randomGenerator.generate(8);
        String identification = authService.generateSpecialCode();
        Cage cage = new Cage();
        BufferedImage image = imageService.resizeCaptchaImage(cage.drawImage(randomString));
        String code = imageService.formImageToString(image);
        CaptchaResponse captchaResponse = new CaptchaResponse(identification, code);
        authService.addCaptchaToDB(captchaResponse, randomString);
        return ResponseEntity.ok().body(captchaResponse);
    }

    @GetMapping("logout")
    public String logout(HttpServletResponse httpServletResponse) throws Exception {
        SecurityContextHolder.clearContext();
        LogoutResponse response = new LogoutResponse();
        response.setResult(true);
        return "index";
    }



}
