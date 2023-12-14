package com.example.webstore.controllers;

import com.example.webstore.entities.Invoice;
import com.example.webstore.entities.User;
import com.example.webstore.model.UpdateInvoiceRequest;
import com.example.webstore.services.InvoiceService;
import com.example.webstore.services.Userservice;
import com.google.firebase.auth.FirebaseAuthException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Controller
@RequestMapping("/invoice")
@AllArgsConstructor
public class InvoiceController {
    private final Userservice userservice;
    private final InvoiceService invoiceService;
    @GetMapping
    public String getAllInvoice(Model model) throws ExecutionException, InterruptedException, FirebaseAuthException {
        List<Invoice> tmp=invoiceService.getListInvoice();
        List<Integer> dataChart=invoiceService.getPriceByUser();
        List<String> userEmailList=userservice.getListName();
        model.addAttribute("listInvoice",tmp);
        model.addAttribute("dataChart",dataChart);
        model.addAttribute("userEmailList",userEmailList);
        return "invoice";
    }
    @PostMapping
    public void updateInvoice(@RequestBody UpdateInvoiceRequest updateInvoiceRequest) throws ExecutionException, FirebaseAuthException, InterruptedException {
        invoiceService.updateStatusInvoice(updateInvoiceRequest);
    }
}
