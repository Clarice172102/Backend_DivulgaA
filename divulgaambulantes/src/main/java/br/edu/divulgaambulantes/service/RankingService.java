package br.edu.divulgaambulantes.service;

import br.edu.divulgaambulantes.dto.RankingVendedorDTO;
import br.edu.divulgaambulantes.repository.RankingJdbcRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RankingService {

    private final RankingJdbcRepository repository;

    public RankingService(
            RankingJdbcRepository repository
    ) {
        this.repository = repository;
    }

    public List<RankingVendedorDTO> buscarTop20() {
        return repository.buscarTop20();
    }
}