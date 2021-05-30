package main.service;

import lombok.AllArgsConstructor;
import main.api.responseAndAnswers.profile.ProfileChange;
import main.api.responseAndAnswers.profile.ProfileChangeAnswer;
import main.api.responseAndAnswers.profile.ProfileErrors;
import main.model.User;
import main.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.Optional;

@Service
@Component
public class ProfileService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final int MAX_FILE_SIZE;
    private final ImageService imageService;

    public ProfileService(UserRepository userRepository,
                          BCryptPasswordEncoder passwordEncoder, @Value("${blog.files.maxFileSize}") int MAX_FILE_SIZE,
                          ImageService imageService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.MAX_FILE_SIZE = MAX_FILE_SIZE;
        this.imageService = imageService;
    }

    public ResponseEntity<ProfileChangeAnswer> editProfile(MultipartFile photo, String name, String email, String password, Principal principal) throws IOException {
        ProfileChange change = new ProfileChange(name,0, email, password, "");
        ProfileChangeAnswer answer = getProfileChangeAnswer(change, principal);
        long size = photo.getSize();
        if(size > MAX_FILE_SIZE){
            if(answer.getErrors() == null){
                ProfileErrors errors = new ProfileErrors();
                errors.setPhoto("Фото слишком большое, нужно не более 5 Мб");
                answer.setResult(false);
                answer.setErrors(errors);
                return ResponseEntity.ok().body(answer);
            }
        }
        imageService.image(photo, true, principal);
        return  ResponseEntity.ok().body(answer);
    }
    public ProfileChangeAnswer getProfileChangeAnswer(ProfileChange profileChange, Principal principal){
        ProfileChangeAnswer answer = new ProfileChangeAnswer();
        ProfileErrors errors = new ProfileErrors();
        User mainUser = userRepository.findByEmail(principal.getName()).get();
        nameErrors(profileChange, mainUser, errors);
        emailErrors(profileChange, mainUser, errors);
        passwordErrors(profileChange, mainUser, errors);
        if(profileChange.getRemovePhoto() == 1){
            mainUser.setPhoto(null);
        }
        answer.setResult(errors.allNull());
        if(!errors.allNull()){
            answer.setErrors(errors);
        } else {
            userRepository.save(mainUser);
        }
        return answer;
    }

    private void nameErrors(ProfileChange profileChange, User mainUser,ProfileErrors errors){
        if(profileChange.getName()!=null){
            mainUser.setName(profileChange.getName());
        } else {
            errors.setName("Имя указано неверно");
        }
    }

    private void emailErrors(ProfileChange profileChange, User mainUser, ProfileErrors errors){
        if (profileChange.getEmail() == null) {
            errors.setEmail("e-mail указан неверно");
            return;
        }

        User secondUser = userRepository.findByEmail(profileChange.getEmail()).orElseThrow();
        if (secondUser.getId() != mainUser.getId()) {
            errors.setEmail("Этот e-mail уже зарегистрирован");
            return;
        }
        mainUser.setEmail(profileChange.getEmail());
    }

    private void passwordErrors(ProfileChange profileChange, User mainUser, ProfileErrors errors){
        if(profileChange.getPassword()!=null){
            if(profileChange.getPassword().length() < 6){
                errors.setPassword("Пароль короче 6-ти символов");
            } else mainUser.setPassword(passwordEncoder.encode(profileChange.getPassword()));
        }
    }
}
