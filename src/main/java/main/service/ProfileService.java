package main.service;

import main.api.responseAndAnswers.profile.ProfileChange;
import main.api.responseAndAnswers.profile.ProfileChangeAnswer;
import main.api.responseAndAnswers.profile.ProfileErrors;
import main.model.User;
import main.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Optional;

@Service
public class ProfileService {

    @Autowired
    private UserRepository userRepository;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);

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
        if(profileChange.getEmail()!=null){
            Optional<User> secondUser= userRepository.findByEmail(profileChange.getEmail());
            if(secondUser.isPresent()){
                if(secondUser.get().getId() != mainUser.getId()){
                    errors.setEmail("Этот e-mail уже зарегистрирован");
                }
            }
        } else if (profileChange.getEmail() == null){
            errors.setEmail("e-mail указан неверно");
        } else mainUser.setEmail(profileChange.getEmail());
    }

    private void passwordErrors(ProfileChange profileChange, User mainUser, ProfileErrors errors){
        if(profileChange.getPassword()!=null){
            if(profileChange.getPassword().length() < 6){
                errors.setPassword("Пароль короче 6-ти символов");
            } else mainUser.setPassword(passwordEncoder.encode(profileChange.getPassword()));
        }
    }
}
