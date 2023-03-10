package com.project.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import com.project.VO.LoginVO;
import com.project.VO.MemberVO;
import com.project.service.LoginService;
import com.project.service.MemberService;
import com.project.util.BaseMethods;

@Controller
public class MemberController {

	@Autowired
	private LoginService loginService;

	@Autowired
	private MemberService memberService;

	@Autowired
	private BaseMethods baseMethods;

	@PostMapping(value = "user/addMember")
	public ModelAndView addMember(HttpServletRequest httpServletRequest) {

		System.out.println("enter");
		List<LoginVO> loginVO = loginService.getUserDetails(httpServletRequest.getParameter("username"));
		
		if (!loginVO.isEmpty()) {
			// Check in member table
			Integer memberId = memberService.getMemberId(loginVO.get(0).getUsername());

			if (memberId != null) {
				System.out.println("Already exist");
				return new ModelAndView("user/addMember").addObject("add", new LoginVO())
						.addObject("msg", "User Already exist in your Friend list");
				// User already exist
			} else {
				System.out.println("Add member");
				return new ModelAndView("user/addMember", "MemberList", loginVO).addObject("add", new LoginVO());
			}

		} else {
			// User does not exists
			System.out.println("User does not exist");
			return new ModelAndView("user/addMember").addObject("add", new LoginVO())
					.addObject("msg", "User Does not exist");
		}
	}

	@PostMapping(value = "user/saveMember")
	public ModelAndView saveMember(HttpServletRequest httpServletRequest) {

		String type = httpServletRequest.getParameter("type");
		String role = httpServletRequest.getParameter("role");
		String userName = httpServletRequest.getParameter("username");
//		String loginId = httpServletRequest.getParameter("loginId");
		String currentUsername = baseMethods.getUser();

		System.out.println(userName);

		LoginVO loginVO = new LoginVO();
		loginVO.setLoginId(loginService.getUserId(currentUsername));

		MemberVO memberVO = new MemberVO();
		memberVO.setCategory(type);
		memberVO.setRole(Integer.parseInt(role));
		memberVO.setLevel(0);
		memberVO.setUserName(userName);
		memberVO.setLoginVO(loginVO);

		memberService.insert(memberVO);

		return new ModelAndView("user/index").addObject("add", new LoginVO());
	}
	
	@GetMapping(value = "user/friendsPage")
	public ModelAndView userIndex() {
		
		Integer loginId =  loginService.getUserId(baseMethods.getUser());
		List<MemberVO> friendsList =  memberService.getFriendList(loginId);
		
		return new ModelAndView("user/friends", "add", new LoginVO()).addObject("friendlist", friendsList);
	}

}
