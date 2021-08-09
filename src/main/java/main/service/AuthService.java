package main.service;

import com.github.cage.Cage;
import lombok.AllArgsConstructor;
import main.api.responseAndAnswers.auth.LoginResponse;
import main.api.responseAndAnswers.auth.UserLoginResponse;
import main.api.responseAndAnswers.auth.*;
import main.model.CaptchaCode;
import main.model.User;
import main.repository.CaptchaCodeRepository;
import main.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

@Service
@EnableScheduling
@AllArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final CaptchaCodeRepository captchaCodeRepository;
    private final MailSender mailSender;
    private final BCryptPasswordEncoder passwordEncoder;
    private final ImageService imageService;
    private final SettingsService settingsService;
    private final AuthenticationManager authenticationManager;

    public ResponseEntity<LoginResponse> login(LoginRequest loginRequest){
        main.model.User currentUser = userRepository.findByEmail(loginRequest.getEmail()).orElseThrow(() -> new UsernameNotFoundException(loginRequest.getEmail()));
        Authentication auth = authenticationManager.
                authenticate(
                        new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(auth);
        org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User) auth.getPrincipal();
        LoginResponse loginResponse = getLoginResponse(user.getUsername());
        return ResponseEntity.ok().body(loginResponse);
    }

    public LoginResponse getLoginResponse(String email) {
        main.model.User currentUser = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(email));

        UserLoginResponse userLoginResponse = new UserLoginResponse();
        userLoginResponse.setEmail(currentUser.getEmail());
        userLoginResponse.setName(currentUser.getName());
        userLoginResponse.setModeration(currentUser.getIsModerator() == 1);
        userLoginResponse.setId(currentUser.getId());
        userLoginResponse.setPhoto(currentUser.getPhoto());
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setResult(true);
        loginResponse.setUserLoginResponse(userLoginResponse);
        return loginResponse;
    }


    public ResponseEntity<PasswordAnswer> password(PasswordRequest request){
        PasswordAnswer answer = new PasswordAnswer();
        Optional<User> userOptional = userRepository.findByCode(request.getCode());
        if(userOptional.isEmpty()){
            return oldLink();
        }
        User user = userOptional.get();
        if(request.getPassword().length() < 6){
            return shortPassword();
        }
        CaptchaCode code = captchaCodeRepository.getBySecret(request.getCaptchaSecret());
        if(!code.getCode().equals(request.getCaptcha())){
            return wrongCaptcha();
        }
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setCode(null);
        userRepository.save(user);
        answer.setErrors(null);
        answer.setResult(true);
        return  ResponseEntity.ok().body(answer);
    }

    private ResponseEntity<PasswordAnswer> oldLink(){
        PasswordAnswer answer = new PasswordAnswer();
        PasswordErrors errors = new PasswordErrors();
        errors.setCode("Ссылка для восстановления пароля устарела.\n" +
                "<a href=\n" +
                "/auth/restore\\\">Запросить ссылку снова</a>");
        answer.setErrors(errors);
        answer.setResult(false);
        return  ResponseEntity.ok().body(answer);
    }

    private ResponseEntity<PasswordAnswer> shortPassword(){
        PasswordAnswer answer = new PasswordAnswer();
        PasswordErrors errors = new PasswordErrors();
        errors.setPassword("Пароль короче 6-ти символов");
        answer.setErrors(errors);
        answer.setResult(false);
        return  ResponseEntity.ok().body(answer);
    }

    private ResponseEntity<PasswordAnswer> wrongCaptcha(){
        PasswordAnswer answer = new PasswordAnswer();
        PasswordErrors errors = new PasswordErrors();
        errors.setCaptcha("Код с картинки введён неверно");
        answer.setErrors(errors);
        answer.setResult(false);
        return  ResponseEntity.ok().body(answer);
    }


    public void addUserToDB(RegisterRequest request){
        String email = request.getEmail();
        String name = request.getName();
        String password = request.getPassword();
        User user = new User();
        user.setEmail(email);
        user.setName(name);
        user.setPassword(passwordEncoder.encode(password));
        byte isModerator = 0;
        user.setIsModerator(isModerator);
        user.setRegTime(LocalDateTime.now(ZoneOffset.UTC));
        userRepository.save(user);
    }

    public ResponseEntity<CaptchaResponse> captcha(){
        String randomString = RandomGenerator.generate(8);
        String identification = generateSpecialCode();
        Cage cage = new Cage();
        BufferedImage image = imageService.resizeCaptchaImage(cage.drawImage(randomString));
        String code = imageService.convertToBase64(image);
        CaptchaResponse captchaResponse = new CaptchaResponse(identification, code);
        addCaptchaToDB(captchaResponse, randomString);
        return ResponseEntity.ok().body(captchaResponse);
    }

    public String generateSpecialCode() {
        boolean isInDB = true;
        String code = "";
        while (isInDB) {
            UUID secretCode = UUID.randomUUID();
            code = secretCode.toString();
            isInDB = captchaCodeRepository.countOfIdentification(code) > 0;

        }
        return code;
    }

    public void addCaptchaToDB(CaptchaResponse captchaResponse, String randomString) {
        CaptchaCode captchaCode = new CaptchaCode();
        captchaCode.setCode(randomString);
        captchaCode.setSecretCode(captchaResponse.getSecret());
        captchaCode.setTime(LocalDateTime.now(ZoneOffset.UTC));
        captchaCodeRepository.save(captchaCode);
    }

    public String logout(){
        SecurityContextHolder.clearContext();
        LogoutResponse response = new LogoutResponse();
        response.setResult(true);
        return "index";
    }

    public ResponseEntity<RegisterResponse> register(RegisterRequest request){
        if(!settingsService.getUserMode().getValue().equals("YES")){
            return ResponseEntity.notFound().build();
        }
        RegisterResponse response = findErrors(request);
        if (response.isResult()) {
           addUserToDB(request);
        }
        return ResponseEntity.ok().body(response);
    }

    public RegisterResponse findErrors(RegisterRequest request) {
        Errors errors = getErrors(request);
        return errors != null ? new RegisterResponse(false, errors) : new RegisterResponse(true, null);
    }

    public Errors getErrors(RegisterRequest request) {
        Errors errors = new Errors();
        String email = request.getEmail();
        String name = request.getName();
        String password = request.getPassword();
        String captcha = request.getCaptcha();
        String secret = request.getCaptchaSecret();
        errorInEmail(errors, email);
        errorInName(errors, name);
        errorInPassword(errors, password);
        errorsInCaptcha(errors, captcha, secret);
        boolean areErrors = errors.checkErrors();
        return areErrors? errors : null;
    }

    public void errorInEmail(Errors errors, String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            errors.setEmail("Этот e-mail уже зарегистрирован");
        }
    }

    public void errorInName(Errors errors, String name){
        if(name == null){
            errors.setName("Имя указано неверно");
        }
    }

    public void errorInPassword(Errors errors, String password){
        if(password.length() < 6){
            errors.setPassword("Пароль короче 6-ти символов");
        }
    }

    private void errorsInCaptcha(Errors errors, String captcha, String secret) {
        CaptchaCode captchaCode = captchaCodeRepository.getBySecret(secret);
        if (!captchaCode.getCode().equals(captcha)){
            errors.setCaptcha("Код с картинки введён неверно");
        }
    }

    public ResponseEntity<RestoreResponse> restore(RestoreRequest restoreRequest){
        Optional<main.model.User> user = userRepository.findByEmail(restoreRequest.getEmail());
        if(user.isPresent()){
            return letterSend(user, restoreRequest);
        } else {
            return userNotFound();
        }
    }

    public ResponseEntity<RestoreResponse> letterSend(Optional<main.model.User> user, RestoreRequest restoreRequest){
        main.model.User userGet = user.get();
        int length = 64;
        String code = RandomGenerator.generate(length);
        userGet.setCode(code);
        mailSender.send(restoreRequest.getEmail(), "Восстановление пароля", "Ссылка для восстановление пароля: \n https://sping-boot-aplication-skillbox.herokuapp.com/login/change-password/" + code);
        userRepository.save(userGet);
        RestoreResponse response = new RestoreResponse();
        response.setResult(true);
        return ResponseEntity.ok().body(response);
    }

    public ResponseEntity<RestoreResponse> userNotFound(){
        RestoreResponse response = new RestoreResponse();
        response.setResult(false);
        return ResponseEntity.ok().body(response);
    }

    @Scheduled(fixedRate = 3600000)
    public void deleteOldCaptcha() {
        LocalDateTime hourAgo = LocalDateTime.now(ZoneOffset.UTC);
        captchaCodeRepository.oldVersions(hourAgo);
    }
}
