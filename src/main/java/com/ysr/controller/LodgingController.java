package com.ysr.controller;

import com.ysr.model.Lodging;
import com.ysr.service.LodgingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/lodgings")
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin(origins = "*")
public class LodgingController {

    private LodgingService lodgingService;
    @Autowired
    public void setLodgingService(LodgingService lodgingService) {
        this.lodgingService = lodgingService;
    }

    @PostMapping
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Lodging> addLodging(@RequestBody Lodging lodging){
        return ResponseEntity.ok(lodgingService.addLodging(lodging));
    }

    @PutMapping("/{id}")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Lodging> updateLodging(@PathVariable Integer id, @RequestBody Lodging lodging) {
        return ResponseEntity.ok(lodgingService.updateLodging(id, lodging));
    }

    @DeleteMapping("/{id}")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteLodging(@PathVariable Integer id) {
        lodgingService.deleteLodging(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Lodging> getLodgingById(@PathVariable Integer id) {
        return ResponseEntity.ok(lodgingService.getLodgingById(id));
    }

    @GetMapping
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Lodging>> getLodgings() {
        return ResponseEntity.ok(lodgingService.getAllLodgings());
    }

}
