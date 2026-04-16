package com.bookcode.excitationcontroller.service;

import com.bookcode.excitationcontroller.entity.ExcRealData;
import com.bookcode.excitationcontroller.repository.ExcRealDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ExcRealDataService {

    @Autowired
    private ExcRealDataRepository excRealDataRepository;

    public List<ExcRealData> findAll() {
        return excRealDataRepository.findAll();
    }

    public Optional<ExcRealData> findById(Long id) {
        return excRealDataRepository.findById(id);
    }

    public ExcRealData save(ExcRealData excRealData) {
        return excRealDataRepository.save(excRealData);
    }

    public void deleteById(Long id) {
        excRealDataRepository.deleteById(id);
    }

    public ExcRealData update(Long id, ExcRealData excRealData) {
        Optional<ExcRealData> existingData = excRealDataRepository.findById(id);
        if (existingData.isPresent()) {
            ExcRealData data = existingData.get();
            data.setDeviceNo(excRealData.getDeviceNo());
            data.setUg(excRealData.getUg());
            data.setIg(excRealData.getIg());
            data.setFg(excRealData.getFg());
            data.setPg(excRealData.getPg());
            data.setQg(excRealData.getQg());
            data.setIl1(excRealData.getIl1());
            data.setUgSet(excRealData.getUgSet());
            data.setCtrlDeg(excRealData.getCtrlDeg());
            data.setRunState(excRealData.getRunState());
            data.setCollectTime(excRealData.getCollectTime());
            return excRealDataRepository.save(data);
        }
        return null;
    }
}
