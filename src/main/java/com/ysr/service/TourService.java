package com.ysr.service;

import com.ysr.exception.LocationNotFoundException;
import com.ysr.exception.LodgingNotFoundException;
import com.ysr.exception.TourNotFoundException;
import com.ysr.exception.TransportNotFoundException;
import com.ysr.model.*;
import com.ysr.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class TourService {

    private final TourRepo tourRepo;
    private final LocationRepo locationRepo;
    private final LodgingRepo lodgingRepo;
    private final TransportRepo transportRepo;
    private final BookingRepo bookingRepo;
    private final CloudinaryService cloudinaryService;

    public TourService(TourRepo tourRepo, LocationRepo locationRepo, LodgingRepo lodgingRepo,
                       TransportRepo transportRepo, BookingRepo bookingRepo, CloudinaryService cloudinaryService) {
        this.tourRepo = tourRepo;
        this.locationRepo = locationRepo;
        this.lodgingRepo = lodgingRepo;
        this.transportRepo = transportRepo;
        this.bookingRepo = bookingRepo;
        this.cloudinaryService = cloudinaryService;

    }

    @Transactional
    public Tours addTour(Tours tour, List<MultipartFile> images,
                         Integer locationId, Integer lodgingId, Integer transportId) throws IOException {
        Location location = locationRepo.findById(locationId)
                .orElseThrow(() -> new LocationNotFoundException("Location not found with id: " + locationId));
        Lodging lodging = lodgingRepo.findById(lodgingId)
                .orElseThrow(() -> new LodgingNotFoundException("Lodging not found with id: " + lodgingId));
        Transport transport = transportRepo.findById(transportId)
                .orElseThrow(() -> new TransportNotFoundException("Transport not found with id: " + transportId));
        if (tour.getStartDate() == null || tour.getEndDate() == null) {
            throw new IllegalArgumentException("Start date and end date cannot be null");
        }
        if (tour.getEndDate().isBefore(tour.getStartDate())) {
            throw new IllegalArgumentException("End date cannot be before start date");
        }
        List<String> imageUrls = new ArrayList<>();
        for (MultipartFile image : images) {
            if (image.isEmpty()) {
                throw new IOException("Image is empty");
            }
            String imageUrl = cloudinaryService.uploadImage(image);
            imageUrls.add(imageUrl);
        }
        tour.setImages(imageUrls);
        tour.setLocation(location);
        tour.setLodging(lodging);
        tour.setTransport(transport);
        return tourRepo.save(tour);
    }

    public List<Tours> getAllTours() {
        return tourRepo.getAllToursWithDetails();
    }

    public Tours getTourById(Integer tourId) {
        Tours tour = tourRepo.getTourByIdWithDetails(tourId);
        if (tour == null)
            throw new TourNotFoundException("Tour not found with id: " + tourId);
        return tour;
    }

    public void deleteTourById(Integer tourId) {
        Tours tour = tourRepo.findById(tourId)
                .orElseThrow(() -> new TourNotFoundException("Tour not found with id: " + tourId));
        List<Bookings> bookings = bookingRepo.findByTourId(tourId);
        if (!(bookings == null || bookings.isEmpty())) {
            throw new IllegalStateException("Cannot delete tour with id: " + tourId + " because it has associated bookings.");
        }
        tourRepo.delete(tour);
    }

    @Transactional
    public Tours updateTourById(Integer tourId, Tours tour, List<MultipartFile> images,
                                Integer locationId, Integer lodgingId, Integer transportId) throws IOException {
        Tours existingTour = tourRepo.findByIdForUpdate(tourId)
                .orElseThrow(() -> new TourNotFoundException("Tour not found with id: " + tourId));
        Location location = locationRepo.findById(locationId)
                .orElseThrow(() -> new LocationNotFoundException("Location not found with id: " + locationId));
        Lodging lodging = lodgingRepo.findById(lodgingId)
                .orElseThrow(() -> new LodgingNotFoundException("Lodging not found with id: " + lodgingId));
        Transport transport = transportRepo.findById(transportId)
                .orElseThrow(() -> new TransportNotFoundException("Transport not found with id: " + transportId));
        if (tour.getStartDate() == null || tour.getEndDate() == null) {
            throw new IllegalArgumentException("Start date and end date cannot be null");
        }
        if (tour.getEndDate().isBefore(tour.getStartDate())) {
            throw new IllegalArgumentException("End date cannot be before start date");
        }
        List<String> imageUrls = new ArrayList<>();
        if (images == null || images.isEmpty()) {
            imageUrls = existingTour.getImages();
        } else {
            for (MultipartFile image : images) {
                if (image.isEmpty()) {
                    throw new IOException("Image is empty");
                }
                String imageUrl = cloudinaryService.uploadImage(image);
                imageUrls.add(imageUrl);
            }
            for (String imageUrl : existingTour.getImages()) {
                cloudinaryService.deleteImage(imageUrl);
            }
        }
        existingTour.setName(tour.getName());
        existingTour.setDescription(tour.getDescription());
        existingTour.setGuide(tour.getGuide());
        existingTour.setStartDate(tour.getStartDate());
        existingTour.setEndDate(tour.getEndDate());
        existingTour.setPrice(tour.getPrice());
        existingTour.setMeals(tour.getMeals());
        existingTour.setActivities(tour.getActivities());
        existingTour.setImages(imageUrls);
        existingTour.setLocation(location);
        existingTour.setLodging(lodging);
        existingTour.setTransport(transport);
        return tourRepo.save(existingTour);
    }

    public List<Tours> getToursWithFilters(String country, String lodgingType, String transportType,
                                           BigDecimal minPrice, BigDecimal maxPrice) {
        if (country != null && country.isBlank())
            country = null;
        if (lodgingType != null && lodgingType.isBlank())
            lodgingType = null;
        if (transportType != null && transportType.isBlank())
            transportType = null;
        if (minPrice != null && minPrice.compareTo(BigDecimal.ZERO) < 0)
            minPrice = null;
        if (maxPrice != null && maxPrice.compareTo(BigDecimal.ZERO) < 0)
            maxPrice = null;
        if (minPrice != null && maxPrice != null && minPrice.compareTo(maxPrice) > 0)
            throw new IllegalArgumentException("Min price cannot be greater than max price");
        return tourRepo.filterTours(country, lodgingType, transportType, minPrice, maxPrice);
    }

}
