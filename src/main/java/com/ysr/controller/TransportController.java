package com.ysr.controller;

import com.ysr.model.Transport;
import com.ysr.service.TransportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/transports")
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin(origins = "*")
public class TransportController {

    private TransportService transportService;
    @Autowired
    public void setTransportService(TransportService transportService) {
        this.transportService = transportService;
    }

    @PostMapping
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Transport> addTransport(@RequestBody Transport transport){
        return ResponseEntity.ok(transportService.addTransport(transport));
    }

    @PutMapping("/{id}")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Transport> updateTransport(@PathVariable Integer id, @RequestBody Transport transport) {
        return ResponseEntity.ok(transportService.updateTransport(id, transport));
    }

    @DeleteMapping("/{id}")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTransport(@PathVariable Integer id) {
        transportService.deleteTransport(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Transport> getTransportById(@PathVariable Integer id) {
        return ResponseEntity.ok(transportService.getTransportById(id));
    }

    @GetMapping
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Transport>> getTransports() {
        return ResponseEntity.ok(transportService.getAllTransports());
    }

}
