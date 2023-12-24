package com.example.webstore.controllers;

import com.example.webstore.entities.Product;
import com.example.webstore.entities.User;
import com.example.webstore.model.UpdateUserRequest;
import com.example.webstore.services.Userservice;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.cloud.FirestoreClient;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Controller
@RequestMapping("/user")
@AllArgsConstructor
public class UserController {
    private final Userservice userservice;
    @GetMapping
    public String getUser(Model model) throws ExecutionException, InterruptedException, FirebaseAuthException {
        List<User> tmp=userservice.getListUser();
        model.addAttribute("listUser",tmp);
        return "user";
    }
    @PutMapping
    public void updateUser(@RequestBody UpdateUserRequest user) throws ExecutionException, InterruptedException, FirebaseAuthException {
        userservice.updateUser(user);
    }
    @DeleteMapping("/{email}")
    @ResponseBody
    public void deleteUser(@PathVariable String email) throws ExecutionException, InterruptedException, FirebaseAuthException {
        userservice.deleteUser(email);


    }
}
