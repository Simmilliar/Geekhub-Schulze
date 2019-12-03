package com.geekhubjava.schulze.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class PagesController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/elections")
    public String elections() {
        return "elections";
    }

    @GetMapping("/elections/new")
    public String newElection() {
        return "new_election";
    }

    @GetMapping("/elections/{shareId}")
    public String electionInfo(@PathVariable String shareId, Model model) {
        model.addAttribute("shareId", shareId);
        return "election";
    }

    @GetMapping("/elections/{shareId}/voting")
    public String voting(@PathVariable String shareId, Model model) {
        model.addAttribute("shareId", shareId);
        return "voting";
    }
}
