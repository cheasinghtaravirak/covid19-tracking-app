package com.springboot.covid19trackingapp.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.springboot.covid19trackingapp.models.LocationStat;
import com.springboot.covid19trackingapp.services.Covid19DataService;

@Controller
public class home {

	@Autowired
	private Covid19DataService covid19DataService;

	@GetMapping("/")
	public String home(Model model) {

		List<LocationStat> locationStats = new ArrayList<LocationStat>();
		locationStats = covid19DataService.getAllLocationStats();
		int totalCases = locationStats.stream().mapToInt(locationStat -> locationStat.getLatestTotalCases()).sum();
		int totalNewCases = locationStats.stream().mapToInt(locationStat -> locationStat.getDiffFromPrevDay()).sum();

		model.addAttribute("locationStats", covid19DataService.getAllLocationStats());
		model.addAttribute("totalCases", totalCases);
		model.addAttribute("totalNewCases", totalNewCases);
		return "home";
	}
}
