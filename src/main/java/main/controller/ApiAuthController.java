package main.controller;

import com.github.cage.Cage;
import main.api.responseAndAnswers.auth.LoginRequest;
import main.api.responseAndAnswers.auth.LoginResponse;
import main.api.responseAndAnswers.auth.*;
import main.repository.UserRepository;
import main.service.AuthService;
import main.service.ImageService;
import main.service.MailSender;
import main.service.RandomGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
public class ApiAuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private MailSender mailSender;

    @Autowired
    private RandomGenerator randomGenerator;

    @Autowired
    private ImageService imageService;

    @Autowired
    public ApiAuthController(AuthenticationManager authenticationManager, UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
    }

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
        RegisterResponse response = authService.findErrors(request);
        if(!response.isResult()){
            return ResponseEntity.ok().body(response);
        } else {
            authService.addUserToDB(request);
            return ResponseEntity.ok().body(response);
        }
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
    private ResponseEntity<LogoutResponse> logout(){
        SecurityContextHolder.clearContext();
        LogoutResponse response = new LogoutResponse();
        response.setResult(true);

        return ResponseEntity.ok().body(response);
    }

}
