package com.tomakehurst.springclosure.example;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ExampleController {

	@RequestMapping(value="/")
	public String openHomepage(Model model) {
		String serverTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date());
		model.addAttribute("serverTime", serverTime);
		return "com.tomakehurst.index";
	}
}
