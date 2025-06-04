package com.ua.unialgo.question.entity;

import com.ua.unialgo.user.entity.Teacher;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn
    private Teacher teacher;

    private String title;

    private String statement;

    @ElementCollection
    @CollectionTable(name = "question_allowed_languages", joinColumns = @JoinColumn(name = "question_id"))
    @Column(name = "language_id")
    private List<Integer> allowedLanguages;

    @Column(name = "time_limit")
    private Float timeLimit;

    @Column(name = "memory_limit")
    private Integer memoryLimit;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TestCase> testCases;
}
