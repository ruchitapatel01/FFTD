package com.project.controller;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.project.VO.EventGalleryVO;
import com.project.VO.EventVO;
import com.project.VO.FriendsGalleryVO;
import com.project.VO.LoginVO;
import com.project.VO.MemberVO;
import com.project.service.EventGalleryService;
import com.project.service.EventService;
import com.project.service.FriendsGalleryService;
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
	private FriendsGalleryService friendsGalleryService;

	@Autowired
	private EventService eventService;
	
	@Autowired
	private EventGalleryService eventGalleryService;
	
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
				return new ModelAndView("user/addMember").addObject("add", new LoginVO()).addObject("msg",
						"User Already exist in your Friend list");
				// User already exist
			} else {
				System.out.println("Add member");
				return new ModelAndView("user/addMember", "MemberList", loginVO).addObject("add", new LoginVO());
			}

		} else {
			// User does not exists
			System.out.println("User does not exist");
			return new ModelAndView("user/addMember").addObject("add", new LoginVO()).addObject("msg",
					"User Does not exist");
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

		if (type.equalsIgnoreCase("Friend")) {
			memberVO.setLevel(0);
		} else if (type.equalsIgnoreCase("Grand Father") || type.equalsIgnoreCase("Grand Mother")) {
			memberVO.setLevel(1);
		} else if (type.equalsIgnoreCase("Father") || type.equalsIgnoreCase("Mother")) {
			memberVO.setLevel(2);
		} else if (type.equalsIgnoreCase("Brother") || type.equalsIgnoreCase("Sister")) {
			memberVO.setLevel(3);
		} else if (type.equalsIgnoreCase("Son") || type.equalsIgnoreCase("Daughter")) {
			memberVO.setLevel(4);
		}
		memberVO.setCategory(type);
		memberVO.setRole(Integer.parseInt(role));
		memberVO.setUserName(userName);
		memberVO.setLoginVO(loginVO);

		memberService.insert(memberVO);

		return new ModelAndView("user/index").addObject("add", new LoginVO());
	}

	@GetMapping(value = "user/friendsPage")
	public ModelAndView userIndex() {

		Integer loginId = loginService.getUserId(baseMethods.getUser());
		List<MemberVO> friendsList = memberService.getFriendList(loginId);

		return new ModelAndView("user/friends", "add", new LoginVO()).addObject("friendlist", friendsList);
	}

	@PostMapping(value = "user/friendPost")
	public ModelAndView friendsPost(HttpServletRequest httpServletRequest) {

		String name = httpServletRequest.getParameter("friendName");
		System.out.println(name);

		List<FriendsGalleryVO> friendGalleryList = friendsGalleryService.getFriendsGalleryList(baseMethods.getUser(),
				name);

		return new ModelAndView("user/friendsGallery").addObject("add", new LoginVO()).addObject("friendName", name)
				.addObject("friendsData", new FriendsGalleryVO()).addObject("galleryList", friendGalleryList);
	}

	@PostMapping(value = "user/saveImage")
	public ModelAndView saveGallery(FriendsGalleryVO friendsGalleryVO, HttpServletRequest httpServletRequest) {

		String currentUsername = baseMethods.getUser();
		String friendsUser = httpServletRequest.getParameter("friendsUsername");
		
		LoginVO loginVO = new LoginVO();
		loginVO.setLoginId(loginService.getUserId(currentUsername));
		friendsGalleryVO.setUser(loginVO);

		LoginVO loginVO2 = new LoginVO();
		loginVO2.setLoginId(loginService.getUserId(friendsUser));
		friendsGalleryVO.setFriend(loginVO2);

		friendsGalleryService.insert(friendsGalleryVO);
		List<FriendsGalleryVO> friendGalleryList = friendsGalleryService.getFriendsGalleryList(baseMethods.getUser(),
				friendsUser);

		return new ModelAndView("user/friendsGallery").addObject("add", new LoginVO())
				.addObject("friendName", friendsUser).addObject("friendsData", new FriendsGalleryVO())
				.addObject("galleryList", friendGalleryList);
	}

	@PostMapping(value = "user/deletePost")
	public ModelAndView deleteImage(HttpServletRequest httpServletRequest) {

		Integer fileId = Integer.parseInt(httpServletRequest.getParameter("fileId"));
		String userName = httpServletRequest.getParameter("username");
		String friendsName = httpServletRequest.getParameter("friendName");

		friendsGalleryService.deletePost(fileId);
		List<FriendsGalleryVO> friendGalleryList = friendsGalleryService.getFriendsGalleryList(userName, friendsName);

		return new ModelAndView("user/friendsGallery").addObject("add", new LoginVO())
				.addObject("friendName", friendsName).addObject("friendsData", new FriendsGalleryVO())
				.addObject("galleryList", friendGalleryList);
	}

	@GetMapping(value = "user/familyPage")
	public ModelAndView family() {

		List<MemberVO> familyList = memberService.getFamilyList(loginService.getUserId(baseMethods.getUser()));
		
		return new ModelAndView("user/family", "add", new LoginVO()).addObject("familylist", familyList);
	}
	
	@RequestMapping(value = "user/familyGallery")
	public ModelAndView familyGallery(@RequestParam(required = false) String username) {
		
		return new ModelAndView("user/familyGallery", "add", new LoginVO());
	}

	@GetMapping(value = "user/eventsPage")
	public ModelAndView eventsPage() {
		
		List<EventVO> eventList =  eventService.getEventList(loginService.getUserId(baseMethods.getUser()));
		
		return new ModelAndView("user/events", "add", new LoginVO()).addObject("eventlist", eventList);
	}
	
	@PostMapping(value = "user/saveEvent")
	public ModelAndView saveEvent(HttpServletRequest httpServletRequest) {

		String eventname = httpServletRequest.getParameter("eventName");
		LoginVO loginVO = new LoginVO();
		loginVO.setLoginId(loginService.getUserId(baseMethods.getUser()));
		
		EventVO eventVO = new EventVO();
		eventVO.setEventName(eventname);
		eventVO.setCurrentUser(loginVO);
		eventService.insert(eventVO);
		
		List<EventVO> eventList =  eventService.getEventList(loginService.getUserId(baseMethods.getUser()));

		return new ModelAndView("user/events").addObject("add", new LoginVO()).addObject("eventlist", eventList);
				
	}
	
	@PostMapping(value = "user/eventGallery")
	public ModelAndView eventGallery(HttpServletRequest httpServletRequest) {

		String ename = httpServletRequest.getParameter("eName");
		System.out.println(ename);
		
		LoginVO loginVO = new LoginVO();
		loginVO.setLoginId(loginService.getUserId(baseMethods.getUser()));
		
		List<EventGalleryVO> eventFileList =  eventGalleryService.getEventFileList(ename, loginVO);
		
		return new ModelAndView("user/eventGallery").addObject("add", new LoginVO())
				.addObject("eventGallery", new EventGalleryVO()).addObject("event", ename).addObject("eventFileList", eventFileList);
				
	}
	
	@PostMapping(value = "user/saveEventPost")
	public ModelAndView saveEventPost(EventGalleryVO eventGalleryVO, HttpServletRequest httpServletRequest) {

		String ename = httpServletRequest.getParameter("eventname");
		
		LoginVO loginVO = new LoginVO();
		loginVO.setLoginId(loginService.getUserId(baseMethods.getUser()));
		eventGalleryVO.setUserName(loginVO);
		
		EventVO eventVO = new EventVO();
		eventVO.setEventId(eventService.getEventId(ename, loginVO));
		eventGalleryVO.setEvenVo(eventVO);
		
		System.out.println(eventGalleryVO.getfName());
		eventGalleryService.insert(eventGalleryVO);
		
		List<EventGalleryVO> eventFileList =  eventGalleryService.getEventFileList(ename, loginVO);
		
		return new ModelAndView("user/eventGallery").addObject("add", new LoginVO())
				.addObject("eventGallery", new EventGalleryVO()).addObject("event", ename).addObject("eventFileList", eventFileList);
				
	}
}
