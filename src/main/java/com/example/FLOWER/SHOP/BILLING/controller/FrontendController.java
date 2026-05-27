package com.example.FLOWER.SHOP.BILLING.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class FrontendController {

    @RequestMapping(value = {
        "/", 
        "/login", 
        "/dashboard", 
        "/all-customers", 
        "/customer", 
        "/customer/**"
    })
    public String forward() {
        return "forward:/index.html";
    }
}
