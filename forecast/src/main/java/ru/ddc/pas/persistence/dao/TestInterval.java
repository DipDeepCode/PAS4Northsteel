package ru.ddc.pas.persistence.dao;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
@AllArgsConstructor
public class TestInterval {
    private LocalDateTime start;
    private LocalDateTime finish;
}
