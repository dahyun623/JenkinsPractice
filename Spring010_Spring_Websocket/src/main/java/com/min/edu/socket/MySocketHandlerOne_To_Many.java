package com.min.edu.socket;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component(value = "wsChat.do")
public class MySocketHandlerOne_To_Many extends TextWebSocketHandler{

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private ArrayList<WebSocketSession> list; //웹소켓 세션을 담아주는 객체(참여하는 모든 session : 채팅대상을 담음)
	private Map<WebSocketSession, String> map = new HashMap<WebSocketSession,String>();//WebsocketSession에 메핑된 이름(nickName)
	
	public MySocketHandlerOne_To_Many() {
		list =new ArrayList<WebSocketSession>();
	}

	// Client 화면에서 WebSocket 객체를 생성했을 때 호출 => ws.onopen
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		logger.info("웹소켓 커넥션 객체 생성 afterConnectionEstablished & WebSocketSession 생성");
		logger.info("방금 참여한 WebSocketSession 정보 : {}",session.getId());
		
		super.afterConnectionEstablished(session);
		
		list.add(session); // 현재 참여한 WebSocketSession을 세션 목록에 담아 준다
		logger.info("현재 참여하고 있는 WebSocketSession의 개수 : {}",list.size());
		Map<String, Object> map = session.getAttributes();
		logger.info("--------- session.getAttributes() --------- \n{}",map);
	}

	// Client화면에서 WebSocket 객체가 담겼을 경우 -> ws.onclose
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		logger.info("웹소켓 세션 삭제 afterConnectionClosed");
		super.afterConnectionClosed(session, status);
		
		logger.info("웹소켓 세션 삭제 대상 : {}",session);
		list.remove(session);
		
		logger.info("현재 입력되어 있는 객체의 수 closed : {}",list.size());
		
		//화면에 메시지 보내기
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String out = sdf.format(new Date());
		for (WebSocketSession s : list) {
			s.sendMessage(new TextMessage("<font style='color:tomato;'>"+map.get(session)+"님이 방을 나갔습니다("+out+")</font>"));
		}
		logger.info("웹소켓 세션 이름 삭제 : {}",map.get(session));
		map.remove(session);
		
		
	}

	// Client에서 Message를 전달받아 모든 참여 WebSocketSession에 Broadcasting을 해주는 ws.send
	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		logger.info("웹소켓 메시지 전달 handleTextMessage");
		
		String msg = message.getPayload();
		String msgToString = message.toString();
		logger.info("전달된 메시지 getPayload : {}",msg);
		logger.info("전달된 메시지 toString : {}",msgToString);
		
		if(msg!=null && !msg.equals("")) {
			
			if(msg.indexOf("#&nick_")!=-1) {
				map.put(session, msg.replace("#&nick_", ""));
				logger.info(session+"의 이름은 : {}",map.get(session));
				
				for(WebSocketSession s : list) {
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String out = sdf.format(new Date());
					s.sendMessage(new TextMessage("<font style='color:forestgreen;'>"+map.get(session)+"님이 입장하셨습니다("+out+")</font>"));
				}
				
			}
			else {// #&nick_ (prefix) 없다면 채팅 글이기 때문에 모든 참여자에게 메시지를 보냄
				for(WebSocketSession s : list) {
					String m = "<font style='color:black;'>"+msg+"</font>";
					s.sendMessage(new TextMessage(m));
				}
			}
			
		}
			
			
		
		logger.info("채팅 참여자들 : {}",map);
		super.handleTextMessage(session, message);
	}
	
	
}
