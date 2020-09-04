package main.api.response.settings;

import lombok.Data;
import main.model.GlobalSetting;

import java.util.TreeMap;

@Data
public class GlobalSettingsResponse {

    private TreeMap<String, Boolean> settings;

    public GlobalSettingsResponse(Iterable<GlobalSetting> settings){
        this.settings = new TreeMap<>();
        settings.forEach(s -> this.settings.put(s.getCode(), s.getValue().equals("YES")? true : false));
    }
}
