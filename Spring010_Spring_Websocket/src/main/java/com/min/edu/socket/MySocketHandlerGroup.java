package com.min.edu.socket;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component(value = "wsChatGr.do")
public class MySocketHandlerGroup extends TextWebSocketHandler{
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private ArrayList<WebSocketSession> list;// WebSocketSession을 담는 객체
	
	public MySocketHandlerGroup() {
		list = new ArrayList<WebSocketSession>();
	}

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		logger.info("afterConnectionEstablished() 실행 {}",session);
		super.afterConnectionEstablished(session);
		
		list.add(session);// 전체 접속자 리스트에 새로운 접속자 추가
		
		Map<String, Object> sessionMap = session.getAttributes();
		String grSession = (String) sessionMap.get("gr_id");
		String memSession = (String) sessionMap.get("mem_id");
		
		logger.info("CLient Session count : {}",list.size());
		logger.info("CLient connected group : {}",grSession);
		logger.info("CLient connected id : {}",memSession);
		
	}

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		logger.info("handleTextMessage() 실행");
		String msg = message.getPayload();
		String txt="";
		logger.info("handleTextMessage() 전송된 메시지 {}",msg);
		
		Map<String, Object> mySession = session.getAttributes();// handshake-interceptors에 의해서 WebSocketSession의 값을 HttpSession값으로 변경
		String grSession = (String) mySession.get("gr_id");// 접속 그룹 아이디
		String memSession = (String) mySession.get("mem_id");//접속자 아이디
		
		//msg분석
		if(msg.indexOf("#&nick_")!=-1) {
			for(WebSocketSession s : list) {
				Map<String, Object> sessionMap = s.getAttributes();
				String otherGrSession = (String) sessionMap.get("gr_id");
				
				if(grSession.equals(otherGrSession)) { // 같은 그룹의 세션에게 입장 메시지를 전달
					s.sendMessage(new TextMessage("<font color='pink' size='2px'>"+memSession+"님이 입장했습니다</font>"));
				}
			}
		}else {
			String msg2 = msg.substring(0,msg.indexOf(":")).trim();
			for (WebSocketSession s : list) {
				Map<String, Object> sessionMap = s.getAttributes();
				String otherGrSession = (String) sessionMap.get("gr_id");
				String otherMemSession = (String) sessionMap.get("mem_id");
				
				if(grSession.equals(otherGrSession)) {//같은 그룹
					if(msg2.equals(otherMemSession)) {
						String newMsg = "[나]"+msg.replace(msg.substring(0,msg.trim().indexOf(":")+1), "");
						logger.info("newMsg : {}",newMsg);
						txt=newMsg;
					}else {
						String part1 = msg.substring(0,msg.indexOf(":")).trim();
						String part2 = "["+part1+"]\n"+msg.substring(msg.trim().indexOf(":")+1);
						txt=part2;
					}
					s.sendMessage(new TextMessage(txt));
				}
				
			}
		}
		
		
		super.handleTextMessage(session, message);
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		logger.info("afterConnectionClosed() 실행");
		super.afterConnectionClosed(session, status);
		

		// 접속을 종료하고자 하는 WebSocketSession의 그룹 확인
		Map<String, Object> mySession = session.getAttributes();
		String myGrSession = (String) mySession.get("gr_id");
		String myMemSession = (String)mySession.get("mem_id");
		
		// 접속을 정료하고자 하는 WebSocketSession의 정보가 있는지 확인
		logger.info("세션 삭제 확인 전 : {}",list.contains(session));
		
		// 세션 정보를 삭제
		list.remove(session);
		logger.info("세션 삭제 확인 후 : {}",list.contains(session));
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy년도 MM월 dd일 HH시 mm분");
		String now = sdf.format(new Date());
		
		// 같은 그룹에게 퇴장 메시지 전달
		for (WebSocketSession s : list) {
			Map<String, Object> sessionMap = s.getAttributes();
			String otherGrSession = (String) sessionMap.get("gr_id");
			if(myGrSession.equals(otherGrSession)) {
				s.sendMessage(new TextMessage("<font color='blue' size='2px'>"+myMemSession+"님이 퇴장하였습니다("+now+")</font>"));
			}
		}
		
		
	}
	
	
	
}
