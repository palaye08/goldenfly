package com.goldenfly.web.dtos;

import com.goldenfly.domain.enums.TypeReservationEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchVolDto {
    private Long villeDepartId;
    private Long villeArriveeId;
    private LocalDate dateDepart;
    private LocalDate dateRetour;
    private TypeReservationEnum typeReservation;
    private Integer nombrePassagers = 1;
}