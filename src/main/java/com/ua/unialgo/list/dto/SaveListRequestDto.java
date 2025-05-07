package com.ua.unialgo.list.dto;

import java.time.LocalDateTime;

public record SaveListRequestDto(String title, String description, LocalDateTime startDate, LocalDateTime endDate) { }
