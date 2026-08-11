package br.edu.divulgaambulantes.controller;

import br.edu.divulgaambulantes.dto.RankingVendedorDTO;
import br.edu.divulgaambulantes.service.RankingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ranking")
@CrossOrigin(origins = "*")
public class RankingController {

    private final RankingService service;

    public RankingController(RankingService service) {
        this.service = service;
    }

    @GetMapping("/vendedores")
    public ResponseEntity<List<RankingVendedorDTO>>
    rankingVendedores() {

        List<RankingVendedorDTO> ranking =
                service.buscarTop20();

        return ResponseEntity.ok(ranking);
    }
}