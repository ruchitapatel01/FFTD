package com.project.controller;

import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
import com.project.VO.JustHappenedVO;
import com.project.VO.LoginVO;
import com.project.VO.MemberVO;
import com.project.VO.TravelVO;
import com.project.service.EventGalleryService;
import com.project.service.EventService;
import com.project.service.FriendsGalleryService;
import com.project.service.JustHappenedService;
import com.project.service.LoginService;
import com.project.service.MemberService;
import com.project.service.TravelService;
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
	private TravelService travelService;

	@Autowired
	private JustHappenedService justHappenedService;

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

		List<EventVO> eventList = eventService.getEventYearList(loginService.getUserId(baseMethods.getUser()));

		return new ModelAndView("user/events", "add", new LoginVO()).addObject("eventVo", new EventVO())
				.addObject("eventlist", eventList);
	}

	@PostMapping(value = "user/saveEvent")
	public ModelAndView saveEvent(EventVO eventVo, HttpServletRequest httpServletRequest) {

		System.out.println(eventVo.getEventFileName());

		LoginVO loginVO = new LoginVO();
		loginVO.setLoginId(loginService.getUserId(baseMethods.getUser()));

		eventVo.setCurrentUser(loginVO);
		eventService.insert(eventVo);

		List<EventVO> eventList = eventService.getEventYearList(loginService.getUserId(baseMethods.getUser()));

		return new ModelAndView("user/events").addObject("add", new LoginVO()).addObject("eventlist", eventList)
				.addObject("eventVo", new EventVO());

	}

	@GetMapping(value = "user/eventNameList")
	public ModelAndView eventNameList(@RequestParam(required = false) String eyear,
			HttpServletRequest httpServletRequest) {

		Integer eYear = Integer.parseInt(eyear);
		List<EventVO> eventNameList = eventService.getEventNameList(eYear);

		return new ModelAndView("user/eventGallery").addObject("add", new LoginVO()).addObject("eventNameList",
				eventNameList);
	}

	@GetMapping(value = "user/eventPostPage")
	public ModelAndView eventPostPage(@RequestParam(required = false) String year,
			@RequestParam(required = false) String eName, HttpServletRequest httpServletRequest) {

		Integer eYear = Integer.parseInt(year);
		List<EventVO> eventFileList = eventService.getEventFileList(eYear, eName);

		return new ModelAndView("user/eventPostPage").addObject("add", new LoginVO())
				.addObject("eventFileList", eventFileList).addObject("eventName", eName);
	}

	@GetMapping(value = "user/travelPage")
	public ModelAndView travelPage() {

		return new ModelAndView("user/travel", "add", new LoginVO()).addObject("travelVo", new TravelVO());
	}

	@PostMapping(value = "user/saveTravelPost")
	public ModelAndView saveTravelPost(TravelVO travelVo, HttpServletRequest httpServletRequest) {

		LoginVO loginVO = new LoginVO();
		loginVO.setLoginId(loginService.getUserId(baseMethods.getUser()));

		travelVo.setCurrentUser(loginVO);
		travelService.insert(travelVo);

		return new ModelAndView("user/travel").addObject("add", new LoginVO()).addObject("travelVo", new TravelVO());

	}

	@GetMapping(value = "user/travelYear")
	public ModelAndView travelYear(HttpServletRequest httpServletRequest) {

		List<TravelVO> travelList = travelService.getTravelYearList(loginService.getUserId(baseMethods.getUser()));

		return new ModelAndView("user/travel").addObject("add", new LoginVO()).addObject("travelVo", new TravelVO())
				.addObject("travelList", travelList).addObject("sort", "year");

	}

	@GetMapping(value = "user/travelCountry")
	public ModelAndView travelCountry(HttpServletRequest httpServletRequest) {

		List<TravelVO> travelList = travelService.getTravelCountryList(loginService.getUserId(baseMethods.getUser()));

		return new ModelAndView("user/travel").addObject("add", new LoginVO()).addObject("travelVo", new TravelVO())
				.addObject("travelList", travelList).addObject("sort", "country");

	}

	@GetMapping(value = "user/travelPlace")
	public ModelAndView travelPlace(HttpServletRequest httpServletRequest) {

		List<TravelVO> travelList = travelService.getTravelPlaceList(loginService.getUserId(baseMethods.getUser()));

		return new ModelAndView("user/travel").addObject("add", new LoginVO()).addObject("travelVo", new TravelVO())
				.addObject("travelList", travelList).addObject("sort", "place");

	}

	@GetMapping(value = "user/travelGallery")
	public ModelAndView travelGallery(@RequestParam(required = false) String sort,
			@RequestParam(required = false) String select, HttpServletRequest httpServletRequest) {

		List<TravelVO> travelList;

		if (select.equalsIgnoreCase("year")) {
			travelList = travelService.getTravelYearSelectionList(loginService.getUserId(baseMethods.getUser()),
					Integer.parseInt(sort));
		} else if (select.equalsIgnoreCase("country")) {
			travelList = travelService.getTravelCpuntrySelectionList(loginService.getUserId(baseMethods.getUser()),
					sort);
		} else {
			travelList = travelService.getTravelPlaceSelectionList(loginService.getUserId(baseMethods.getUser()), sort);
		}

		return new ModelAndView("user/travelGallery").addObject("add", new LoginVO())
				.addObject("travelVo", new TravelVO()).addObject("travelList", travelList).addObject("select", sort);

	}

	@GetMapping(value = "user/JustHappenedPage")
	public ModelAndView JustHappenedPage() throws ParseException {

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		Date date = new Date();

		List<JustHappenedVO> list = justHappenedService.getList(loginService.getUserId(baseMethods.getUser()));
		List<JustHappenedVO> justHappenedList = new ArrayList<JustHappenedVO>();

		for (int i=0; i<list.size(); i++) {
			long timeDiff = Math.abs(date.getTime() - formatter.parse(list.get(i).getDate()).getTime());
			long daysDiff = TimeUnit.DAYS.convert(timeDiff, TimeUnit.MILLISECONDS);
			
			if (daysDiff < list.get(i).getExpDays()) {
				justHappenedList.add(list.get(i));
			}
		}

		return new ModelAndView("user/JustHappened", "add", new LoginVO())
				.addObject("justHappenedVo", new JustHappenedVO()).addObject("list", justHappenedList);
	}

	@PostMapping(value = "user/saveJustHappenedPost")
	public ModelAndView saveJustHappenedPost(JustHappenedVO justhappenedVo, HttpServletRequest httpServletRequest) throws ParseException {

		LoginVO loginVO = new LoginVO();
		loginVO.setLoginId(loginService.getUserId(baseMethods.getUser()));

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		Date date = new Date();

		justhappenedVo.setCurrentUser(loginVO);
		justhappenedVo.setDate(formatter.format(date));
		justHappenedService.insert(justhappenedVo);

		List<JustHappenedVO> list = justHappenedService.getList(loginService.getUserId(baseMethods.getUser()));
		List<JustHappenedVO> justHappenedList = new ArrayList<JustHappenedVO>();

		for (int i=0; i<list.size(); i++) {
		long timeDiff = Math.abs(date.getTime() - formatter.parse(list.get(i).getDate()).getTime());
		long daysDiff = TimeUnit.DAYS.convert(timeDiff, TimeUnit.MILLISECONDS);
		
		if (daysDiff < list.get(i).getExpDays()) {
			justHappenedList.add(list.get(i));
		}
	}

		return new ModelAndView("user/JustHappened").addObject("add", new LoginVO())
				.addObject("justHappenedVo", new JustHappenedVO()).addObject("list", justHappenedList);

	}

