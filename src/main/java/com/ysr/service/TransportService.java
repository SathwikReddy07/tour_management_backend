package com.ysr.service;

import com.ysr.model.Transport;
import com.ysr.repository.TransportRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransportService {

    private TransportRepo transportRepo;
    @Autowired
    public void setTransportRepo(TransportRepo transportRepo) {
        this.transportRepo = transportRepo;
    }

    public Transport addTransport(Transport transport) {
        return transportRepo.save(transport);
    }

    public Transport getTransportById(Integer id) {
        return transportRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Transport not found with id: " + id));
    }

    public List<Transport> getAllTransports() {
        return transportRepo.findAll();
    }

    public Transport updateTransport(Integer id, Transport transport) {
        Transport existingTransport = transportRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Transport not found with id: " + id));
        existingTransport.setName(transport.getName());
        existingTransport.setType(transport.getType());
        existingTransport.setEstimatedTime(transport.getEstimatedTime());
        existingTransport.setDescription(transport.getDescription());
        return transportRepo.save(existingTransport);
    }

    public void deleteTransport(Integer id) {
        if (!transportRepo.existsById(id)) {
            throw new RuntimeException("Transport not found with id: " + id);
        }
        transportRepo.deleteById(id);
    }
}
