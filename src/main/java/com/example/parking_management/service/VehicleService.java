package com.example.parking_management.service;

import com.example.parking_management.model.Vehicle;
import com.example.parking_management.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VehicleService {

  @Autowired
    VehicleRepository vehicleRepository;

  public List<Vehicle> getVehicle()
  {
      return vehicleRepository.findAll();
  }


  public Optional<Vehicle> getVehicle(int vehicleId)
  {
      return vehicleRepository.findById(vehicleId);
  }

  public void saveOrUpdate(Vehicle vehicle)
  {
      vehicleRepository.save(vehicle);
  }

  public void delete(int   vehicleId)
  {
      vehicleRepository.deleteById(vehicleId);
  }
}
