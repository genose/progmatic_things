package org.genose.java_spring_101;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/doctor")
public class DoctorController {

    @GetMapping("")
    public ResponseEntity<String> getDoctorById() {
        return ResponseEntity.ok(" Le Docteur est Introuvable ");
    }

    @GetMapping("/{id}")
    public ResponseEntity<String> getDoctorById(@PathVariable() int id) {
        return ResponseEntity.ok(" Docteur " + id);
    }

    @GetMapping("/{id}/{opt}")
    public ResponseEntity<String> getDoctorById(@PathVariable() int id, @PathVariable() String opt) {
        return ResponseEntity.ok(" Docteur "+opt +"(" + id +")");
    }


}
