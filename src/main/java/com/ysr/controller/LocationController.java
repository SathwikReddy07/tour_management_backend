package com.ysr.controller;

import com.ysr.model.Location;
import com.ysr.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/locations")
@CrossOrigin(origins = "*")
public class LocationController {

    private LocationService locationService;
    @Autowired
    public void setLocationService(LocationService locationService) {
        this.locationService = locationService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Location> addLocation(@RequestBody Location location){
        return ResponseEntity.ok(locationService.addLocation(location));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Location> updateLocation(@PathVariable Integer id, @RequestBody Location location){
        return ResponseEntity.ok(locationService.updateLocation(id, location));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteLocation(@PathVariable Integer id) {
        locationService.deleteLocation(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Location> getLocationById(@PathVariable Integer id) {
        return ResponseEntity.ok(locationService.getLocationById(id));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Location>> getLocations(){
        return ResponseEntity.ok(locationService.getAllLocations());
    }

}
