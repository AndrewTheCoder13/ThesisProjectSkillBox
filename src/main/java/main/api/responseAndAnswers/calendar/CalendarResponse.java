package main.api.responseAndAnswers.calendar;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.TreeMap;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CalendarResponse {
    private int[] years;
    private TreeMap<String, Integer> posts;
}
