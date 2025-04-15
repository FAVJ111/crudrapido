package com.example.crudrapido.service;


import com.example.crudrapido.entity.Activity;
import com.example.crudrapido.repository.ActivityRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ActivityDataLoader implements CommandLineRunner {

    private final ActivityRepository activityRepository;

    public ActivityDataLoader(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (activityRepository.count() == 0) {
            List<Activity> activities = List.of(
                new Activity("Natación"),
                new Activity("Fútbol"),
                new Activity("Pintura"),
                new Activity("Robótica"),
                new Activity("Música"),
                new Activity("Teatro"),
                new Activity("Ajedrez")
            );

            activityRepository.saveAll(activities);
            System.out.println("Actividades precargadas en la base de datos.");
        }
    }
}