/**
	Websocket 객체를 브라우저가 실행될때 생성해주는 js
 */
 
 var ws = null;
 var url = null;
 var nick = null;
 
 $(document).ready(function(){
	$("#nickName").focus();
	
	$("#join_room").bind("click",function(){
		console.log("닉네임 입력");
		if($("#nickName").val()==""){
			alert();
			$("#nickName").focus();
			return;
		}
		
		nick = $("#nickName").val();//대화창에서 입력받은 닉네임 -> server sode로 전송하여(map<WebsocketSession,nick>) 
		console.log("입력받은 닉네임 : ",nick);
		
		$("#resive_msg").html("");
		$("#chat_div").show("");
		$("#chat").focus();
		
		//Test 서버
//		ws = new WebSocket("wss://javascript.info/article/websocket/demo/hello");
		
		/**
			spring 혹은 node로 구성된 websocket 서버를 구현하여 ws로 사용함
		*/
//		var url = location.href;
//		console.log(url);
//		var checkUrl = url.substring(url.indexOf('//'),url.lastIndexOf('/')+1)
//		console.log(checkUrl);
//		ws = new WebSocket("ws:"+checkUrl+"wsChat.do");
		ws = new WebSocket("ws://192.168.8.159:8099/Spring010_Spring_Websocket/wsChat.do")
		
		ws.onopen = function(){
			console.log("웹소켓 객체 오픈");
			ws.send("#&nick_"+nick);
		}
		
		ws.onmessage=function(event){
			$("#resive_msg").append(event.data+"<br>")
		}
		
		ws.onclose=function(){
			alert("서버의 연결이 종료되었습니다");
		}
		
		$("#chat_btn").bind("click",function(){
			console.log("대화내용 전달");
			if($("#chat").val()==""){
				alert("대화내용을 입력해 주세요");
				return;
			}else{
				ws.send("["+nick+"]"+$("#chat").val());
				$("#chat").val("");
				$("#chat").focus();
			}
		});
		
		 
		 console.log("생성된 웹소켓서버: ",ws);
		
	});
});

function disconnection(){
	ws.close();
	ws=null;
	window.close();
}


window.addEventListener("beforeunload",(event)=>{
	event.preventDefault()
	event.returnValue="";
	ws.close();
	ws=null;
});