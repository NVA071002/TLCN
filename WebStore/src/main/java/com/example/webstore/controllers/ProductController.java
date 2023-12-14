package com.example.webstore.controllers;

import com.example.webstore.entities.Product;
import com.example.webstore.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Controller
@RequestMapping("/product")
public class ProductController {
    @Autowired
    ProductService productService;

//    @GetMapping("/")
//    public String getProduct(Model model){
//        return "index";
//    }
    @GetMapping("/test")
    @ResponseBody
    public Product getProduct(@RequestParam @Validated String id) throws ExecutionException, InterruptedException {
        CompletableFuture<Product> futureProduct = productService.findProductById(id);
        return futureProduct.join();
    }
    @GetMapping
    public String getListProduct(Model model) throws ExecutionException, InterruptedException {
        CompletableFuture<List<Product>> futureProduct = productService.getListProduct();
        model.addAttribute("listProduct",futureProduct.join());
        return "index";
    }
    @PostMapping
    @ResponseBody
    public String insertProduct(@RequestBody Product product){
        String mess=productService.insertProuct(product);
        return mess;
    }
    @DeleteMapping("/{id}")
    @ResponseBody
    public void deletProduct(@PathVariable int id){
        productService.deleteProduct(id);

    }
    @PutMapping
    @ResponseBody
    public String updateProduct(@RequestBody Product product){
        String mess=productService.updateProduct(product);
        return mess;
    }
}
