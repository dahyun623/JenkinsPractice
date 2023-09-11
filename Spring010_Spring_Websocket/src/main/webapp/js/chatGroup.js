var ws = null;
var url = null;
var nick = null;
// onbeforeunload 시 강제 닫기가 아닌 닫기버튼으로 동작시 경고창 없애기 위해서 선언
var pageClosed = true;

var roomClose = function(){
	alert("채팅종료");
	$.ajax({
		type:"post",
		url:"./socketOut.do",
		async:true,
		success:function(){
			pageClosed = false;
			console.log("pageClosed 폴스가 떠야 돼 : "+pageClosed);
			self.close();
		}
	});
}

// 채팅을 위한 WebSocket 객체를 생성
$(document).ready(function(){
	console.log("pageClosed 트루야 폴스야",pageClosed);
	$(window).on("beforeunload",function(){
		if(pageClosed==true){
			ws.close();
			return "Any Change will be lost";
		}
	});


	console.log("그룹 채팅 로딩");
	
	var ws = null;
	var url = location.href;//현재 웹주소
	var checkUrl = "ws:"+url.substring(url.indexOf('//'),url.lastIndexOf('/')+1)+"wsChatGr.do";//웹소켓 주소
	var nickName = document.querySelector("#nickName>span").textContent;
	var group = document.getElementById("group").textContent;
	console.log(checkUrl,nickName,group);
	
	$(".chat").focus();
	
	// 웹소켓 객체를 생성
	ws = new WebSocket(checkUrl);
	console.log("생성된 websocket 객체",ws);
	
	// 웹소켓 오픈 후에 send()를 통해서 서버에 Text를 전송하면 -> handlerTextMessage -> SessionMap
	ws.onopen = function(){
		console.log("웹소켓 오픈");
		ws.send("#&nick_"+nickName); // -> handlerTextMessage
	}
	
	ws.onmessage = function(event){
		var msg = event.data;
		console.log("서버로부터 전송된 메시지",msg);
//		$(".receive_msg").append($("<div class='noticeText'>").append(msg+"<br>"));
		
		if(msg.startsWith("<font color=")){// 입장 퇴장
			$(".receive_msg").append($("<div class='noticeText'>").append(msg+"<br>"));
			/*
				참가자 리스트 갱신
			 */
			viewList(group);
		}else if(msg.startsWith("[나]")){// 내 대화내용
			msg = msg.substring(3);
			$(".receive_msg").append($("<div class='sendText'>").append($("<span class='send_img'>").text(msg))).append("<br><br>");// send_img 작업하기
		}else{
			$(".receive_msg").append($("<div class='receiveText'>").append($("<span class='receive_img'>").text(msg))).append("<br><br>");// send_img 작업하기
			
		}
		$(".receive_msg").scrollTop($(".receive_msg")[0].scrollHeight);
	}
	
	ws.onclose = function(){
		alert("서버와 연결이 종료되었습니다 \n 채팅방이 삭제 됩니다");
	}
	
	$(".chat_btn").bind("click", function(){
		if($(".chat").val() == ""){
			alert("내용을 입력해 주세요");
		}else{
			ws.send(nickName + ":"+$(".chat").val());
			$(".chat").val("");
			$(".chat").focus();
		}
	});
	
	
	
	
}); 






	
	function viewList(grId){
		$(".memList").children().remove();
		$.ajax({
			type:"post",
			url:"./viewChatList.do",
			data:"gr_id="+grId,
			dataType:"json",
			success:function(result){
				console.log(grId, result.list);
				for(let str in result.list){
					if(grId==result.list[str]){
						$(".memList").prepend("<p style='border-bottom:0.5px solid #b4b4b4;'>"+str+"</p>");
					}
				}
			}
			
		});
	}

// 아이디말고 세션으로 str 바꾸기
// 채팅종료 누르면 경고창 false로 바꿔서 알림창 안뜨게 하기 - > 완
	
	
	