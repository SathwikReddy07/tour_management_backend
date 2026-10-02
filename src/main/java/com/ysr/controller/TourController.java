package com.ysr.controller;

import com.ysr.model.Tours;
import com.ysr.repository.LocationRepo;
import com.ysr.repository.LodgingRepo;
import com.ysr.repository.TransportRepo;
import com.ysr.service.CloudinaryService;
import com.ysr.service.TourService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/admin/tours")
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin(origins = "*")
public class TourController {

    private TourService tourService;
    @Autowired
    public void setTourService(TourService tourService) {
        this.tourService = tourService;
    }

    @PostMapping(consumes = "multipart/form-data")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Tours> addTour(
            @RequestPart("tour") Tours tour,
            @RequestPart("images") List<MultipartFile> images,
            @RequestParam("locationId") Integer locationId,
            @RequestParam("lodgingId") Integer lodgingId,
            @RequestParam("transportId") Integer transportId) throws IOException {
        return ResponseEntity.ok(tourService.addTour(tour, images, locationId, lodgingId, transportId));
    }

    @PutMapping(value = "/{tourId}", consumes = "multipart/form-data")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Tours> updateTour(
            @PathVariable("tourId") Integer tourId,
            @RequestPart("tour") Tours tour,
            @RequestPart("images") List<MultipartFile> images,
            @RequestParam("locationId") Integer locationId,
            @RequestParam("lodgingId") Integer lodgingId,
            @RequestParam("transportId") Integer transportId) throws IOException {
        return ResponseEntity.ok(tourService.updateTourById(tourId, tour, images, locationId, lodgingId, transportId));
    }

    @DeleteMapping("/{tourId}")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTour(@PathVariable("tourId") Integer tourId) {
        tourService.deleteTourById(tourId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{tourId}")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Tours> getTourById(@PathVariable("tourId") Integer tourId) {
        return ResponseEntity.ok(tourService.getTourById(tourId));
    }

    @GetMapping
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Tours>> getAllTours() {
        return ResponseEntity.ok(tourService.getAllTours());
    }

}
