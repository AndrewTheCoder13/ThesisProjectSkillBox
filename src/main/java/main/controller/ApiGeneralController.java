package main.controller;

import main.api.responseAndAnswers.general.InitResponse;
import main.api.responseAndAnswers.calendar.CalendarResponse;
import main.api.responseAndAnswers.comment.PutCommentAnswer;
import main.api.responseAndAnswers.comment.PutCommentRequest;
import main.api.responseAndAnswers.post.ModerationAnswer;
import main.api.responseAndAnswers.post.ModerationRequest;
import main.api.responseAndAnswers.profile.ProfileChange;
import main.api.responseAndAnswers.profile.ProfileChangeAnswer;
import main.api.responseAndAnswers.profile.ProfileErrors;
import main.api.responseAndAnswers.settings.GlobalSettingsResponse;
import main.api.responseAndAnswers.settings.SaveSettingsAnswer;
import main.api.responseAndAnswers.settings.SaveSettingsRequest;
import main.api.responseAndAnswers.tags.Tags;
import main.api.responseAndAnswers.user.StatisticAnswer;
import main.model.GlobalSetting;
import main.repository.GlobalSettingsRepository;
import main.service.*;
import org.apache.tomcat.util.http.fileupload.impl.SizeLimitExceededException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.*;

@RestController("/api")
public class ApiGeneralController {

    private GlobalSettingsRepository globalSettingsRepository;

    private TagService tagService;

    private ImageService imageService;

    private ProfileService profileService;

    private CalendarService calendarService;

    private PostService postService;

    private BCryptPasswordEncoder passwordEncoder;

    private final InitResponse initResponse;

    @Autowired
    public ApiGeneralController(GlobalSettingsRepository globalSettingsRepository, TagService tagService,
                                ImageService imageService, ProfileService profileService, CalendarService calendarService,
                                PostService postService, InitResponse initResponse) {
        this.globalSettingsRepository = globalSettingsRepository;
        this.tagService = tagService;
        this.imageService = imageService;
        this.profileService = profileService;
        this.calendarService = calendarService;
        this.postService = postService;
        passwordEncoder = new BCryptPasswordEncoder(12);
        this.initResponse = initResponse;
    }


    @GetMapping("/api/init")
    public ResponseEntity<InitResponse> init() {
        return ResponseEntity.ok().body(initResponse);
    }

    @PreAuthorize("hasAuthority('user:write')")
    @PostMapping(value = "/api/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String image(@RequestParam("image")MultipartFile file) throws SizeLimitExceededException, IOException {
        long size = file.getSize();
        if(size > 5242880){
            throw new SizeLimitExceededException("Файл слишком большой", 5, size);
        }
        String format = file.getContentType().substring(file.getContentType().indexOf("/") + 1);
        if(!format.equals("jpg") && !format.equals("jpeg") && !format.equals("png")){
            throw new InvalidPropertiesFormatException("Неверный формат");
        }
        return imageService.image(file, false, null);
    }

    @PreAuthorize("hasAuthority('user:write')")
    @PostMapping("/api/comment")
    public ResponseEntity<PutCommentAnswer> putCommentAnswer(Principal principal, @RequestBody PutCommentRequest request){
        return postService.addComment(request, principal);
    }

    @GetMapping("/api/tag")
    public ResponseEntity<Tags> tag(@RequestParam(required = false) String query) {
        return ResponseEntity.ok().body(tagService.getTags(query));
    }

    @PreAuthorize("hasAuthority('user:moderate')")
    @PostMapping("/api/moderation")
    public ResponseEntity<ModerationAnswer> moderation(Principal principal, @RequestBody ModerationRequest request){
        return postService.moderation(principal, request);
    }

    @GetMapping("/api/calendar")
    public ResponseEntity<CalendarResponse> calendar(@RequestParam Map<String, String> allParams){
        return  calendarService.calendar(allParams);
    }

    @PreAuthorize("hasAuthority('user:write')")
    @PostMapping(value = "/api/profile/my", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProfileChangeAnswer> editProfile(@RequestBody ProfileChange profileChange, Principal principal){
        ProfileChangeAnswer answer = profileService.getProfileChangeAnswer(profileChange, principal);
        return ResponseEntity.ok().body(answer);
    }

    @PreAuthorize("hasAuthority('user:write')")
    @PostMapping(value = "/api/profile/my", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProfileChangeAnswer> editProfile(@RequestPart("photo") MultipartFile photo,
                                                            @RequestPart(value = "name", required = false) String name,
                                                            @RequestPart(value = "email", required = false) String email,
                                                            @RequestPart(value = "password", required = false) String password,
                                                            Principal principal) throws IOException {
        ProfileChange change = new ProfileChange(name,0, email, password, "");
        ProfileChangeAnswer answer = profileService.getProfileChangeAnswer(change, principal);
        long size = photo.getSize();
        if(size > 5242880){
            if(answer.getErrors() == null){
                ProfileErrors errors = new ProfileErrors();
                errors.setPhoto("Фото слишком большое, нужно не более 5 Мб");
                answer.setResult(false);
                answer.setErrors(errors);
                return ResponseEntity.ok().body(answer);
            }
        }
        imageService.image(photo, true, principal);
        return ResponseEntity.ok().body(answer);
    }

    @PreAuthorize("hasAuthority('user:write')")
    @GetMapping("/api/statistics/my")
    public ResponseEntity<StatisticAnswer> myStatistic(Principal principal){
        return  postService.myStatistic(principal);
    }

    @GetMapping("/api/statistics/all")
    public ResponseEntity<StatisticAnswer> allStatistic(Principal principal){
        return  postService.allStatistic(principal);
    }

    @GetMapping("/api/settings")
    public ResponseEntity<TreeMap<String, Boolean>> settings() {
        Iterable<GlobalSetting> settings = globalSettingsRepository.findAll();
        GlobalSettingsResponse responseObject = new GlobalSettingsResponse(settings);
        TreeMap<String, Boolean> finalResponse = responseObject.getSettings();
        return ResponseEntity.ok().body(finalResponse);
    }

    @PreAuthorize("hasAuthority('user:moderate')")
    @PutMapping("/api/settings")
    public ResponseEntity<SaveSettingsAnswer> saveSettings(@RequestBody SaveSettingsRequest saveSettingsRequest) {
        SaveSettingsAnswer saveSettingsAnswer = new SaveSettingsAnswer();
        saveSettingsAnswer.setResult(true);
        GlobalSetting multiUserMode = globalSettingsRepository.findByCode("MULTIUSER_MODE").get();
        multiUserMode.setValue(saveSettingsRequest.isMultiUserMode()? "YES" : "NO");
        GlobalSetting postPreModeration = globalSettingsRepository.findByCode("POST_PREMODERATION").get();
        postPreModeration.setValue(saveSettingsRequest.isPostPreModeration()? "YES" : "NO");
        GlobalSetting statisticIsPublic = globalSettingsRepository.findByCode("STATISTICS_IS_PUBLIC").get();
        statisticIsPublic.setValue(saveSettingsRequest.isStatisticsIsPublic()? "YES" : "NO");
        globalSettingsRepository.save(multiUserMode);
        globalSettingsRepository.save(postPreModeration);
        globalSettingsRepository.save(statisticIsPublic);
        return ResponseEntity.ok().body(saveSettingsAnswer);
    }
}
