package com.bookcode.excitationcontroller.controller;

import com.bookcode.excitationcontroller.entity.ExcFault;
import com.bookcode.excitationcontroller.service.ExcFaultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fault")
public class ExcFaultController {

    @Autowired
    private ExcFaultService excFaultService;

    @GetMapping
    public List<ExcFault> findAll() {
        return excFaultService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExcFault> findById(@PathVariable Long id) {
        return excFaultService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ExcFault save(@RequestBody ExcFault excFault) {
        return excFaultService.save(excFault);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExcFault> update(@PathVariable Long id, @RequestBody ExcFault excFault) {
        ExcFault updatedFault = excFaultService.update(id, excFault);
        if (updatedFault != null) {
            return ResponseEntity.ok(updatedFault);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        excFaultService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
