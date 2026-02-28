package com.example.demo;

import javafx.application.Application;

public class Launcher {
    public static void main(String[] args) {

        try {
            Seeder.seed();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Application.launch(HelloApplication.class, args);
    }
}
