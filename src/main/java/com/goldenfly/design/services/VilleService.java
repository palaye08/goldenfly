package com.goldenfly.design.services;

import com.goldenfly.design.repositories.VilleRepository;
import com.goldenfly.domain.entities.Ville;
import com.goldenfly.web.dtos.CreateVilleDto;
import com.goldenfly.web.dtos.VilleDto;
import com.goldenfly.web.mappers.VilleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class VilleService {

    private final VilleRepository villeRepository;
    private final VilleMapper villeMapper;

    public VilleDto creerVille(CreateVilleDto dto) {
        if (villeRepository.existsByCode(dto.getCode())) {
            throw new RuntimeException("Une ville avec ce code existe déjà");
        }

        Ville ville = villeMapper.toEntity(dto);
        ville = villeRepository.save(ville);
        return villeMapper.toDto(ville);
    }

    @Transactional(readOnly = true)
    public List<VilleDto> getAllVilles() {
        return villeRepository.findAll().stream()
                .map(villeMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<VilleDto> getVillesActives() {
        return villeRepository.findByActif(true).stream()
                .map(villeMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public VilleDto getVilleById(Long id) {
        Ville ville = villeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ville non trouvée"));
        return villeMapper.toDto(ville);
    }

    public VilleDto updateVille(Long id, CreateVilleDto dto) {
        Ville ville = villeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ville non trouvée"));
        villeMapper.updateEntity(ville, dto);
        ville = villeRepository.save(ville);
        return villeMapper.toDto(ville);
    }

    public void deleteVille(Long id) {
        Ville ville = villeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ville non trouvée"));
        ville.setActif(false);
        villeRepository.save(ville);
    }
}