package main.service;

import main.model.GlobalSetting;
import main.repository.GlobalSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SettingsService {

    private GlobalSettingsRepository globalSettingsRepository;

    @Autowired
    public SettingsService(GlobalSettingsRepository globalSettingsRepository){
        this.globalSettingsRepository = globalSettingsRepository;
    }

    public GlobalSetting getUserMode(){
        return globalSettingsRepository.findByCode("MULTIUSER_MODE").get();
    }

    public GlobalSetting getPostModerationMode(){
        return globalSettingsRepository.findByCode("POST_PREMODERATION").get();
    }

    public GlobalSetting getStatisticsMode(){
        return globalSettingsRepository.findByCode("STATISTICS_IS_PUBLIC").get();
    }
}