//	@RequestMapping(value = "user/eventGallery")
//	public ModelAndView eventGallery(@RequestParam(required = false) String eN, HttpServletRequest httpServletRequest) {
//
//		String event = httpServletRequest.getParameter("eN");
//		System.out.println(eN);
//
//		LoginVO loginVO = new LoginVO();
//		loginVO.setLoginId(loginService.getUserId(baseMethods.getUser()));
//
//		List<EventGalleryVO> eventFileList = eventGalleryService.getEventFileList(event, loginVO);
//
//		return new ModelAndView("user/eventGallery").addObject("add", new LoginVO())
//				.addObject("eventGallery", new EventGalleryVO()).addObject("event", event)
//				.addObject("eventFileList", eventFileList);
//
//	}

//	@PostMapping(value = "user/saveEventPost")
//	public ModelAndView saveEventPost(EventGalleryVO eventGalleryVO, HttpServletRequest httpServletRequest) {
//
//		String ename = httpServletRequest.getParameter("eventname");
//
//		LoginVO loginVO = new LoginVO();
//		loginVO.setLoginId(loginService.getUserId(baseMethods.getUser()));
//		eventGalleryVO.setUserName(loginVO);
//
//		EventVO eventVO = new EventVO();
//		eventVO.setEventId(eventService.getEventId(ename, loginVO));
//		eventGalleryVO.setEvenVo(eventVO);
//
//		System.out.println(eventGalleryVO.getfName());
//		eventGalleryService.insert(eventGalleryVO);
//
//		List<EventGalleryVO> eventFileList = eventGalleryService.getEventFileList(ename, loginVO);
//
//		return new ModelAndView("user/eventGallery").addObject("add", new LoginVO())
//				.addObject("eventGallery", new EventGalleryVO()).addObject("event", ename)
//				.addObject("eventFileList", eventFileList);
//
//	}
}
