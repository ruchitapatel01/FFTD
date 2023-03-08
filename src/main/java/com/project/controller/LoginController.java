package com.project.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;


@Controller
public class LoginController {
	
	@GetMapping(value = "/")
	public ModelAndView loadLogin() {
		return new ModelAndView("/login");
	}

	@GetMapping(value = "/admin/index")
	public ModelAndView adminIndex() {
		return new ModelAndView("admin/index");
	}

	@GetMapping(value = "/user/index")
	public ModelAndView userIndex() {
		return new ModelAndView("user/index");
	}

	@RequestMapping(value = "/logout", method = { RequestMethod.POST, RequestMethod.GET })
	public String viewUserDetails(ModelMap model, HttpServletResponse response, HttpServletRequest request) {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null) {
			new SecurityContextLogoutHandler().logout(request, response, auth);
			request.getSession().invalidate();
			request.getSession().setAttribute("statusText", "Logout successfully");
			request.getSession().setAttribute("tempStatus", "success");
		}
		return "/login";
	}

	@GetMapping(value = "/login")
	public ModelAndView load() {

		return new ModelAndView("/login");
	}

	@GetMapping(value = "/403")
	public ModelAndView load403() {
		return new ModelAndView("/login");
	}

	@GetMapping(value = "/error")
	public ModelAndView error() {
		return new ModelAndView("/login");
	}

	@GetMapping(value = "/404")
	public ModelAndView handle404() {
		return new ModelAndView("404");
	}
}