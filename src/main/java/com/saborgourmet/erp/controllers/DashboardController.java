package com.saborgourmet.erp.controllers;

import com.saborgourmet.erp.dto.DashboardGerenteDTO;
import com.saborgourmet.erp.dto.DashboardMeseroDTO;
import com.saborgourmet.erp.services.DashboardService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.saborgourmet.erp.dto.DashboardCajeroDTO;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    // ==========================================
    // DASHBOARD DEL GERENTE
    // ==========================================
    @PreAuthorize("hasRole('GERENTE')")
    @GetMapping("/gerente")
    public ResponseEntity<DashboardGerenteDTO> obtenerDashboardGerente() {

        DashboardGerenteDTO dashboard =
                dashboardService.obtenerDashboardGerente();

        return ResponseEntity.ok(dashboard);
    }

    // ==========================================
    // DASHBOARD DEL MESERO
    // ==========================================
    @PreAuthorize("hasRole('MESERO')")
    @GetMapping("/mesero")
    public ResponseEntity<DashboardMeseroDTO> obtenerDashboardMesero() {

        DashboardMeseroDTO dashboard =
                dashboardService.obtenerDashboardMesero();

        return ResponseEntity.ok(dashboard);
    }

    // ==========================================
    // DASHBOARD DEL CAJERO
    // ==========================================
    @PreAuthorize("hasRole('CAJERO')")
    @GetMapping("/cajero")
    public ResponseEntity<DashboardCajeroDTO> obtenerDashboardCajero() {

        return ResponseEntity.ok(
                dashboardService.obtenerDashboardCajero()
        );
    }

}
