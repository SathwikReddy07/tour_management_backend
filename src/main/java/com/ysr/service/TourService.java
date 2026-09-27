package com.ysr.service;

import com.ysr.exception.LocationNotFoundException;
import com.ysr.exception.LodgingNotFoundException;
import com.ysr.exception.TourNotFoundException;
import com.ysr.exception.TransportNotFoundException;
import com.ysr.model.Location;
import com.ysr.model.Lodging;
import com.ysr.model.Tours;
import com.ysr.model.Transport;
import com.ysr.repository.LocationRepo;
import com.ysr.repository.LodgingRepo;
import com.ysr.repository.TourRepo;
import com.ysr.repository.TransportRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TourService {

    private TourRepo tourRepo;
    @Autowired
    public void setTourRepo(TourRepo tourRepo) {
        this.tourRepo = tourRepo;
    }

    private LocationRepo locationRepo;
    @Autowired
    public void setLocationRepo(LocationRepo locationRepo) {
        this.locationRepo = locationRepo;
    }

    private LodgingRepo lodgingRepo;
    @Autowired
    public void setLodgingRepo(LodgingRepo lodgingRepo) {
        this.lodgingRepo = lodgingRepo;
    }

    private TransportRepo transportRepo;
    @Autowired
    public void setTransportRepo(TransportRepo transportRepo) {
        this.transportRepo = transportRepo;
    }

    public Tours addTour(Tours tour, Integer locationId, Integer transportId, Integer lodgingId) {
        Location location = locationRepo.findById(locationId)
                .orElseThrow(() -> new LocationNotFoundException("Location not found with id: " + locationId));
        Lodging lodging = lodgingRepo.findById(lodgingId)
                .orElseThrow(() -> new LodgingNotFoundException("Lodging not found with id: " + lodgingId));
        Transport transport = transportRepo.findById(transportId)
                .orElseThrow(() -> new TransportNotFoundException("Transport not found with id: " + transportId));
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
        tourRepo.delete(tour);
    }

    public Tours updateTourById(Integer tourId, Tours tour, Integer locationId, Integer transportId, Integer lodgingId) {
        Tours existingTour = tourRepo.findById(tourId)
                .orElseThrow(() -> new TourNotFoundException("Tour not found with id: " + tourId));
        Location location = locationRepo.findById(locationId)
                .orElseThrow(() -> new LocationNotFoundException("Location not found with id: " + locationId));
        Lodging lodging = lodgingRepo.findById(lodgingId)
                .orElseThrow(() -> new LodgingNotFoundException("Lodging not found with id: " + lodgingId));
        Transport transport = transportRepo.findById(transportId)
                .orElseThrow(() -> new TransportNotFoundException("Transport not found with id: " + transportId));
        existingTour.setName(tour.getName());
        existingTour.setDescription(tour.getDescription());
        existingTour.setGuide(tour.getGuide());
        existingTour.setStartDate(tour.getStartDate());
        existingTour.setEndDate(tour.getEndDate());
        existingTour.setPrice(tour.getPrice());
        existingTour.setTicketsAvailable(tour.getTicketsAvailable());
        existingTour.setMeals(tour.getMeals());
        existingTour.setActivities(tour.getActivities());
        existingTour.setImages(tour.getImages());
        existingTour.setLocation(location);
        existingTour.setLodging(lodging);
        existingTour.setTransport(transport);
        return tourRepo.save(existingTour);
    }

}
