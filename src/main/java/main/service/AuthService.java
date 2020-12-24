package main.service;

import main.api.response.LoginResponse;
import main.api.response.UserLoginResponse;
import main.api.response.auth.CaptchaResponse;
import main.api.response.auth.Errors;
import main.api.response.auth.RegisterRequest;
import main.api.response.auth.RegisterResponse;
import main.config.SecurityConfig;
import main.model.CaptchaCode;
import main.model.User;
import main.repository.CaptchaCodeRepository;
import main.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.Security;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
@EnableScheduling
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CaptchaCodeRepository captchaCodeRepository;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);

    public LoginResponse getLoginResponse(String email) {
        main.model.User currentUser = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(email));

        UserLoginResponse userLoginResponse = new UserLoginResponse();
        userLoginResponse.setEmail(currentUser.getEmail());
        userLoginResponse.setName(currentUser.getName());
        userLoginResponse.setModeration(currentUser.getIsModerator() == 1);
        userLoginResponse.setId(currentUser.getId());

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setResult(true);
        loginResponse.setUserLoginResponse(userLoginResponse);
        return loginResponse;
    }

    public String getRandomStringImpl() {
        char[] charArray = new char[8];
        Random random = new Random();
        int capitalLetter;
        for (int i = 0; i < 8; i++) {
            capitalLetter = 65 + random.nextInt(26);
            charArray[i] = (char) capitalLetter;
        }
        String randomString = "";
        for (char simbol : charArray) {
            randomString += simbol;
        }
        return randomString;
    }

    public String formImageToString(BufferedImage image) {
        String encodedImage = "data:image/png;base64, ";
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "png", baos);
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] imageInByte = baos.toByteArray();
        String encodedString = Base64.getEncoder().encodeToString(imageInByte);
        encodedImage += encodedString;
        return encodedImage;
    }

    public BufferedImage resizeImage(BufferedImage image) {
        BufferedImage newImage = new BufferedImage(100, 35, BufferedImage.TYPE_INT_RGB);
        Graphics g = newImage.createGraphics();
        g.drawImage(image, 0, 0, 100, 35, null);
        g.dispose();
        return newImage;
    }

    public void addCaptchaToDB(CaptchaResponse captchaResponse, String randomString) {
        CaptchaCode captchaCode = new CaptchaCode();
        captchaCode.setCode(randomString);
        captchaCode.setSecretCode(captchaResponse.getSecret());
        captchaCode.setTime(LocalDateTime.now(ZoneOffset.UTC));
        captchaCodeRepository.save(captchaCode);
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

    @Scheduled(fixedRate = 3600000)
    public void deleteOldCaptcha() {
        LocalDateTime hourAgo = LocalDateTime.now(ZoneOffset.UTC);
        captchaCodeRepository.oldVersions(hourAgo);
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
        boolean areErrors = checkErrors(errors);
        return areErrors? errors : null;
    }

    public void errorInEmail(Errors errors, String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            errors.setEmail("Этот e-mail уже зарегистрирован");
        }
    }

    public void errorInName(Errors errors, String name){
        //реализовать по какому принципу некорректное имя
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

    private boolean checkErrors(Errors errors) {
        return errors.getEmail() != null || errors.getPassword() != null || errors.getName() != null || errors.getCaptcha() != null;
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
}
