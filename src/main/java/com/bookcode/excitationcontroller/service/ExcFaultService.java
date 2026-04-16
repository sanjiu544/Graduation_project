package com.bookcode.excitationcontroller.service;

import com.bookcode.excitationcontroller.entity.ExcFault;
import com.bookcode.excitationcontroller.repository.ExcFaultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ExcFaultService {

    @Autowired
    private ExcFaultRepository excFaultRepository;

    public List<ExcFault> findAll() {
        return excFaultRepository.findAll();
    }

    public Optional<ExcFault> findById(Long id) {
        return excFaultRepository.findById(id);
    }

    public ExcFault save(ExcFault excFault) {
        return excFaultRepository.save(excFault);
    }

    public void deleteById(Long id) {
        excFaultRepository.deleteById(id);
    }

    public ExcFault update(Long id, ExcFault excFault) {
        Optional<ExcFault> existingFault = excFaultRepository.findById(id);
        if (existingFault.isPresent()) {
            ExcFault fault = existingFault.get();
            fault.setDeviceNo(excFault.getDeviceNo());
            fault.setFaultName(excFault.getFaultName());
            fault.setFaultTime(excFault.getFaultTime());
            fault.setRecoverTime(excFault.getRecoverTime());
            return excFaultRepository.save(fault);
        }
        return null;
    }
}
