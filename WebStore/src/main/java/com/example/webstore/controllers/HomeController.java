package com.example.webstore.controllers;

import com.example.webstore.entities.Product;
import com.example.webstore.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Controller
@RequestMapping("/")
public class HomeController {
    @Autowired
    ProductService productService;
    @GetMapping
    public String getListProduct(Model model) throws ExecutionException, InterruptedException {
        CompletableFuture<List<Product>> futureProduct = productService.getListProduct();
        model.addAttribute("listProduct",futureProduct.join());
        return "index";
    }
}
