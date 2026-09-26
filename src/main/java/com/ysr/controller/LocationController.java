package com.ysr.controller;

import com.ysr.model.Location;
import com.ysr.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/locations")
public class LocationController {

    private LocationService locationService;
    @Autowired
    public void setLocationService(LocationService locationService) {
        this.locationService = locationService;
    }

    @PostMapping
    public ResponseEntity<?> addLocation(@RequestBody Location location){
        return ResponseEntity.ok(locationService.addLocation(location));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateLocation(@PathVariable Integer id, @RequestBody Location location){
        return ResponseEntity.ok(locationService.updateLocation(id, location));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLocation(@PathVariable Integer id) {
        locationService.deleteLocation(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getLocationById(@PathVariable Integer id) {
        return ResponseEntity.ok(locationService.getLocationById(id));
    }

    @GetMapping
    public ResponseEntity<?> getLocations(){
        return ResponseEntity.ok(locationService.getAllLocations());
    }

}
