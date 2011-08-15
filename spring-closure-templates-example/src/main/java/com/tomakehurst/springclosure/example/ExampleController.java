package com.tomakehurst.springclosure.example;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ExampleController {

	@RequestMapping(value="/")
	public String openHomepage(Model model) {
		addServerTimeStringToModel(model);
		model.addAttribute("words", new ArrayList<String>());
		return "com.tomakehurst.index";
	}
	
	@RequestMapping(value="/server-time")
	public String getServerTime(Model model) {
		addServerTimeStringToModel(model);
		return "com.tomakehurst.serverTime";
	}

	private void addServerTimeStringToModel(Model model) {
		String serverTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		model.addAttribute("serverTime", serverTime);
	}
	
	
}
