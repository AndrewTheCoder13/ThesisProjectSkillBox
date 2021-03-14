package main.service;

import main.model.Post;
import org.javatuples.Triplet;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ArrayService {
    public List<Post> makingSubArray(Triplet<Integer, Integer, ArrayList<Post>> triplet) {
        int begin = triplet.getValue0() * triplet.getValue1();
        int end = begin + triplet.getValue1() > triplet.getValue2().size() ? triplet.getValue2().size() : begin + triplet.getValue1();
        return triplet.getValue2().subList(begin, end);
    }
}
