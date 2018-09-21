package ru.home.test.filescan.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Config {
    @Id
    @Column
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    @Column
    private String name;
    @Column
    private String value;
}
