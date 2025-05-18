package com.ua.unialgo.assignment.entity;

import java.time.LocalDateTime;

import com.ua.unialgo.user.entity.Teacher;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Assignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn
    private Teacher teacher;

    private String title;

    private String description;

    private LocalDateTime startDate;
    
    private LocalDateTime endDate;
    
    private LocalDateTime creationDate;
}
