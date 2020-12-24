package main.controller;

import com.github.cage.Cage;
import jdk.jfr.Enabled;
import main.api.response.LoginRequest;
import main.api.response.LoginResponse;
import main.api.response.UserLoginResponse;
import main.api.response.auth.CaptchaResponse;
import main.api.response.auth.RegisterRequest;
import main.api.response.auth.RegisterResponse;
import main.repository.UserRepository;
import main.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.security.Principal;

@RestController("/api/auth/")
public class ApiAuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    public ApiAuthController(AuthenticationManager authenticationManager, UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
    }

    @PostMapping("/api/auth/login")
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

    @GetMapping("/api/auth/check")
    public ResponseEntity<LoginResponse> check(Principal principal) {
        if(principal == null){
            return ResponseEntity.ok().body(new LoginResponse());
        }
        return ResponseEntity.ok().body(authService.getLoginResponse(principal.getName()));
    }


    @PostMapping("/api/auth/register")
    private ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest request){
        RegisterResponse response = authService.findErrors(request);
        if(!response.isResult()){
            return ResponseEntity.ok().body(response);
        } else {
            authService.addUserToDB(request);
            return ResponseEntity.ok().body(response);
        }
    }

    @GetMapping("/api/auth/captcha")
    private ResponseEntity<CaptchaResponse> captcha(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String randomString = authService.getRandomStringImpl();
        String identification = authService.generateSpecialCode();
        Cage cage = new Cage();
        BufferedImage image = authService.resizeImage(cage.drawImage(randomString));
        String code = authService.formImageToString(image);
        CaptchaResponse captchaResponse = new CaptchaResponse(identification, code);
        authService.addCaptchaToDB(captchaResponse, randomString);
        return ResponseEntity.ok().body(captchaResponse);
    }

}
