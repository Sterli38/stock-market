package com.example.stockmarket.controller.ParticipantController;

import com.example.stockmarket.entity.Participant;
import com.example.stockmarket.service.ParticipantService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/controller")
public class ParticipantController {
    private final ParticipantService service;

    @PostMapping("/create")
    public Participant createParticipant(@RequestBody Participant participant) {
        return createParticipant(participant);
    }

    @GetMapping("/get/{id}")
    public Participant getParticipantById(@PathVariable Long id) {
        return service.getParticipantById(id);
    }

    @PostMapping("/edit")
    public Participant editParticipant(@RequestBody Participant participant) {
        return service.editParticipant(participant);
    }

    @DeleteMapping("/delete")
    public Participant deleteParticipantById(@PathVariable Long id) {
        return service.deleteParticipantById(id);
    }
}
