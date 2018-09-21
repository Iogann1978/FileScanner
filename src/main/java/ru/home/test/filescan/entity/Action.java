package ru.home.test.filescan.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Action {
    @Id
    @Column
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    @Column
    private String descript;
    @Column
    private LocalDateTime localDateTime;

    public Action(String descript, LocalDateTime localDateTime) {
        this.descript = descript;
        this.localDateTime = localDateTime;
    }
}
