package com.goldenfly.web.mappers;

import com.goldenfly.domain.entities.Paiement;
import com.goldenfly.web.dtos.PaiementDto;
import org.springframework.stereotype.Component;

@Component
public class PaiementMapper {

    public PaiementDto toDto(Paiement entity) {
        if (entity == null) return null;

        PaiementDto dto = new PaiementDto();
        dto.setId(entity.getId());
        dto.setNumeroPaiement(entity.getNumeroPaiement());
        dto.setReservationId(entity.getReservation().getId());
        dto.setNumeroReservation(entity.getReservation().getNumeroReservation());
        dto.setMontant(entity.getMontant());
        dto.setModePaiement(entity.getModePaiement());
        dto.setStatut(entity.getStatut());
        dto.setTransactionId(entity.getTransactionId());
        dto.setNumeroTelephone(entity.getNumeroTelephone());
        dto.setNumeroRecu(entity.getNumeroRecu());
        dto.setReferenceExterne(entity.getReferenceExterne());
        dto.setDatePaiement(entity.getDatePaiement());
        dto.setDateCreation(entity.getDateCreation());
        dto.setCommentaire(entity.getCommentaire());
        return dto;
    }
}