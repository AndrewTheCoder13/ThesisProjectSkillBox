package main.service;

import lombok.AllArgsConstructor;
import main.api.responseAndAnswers.settings.SaveSettingsAnswer;
import main.api.responseAndAnswers.settings.SaveSettingsRequest;
import main.model.GlobalSetting;
import main.repository.GlobalSettingsRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class SettingsService {

    private final GlobalSettingsRepository globalSettingsRepository;

    public GlobalSetting getUserMode(){
        return globalSettingsRepository.findByCode("MULTIUSER_MODE").get();
    }

    public GlobalSetting getPostModerationMode(){
        return globalSettingsRepository.findByCode("POST_PREMODERATION").get();
    }

    public GlobalSetting getStatisticsMode(){
        return globalSettingsRepository.findByCode("STATISTICS_IS_PUBLIC").get();
    }

    public ResponseEntity<SaveSettingsAnswer> saveSetting(SaveSettingsRequest saveSettingsRequest){
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
