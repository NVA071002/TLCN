package com.example.webstore.controllers;

import com.example.webstore.entities.FormData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import static com.google.firebase.auth.FirebaseAuth.*;

@Controller
public class AuthController {
    @PostMapping("/login")
    public String login(@ModelAttribute  FormData formData) {
        try {
            UserRecord userRecord = FirebaseAuth.getInstance().getUserByEmail(formData.getEmail());
        } catch (FirebaseAuthException e) {
            e.printStackTrace();
            return"redirect:/login";
        }
        return"redirect:/product";
    }

    @GetMapping("/login")
    public ModelAndView init() {
        return new ModelAndView("login");
    }
}


