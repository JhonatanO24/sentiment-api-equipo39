package com.hackaton.sentiment.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SentimentRequestDTO {

    @NotBlank(message = "El texto no puede estar vacío")
    @Size(min = 5, max = 500, message = "El texto debe tener entre 5 y 500 caracteres")
    private String text;
}
