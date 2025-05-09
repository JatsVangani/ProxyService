package com.practo.proxy.controller;

import com.practo.proxy.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@Controller
public class HomeController {

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Displays the home page with service selection options.
     *
     * @param model the model to add attributes to
     * @return the view name
     */
    @GetMapping("/home")
    public String home(Model model) {
        // Internal services will be loaded from application.yml
        return "home";
    }

    /**
     * Generates a JWT token for the selected services.
     *
     * @param principal the authenticated user
     * @param selectedServices the list of selected services
     * @param model the model to add attributes to
     * @return the view name
     */
    @PostMapping("/generate-token")
    public String generateToken(@AuthenticationPrincipal OAuth2User principal,
                              @RequestParam List<String> selectedServices,
                              Model model) {
        String username = principal.getAttribute("login");
        String token = jwtUtil.generateToken(username, selectedServices);
        
        model.addAttribute("token", token);
        return "token";
    }
} 