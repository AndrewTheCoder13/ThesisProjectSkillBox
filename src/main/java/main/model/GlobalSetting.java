package main.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "global_settings")
@Data
public class GlobalSetting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private int id;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String value;

}
