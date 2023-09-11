package com.min.edu;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.ServletConfigAware;

@Controller
public class ChatController implements ServletConfigAware{
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private ServletContext servletContext;
	
	@Override
	public void setServletConfig(ServletConfig servletConfig) {
		servletContext = servletConfig.getServletContext();
	}
	
	
	
	@GetMapping(path="/chatOneToMany.do")
	public String chatOneToMany() {
		logger.info("ChatController chatOneToMany");
		return "chatOneToMany";
	}
	
	@GetMapping(path = "/chatGroup.do")
	public String chatGroup() {
		logger.info("ChatController chatGroup");
		return "chatGroup";
	}
	
	@GetMapping(path="/socketOpenGr.do")
	public String socketOpenGr(String gr_id, String mem_id,HttpSession session) {
		logger.info("ChatController socketOpenGr 참여자 아이디 {} / 그룹 {}",mem_id,gr_id);
		
		//parameter의 정보를 HttpSession에 담는다, 
		//자동으로 Bean의 HandShake-Handler에 handshake-interceptors에 의해서 WebSocketSession에 담아준다
		//참여자 정보를 HttpSession에 담는다
		
		session.setAttribute("mem_id", mem_id);
		session.setAttribute("gr_id", gr_id);
		
		//서버 전체에서 계속해서 참여자의 정보를 담기 위해서 ServletContext 객체를 사용한다
		Map<String, String> chatList = (Map<String, String>) servletContext.getAttribute("chatList");
		if(chatList ==null) {
			chatList = new HashMap<String,String>();
			chatList.put(mem_id, gr_id);
			servletContext.setAttribute("chatList", chatList);
		}else {
			chatList.put(mem_id, gr_id);
			servletContext.setAttribute("chatList", chatList);
		}
		
		logger.info("ChatController 웹소켓 목록 {}",servletContext.getAttribute("chatList"));
		
		
		return "chatGroupView";
	}

	@RequestMapping(value = "/socketOut.do",method = RequestMethod.POST)
	@ResponseBody
	public void socketOut(HttpSession session) {
		logger.info("socketOut 소켓에서 나오기");
		String mem_id = (String) session.getAttribute("mem_id");
		Map<String, String> chatList = (Map<String, String>)servletContext.getAttribute("chatList");
		logger.info("기존 접속 회원 리스트: {}",chatList);
		
		if(chatList != null) {
			chatList.remove(mem_id);
		}
		
		logger.info("갱신 후 접속 회원 리스트 : {}",chatList);
		servletContext.setAttribute("chatList", chatList);
		
	}
	
	@RequestMapping(value = "/viewChatList.do", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Map<String, String>> viewChatList(){
		Map<String, Map<String, String>> map = new HashMap<String, Map<String,String>>();
		Map<String, String> chatList = (Map<String, String>)servletContext.getAttribute("chatList");
		map.put("list", chatList);
		logger.info("접속 회원 리스트 : {}",map);
		return map;
	}
	
}
