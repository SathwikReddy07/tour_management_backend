package com.ysr.controller;

import com.ysr.model.Tours;
import com.ysr.service.TourService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@RestController
@CrossOrigin(origins = "*")
public class TourController {

    private final TourService tourService;

    public TourController(TourService tourService) {
        this.tourService = tourService;
    }

    @PostMapping(value = "/admin/tours", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Tours> addTour(
            @RequestPart("tour") Tours tour,
            @RequestPart("images") List<MultipartFile> images,
            @RequestParam("locationId") Integer locationId,
            @RequestParam("lodgingId") Integer lodgingId,
            @RequestParam("transportId") Integer transportId) throws IOException {
        return ResponseEntity.ok(tourService.addTour(tour, images, locationId, lodgingId, transportId));
    }

    @PutMapping(value = "/admin/tours/{tourId}", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Tours> updateTour(
            @PathVariable("tourId") Integer tourId,
            @RequestPart("tour") Tours tour,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @RequestParam("locationId") Integer locationId,
            @RequestParam("lodgingId") Integer lodgingId,
            @RequestParam("transportId") Integer transportId) throws IOException {
        return ResponseEntity.ok(tourService.updateTourById(tourId, tour, images, locationId, lodgingId, transportId));
    }

    @DeleteMapping("/admin/tours/{tourId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTour(@PathVariable("tourId") Integer tourId) {
        tourService.deleteTourById(tourId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/admin/tours/{tourId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Tours> getTourById(@PathVariable("tourId") Integer tourId) {
        return ResponseEntity.ok(tourService.getTourById(tourId));
    }

    @GetMapping("/admin/tours")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Tours>> getAllTours() {
        return ResponseEntity.ok(tourService.getAllTours());
    }

    @GetMapping("/tours/filter")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<Tours>> getToursWithFilters(
            @RequestParam(value = "country", required = false) String country,
            @RequestParam(value = "lodgingType", required = false) String lodgingType,
            @RequestParam(value = "transportType", required = false) String transportType,
            @RequestParam(value = "minPrice", required = false) BigDecimal minPrice,
            @RequestParam(value = "maxPrice", required = false) BigDecimal maxPrice) {
        List<Tours> filteredTours = tourService.getToursWithFilters(country, lodgingType, transportType, minPrice, maxPrice);
        return ResponseEntity.ok(filteredTours);
    }

}
