package com.ua.unialgo.assignment.dto;

import java.time.LocalDateTime;

public record SaveAssignmentRequestDto(String title, String description, LocalDateTime startDate, LocalDateTime endDate) { }
