package com.bookcode.excitationcontroller.controller;

import com.bookcode.excitationcontroller.entity.ExcStatus;
import com.bookcode.excitationcontroller.service.ExcStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/status")
public class ExcStatusController {

    @Autowired
    private ExcStatusService excStatusService;

    @GetMapping
    public List<ExcStatus> findAll() {
        return excStatusService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExcStatus> findById(@PathVariable Long id) {
        return excStatusService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ExcStatus save(@RequestBody ExcStatus excStatus) {
        return excStatusService.save(excStatus);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExcStatus> update(@PathVariable Long id, @RequestBody ExcStatus excStatus) {
        ExcStatus updatedStatus = excStatusService.update(id, excStatus);
        if (updatedStatus != null) {
            return ResponseEntity.ok(updatedStatus);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        excStatusService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
