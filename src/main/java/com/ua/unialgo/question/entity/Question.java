package com.ua.unialgo.question.entity;

import com.ua.unialgo.user.entity.Teacher;
import jakarta.persistence.*;

@Entity
public class Question {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private Teacher teacher;

    protected Question() {}
}
