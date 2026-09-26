package com.ysr.controller;

import com.ysr.model.Transport;
import com.ysr.service.TransportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/transports")
public class TransportController {

    private TransportService transportService;
    @Autowired
    public void setTransportService(TransportService transportService) {
        this.transportService = transportService;
    }

    @PostMapping
    public ResponseEntity<?> addTransport(@RequestBody Transport transport){
        return ResponseEntity.ok(transportService.addTransport(transport));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTransport(@PathVariable Integer id, @RequestBody Transport transport) {
        return ResponseEntity.ok(transportService.updateTransport(id, transport));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransport(@PathVariable Integer id) {
        transportService.deleteTransport(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTransportById(@PathVariable Integer id) {
        return ResponseEntity.ok(transportService.getTransportById(id));
    }

    @GetMapping
    public ResponseEntity<?> getTransports() {
        return ResponseEntity.ok(transportService.getAllTransports());
    }

}
