package com.ysr.service;

import com.ysr.exception.LodgingNotFoundException;
import com.ysr.model.Lodging;
import com.ysr.repository.LodgingRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LodgingService {

    private LodgingRepo lodgingRepo;

    @Autowired
    public void setLodgingRepo(LodgingRepo lodgingRepo) {
        this.lodgingRepo = lodgingRepo;
    }

    public Lodging addLodging(Lodging lodging) {
        return lodgingRepo.save(lodging);
    }

    public Lodging getLodgingById(Integer id) {
        return lodgingRepo.findById(id)
                .orElseThrow(() -> new LodgingNotFoundException("Lodging not found with id: " + id));
    }

    public List<Lodging> getAllLodgings() {
        return lodgingRepo.findAll();
    }

    public Lodging updateLodging(Integer id, Lodging lodging) {
        Lodging existingLodging = lodgingRepo.findById(id)
                .orElseThrow(() -> new LodgingNotFoundException("Lodging not found with id: " + id));
        existingLodging.setName(lodging.getName());
        existingLodging.setType(lodging.getType());
        existingLodging.setDescription(lodging.getDescription());
        existingLodging.setAddress(lodging.getAddress());
        existingLodging.setRating(lodging.getRating());
        return lodgingRepo.save(existingLodging);
    }

    public void deleteLodging(Integer id) {
        if (!lodgingRepo.existsById(id)) {
            throw new LodgingNotFoundException("Lodging not found with id: " + id);
        }
        lodgingRepo.deleteById(id);
    }

}