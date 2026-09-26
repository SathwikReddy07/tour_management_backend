package com.ysr.controller;

import com.ysr.model.Lodging;
import com.ysr.service.LodgingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/lodgings")
public class LodgingController {

    private LodgingService lodgingService;
    @Autowired
    public void setLodgingService(LodgingService lodgingService) {
        this.lodgingService = lodgingService;
    }

    @PostMapping
    public ResponseEntity<?> addLodging(@RequestBody Lodging lodging){
        return ResponseEntity.ok(lodgingService.addLodging(lodging));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateLodging(@PathVariable Integer id, @RequestBody Lodging lodging) {
        return ResponseEntity.ok(lodgingService.updateLodging(id, lodging));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLodging(@PathVariable Integer id) {
        lodgingService.deleteLodging(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getLodgingById(@PathVariable Integer id) {
        return ResponseEntity.ok(lodgingService.getLodgingById(id));
    }

    @GetMapping
    public ResponseEntity<?> getLodgings() {
        return ResponseEntity.ok(lodgingService.getAllLodgings());
    }

}
