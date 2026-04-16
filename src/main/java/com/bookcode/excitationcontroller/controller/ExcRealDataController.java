package com.bookcode.excitationcontroller.controller;

import com.bookcode.excitationcontroller.entity.ExcRealData;
import com.bookcode.excitationcontroller.service.ExcRealDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/realdata")
public class ExcRealDataController {

    @Autowired
    private ExcRealDataService excRealDataService;

    @GetMapping
    public List<ExcRealData> findAll() {
        return excRealDataService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExcRealData> findById(@PathVariable Long id) {
        return excRealDataService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ExcRealData save(@RequestBody ExcRealData excRealData) {
        return excRealDataService.save(excRealData);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExcRealData> update(@PathVariable Long id, @RequestBody ExcRealData excRealData) {
        ExcRealData updatedData = excRealDataService.update(id, excRealData);
        if (updatedData != null) {
            return ResponseEntity.ok(updatedData);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        excRealDataService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
