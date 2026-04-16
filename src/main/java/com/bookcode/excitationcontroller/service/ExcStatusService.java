package com.bookcode.excitationcontroller.service;

import com.bookcode.excitationcontroller.entity.ExcStatus;
import com.bookcode.excitationcontroller.repository.ExcStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ExcStatusService {

    @Autowired
    private ExcStatusRepository excStatusRepository;

    public List<ExcStatus> findAll() {
        return excStatusRepository.findAll();
    }

    public Optional<ExcStatus> findById(Long id) {
        return excStatusRepository.findById(id);
    }

    public ExcStatus save(ExcStatus excStatus) {
        return excStatusRepository.save(excStatus);
    }

    public void deleteById(Long id) {
        excStatusRepository.deleteById(id);
    }

    public ExcStatus update(Long id, ExcStatus excStatus) {
        Optional<ExcStatus> existingStatus = excStatusRepository.findById(id);
        if (existingStatus.isPresent()) {
            ExcStatus status = existingStatus.get();
            status.setDeviceNo(excStatus.getDeviceNo());
            status.setIdl(excStatus.getIdl());
            status.setIql(excStatus.getIql());
            status.setYwarn(excStatus.getYwarn());
            status.setYerr(excStatus.getYerr());
            status.setYpss(excStatus.getYpss());
            status.setMqlf(excStatus.getMqlf());
            status.setOverUg(excStatus.getOverUg());
            status.setOverIg(excStatus.getOverIg());
            status.setPtBreak(excStatus.getPtBreak());
            status.setUpdateTime(excStatus.getUpdateTime());
            return excStatusRepository.save(status);
        }
        return null;
    }
}
