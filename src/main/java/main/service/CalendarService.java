package main.service;

import main.api.responseAndAnswers.calendar.CalendarResponse;
import main.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

@Service
public class CalendarService {

    @Autowired
    private PostRepository postRepository;

    public ResponseEntity<CalendarResponse> calendar(@RequestParam Map<String, String> allParams) {
        CalendarResponse response = new CalendarResponse();
        TreeMap<String, Integer> posts = new TreeMap<>();
        ArrayList<LocalDateTime> time = postRepository.years();
        int[] years = new int[time.size()];
        time.sort(Comparator.comparing(LocalDateTime::getYear));
        formingList(time, years, posts);
        int counter = countYearsInArray(years);
        int[] newYears = new int[counter];
        for (int i = 0; i < counter; i++) {
            newYears[i] = years[i];
        }
        response.setYears(newYears);
        response.setPosts(posts);
        return ResponseEntity.ok().body(response);
    }

    private void formingList(ArrayList<LocalDateTime> time, int[] years, TreeMap<String, Integer> posts) {
        int currentIndex = 0;
        for (LocalDateTime dateTime : time) {
            int year = dateTime.getYear();
            String date = dateTime.toString();
            date = date.substring(0, date.indexOf("T"));
            int i = findElement(years, year);
            if (i < 0) {
                years[currentIndex] = year;
                currentIndex++;
            }
            if (posts.containsKey(date)) {
                posts.put(date, posts.get(date) + 1);
            } else {
                posts.put(date, 1);
            }
        }
    }

    private int countYearsInArray(int[] years){
        int counter = 0;
        for (int i = 0; i < years.length; i++) {
            if (years[i] != 0) {
                counter++;
            } else {
                break;
            }
        }
        return counter;
    }

    private int findElement(int[] array, int key) {
        int index = -1;
        for (int i = 0; i < array.length; i++) {
            if (array[i] == key) {
                index = i;
                break;
            }
        }
        return index;
    }

    public LocalDateTime getStartTime(String dateComponents[]){
        int year = Integer.parseInt(dateComponents[0]);
        int mount = Integer.parseInt(dateComponents[1]);
        int day = Integer.parseInt(dateComponents[2]);
        return LocalDateTime.of(year, mount, day, 0, 0);
    }

    public LocalDateTime getEndTime(String dateComponents[]){
        int year = Integer.parseInt(dateComponents[0]);
        int mount = Integer.parseInt(dateComponents[1]);
        int day = Integer.parseInt(dateComponents[2]);
        return LocalDateTime.of(year, mount, day, 23, 59);
    }
}
