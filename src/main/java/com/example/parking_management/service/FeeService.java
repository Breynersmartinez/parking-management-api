package com.example.parking_management.service;


import com.example.parking_management.model.Fee;
import com.example.parking_management.repository.FeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FeeService {

    @Autowired
    FeeRepository feeRepository;

public List<Fee> getFee()
{
    return feeRepository.findAll();
}

public Optional<Fee> getFee(int id)
{
    return feeRepository.findById(id);
}

public void saveOrUpdate(Fee fee)
{
    feeRepository.save(fee);
}

public void delete(int id)
{
    feeRepository.deleteById(id);
}


}
