package com.goldenfly.web.mappers;

import com.goldenfly.domain.entities.Reservation;
import com.goldenfly.web.dtos.BilletDto;
import com.goldenfly.web.dtos.ReservationDto;
import org.springframework.stereotype.Component;

@Component
public class ReservationMapper {

    private final UtilisateurMapper utilisateurMapper;
    private final VolMapper volMapper;

    public ReservationMapper(UtilisateurMapper utilisateurMapper, VolMapper volMapper) {
        this.utilisateurMapper = utilisateurMapper;
        this.volMapper = volMapper;
    }

    public ReservationDto toDto(Reservation entity) {
        if (entity == null) return null;

        ReservationDto dto = new ReservationDto();
        dto.setId(entity.getId());
        dto.setNumeroReservation(entity.getNumeroReservation());
        dto.setUtilisateur(utilisateurMapper.toDto(entity.getUtilisateur()));
        dto.setVolAller(volMapper.toDto(entity.getVolAller()));
        if (entity.getVolRetour() != null) {
            dto.setVolRetour(volMapper.toDto(entity.getVolRetour()));
        }
        dto.setTypeReservation(entity.getTypeReservation());
        dto.setClasseVol(entity.getClasseVol());
        dto.setDateDepart(entity.getDateDepart());
        dto.setDateRetour(entity.getDateRetour());
        dto.setPrixTotal(entity.getPrixTotal());
        dto.setStatut(entity.getStatut());
        dto.setQrCode(entity.getQrCode());
        dto.setDateEmbarquement(entity.getDateEmbarquement());
        dto.setNombrePassagers(entity.getNombrePassagers());
        dto.setDateCreation(entity.getDateCreation());

        // AJOUT: Mapper l'attribut estPaye
        dto.setEstPaye(entity.getEstPaye());

        return dto;
    }

    public BilletDto toBilletDto(Reservation entity) {
        if (entity == null) return null;

        BilletDto dto = new BilletDto();
        dto.setNumeroReservation(entity.getNumeroReservation());
        dto.setNomPassager(entity.getUtilisateur().getNom());
        dto.setPrenomPassager(entity.getUtilisateur().getPrenom());
        dto.setEmailPassager(entity.getUtilisateur().getEmail());
        dto.setTelephonePassager(entity.getUtilisateur().getTelephone());

        // Vol Aller
        dto.setNumeroVolAller(entity.getVolAller().getNumeroVol());
        dto.setVilleDepartAller(entity.getVolAller().getVilleDepart().getNom());
        dto.setCodeVilleDepartAller(entity.getVolAller().getVilleDepart().getCode());
        dto.setVilleArriveeAller(entity.getVolAller().getVilleArrivee().getNom());
        dto.setCodeVilleArriveeAller(entity.getVolAller().getVilleArrivee().getCode());
        dto.setDateDepartAller(entity.getDateDepart());
        dto.setHeureDepartAller(entity.getVolAller().getHeureDepart());
        dto.setHeureArriveeAller(entity.getVolAller().getHeureArrivee());
        dto.setDureeVolAller(entity.getVolAller().getDureeVol());

        // Vol Retour (si existe)
        if (entity.getVolRetour() != null) {
            dto.setNumeroVolRetour(entity.getVolRetour().getNumeroVol());
            dto.setVilleDepartRetour(entity.getVolRetour().getVilleDepart().getNom());
            dto.setCodeVilleDepartRetour(entity.getVolRetour().getVilleDepart().getCode());
            dto.setVilleArriveeRetour(entity.getVolRetour().getVilleArrivee().getNom());
            dto.setCodeVilleArriveeRetour(entity.getVolRetour().getVilleArrivee().getCode());
            dto.setDateDepartRetour(entity.getDateRetour());
            dto.setHeureDepartRetour(entity.getVolRetour().getHeureDepart());
            dto.setHeureArriveeRetour(entity.getVolRetour().getHeureArrivee());
            dto.setDureeVolRetour(entity.getVolRetour().getDureeVol());
        }

        dto.setClasseVol(entity.getClasseVol());
        dto.setNombrePassagers(entity.getNombrePassagers());
        dto.setPrixTotal(entity.getPrixTotal());
        dto.setQrCode(entity.getQrCode());
        dto.setStatut(entity.getStatut().name());

        // AJOUT: Mapper l'attribut estPaye pour le billet aussi
        dto.setEstPaye(entity.getEstPaye());

        return dto;
    }
}