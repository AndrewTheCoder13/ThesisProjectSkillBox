package main.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "tag2post")
@Data
public class TagToPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private int id;

    @ManyToOne()
    @JoinColumn(nullable = false)
    private Post post;

    @ManyToOne()
    @JoinColumn(nullable = false)
    private Tag tag;

}
