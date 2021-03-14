package main.service;

import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class RandomGenerator {

    public String generate(int length){
        Random randomGenerator = new Random();
        String path = randomGenerator.ints(48, 122)
                .filter(i -> (i < 57 || i > 65) && (i < 90 || i > 97))
                .mapToObj(i -> (char) i)
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
        return path;
    }
}
