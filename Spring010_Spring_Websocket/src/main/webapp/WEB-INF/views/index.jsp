<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>채팅 처음 화면</title>
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.1/css/bootstrap.min.css">
<style type="text/css">
	div {
		text-align: center;
		margin-top: 20px;
	}
</style>
<script type="text/javascript">
	window.onload=function(){
		document.querySelector(".btn-outline-primary").onclick=function(){
			console.log("1:N 채팅");
			location.href="./chatOneToMany.do";
		}
		document.querySelector(".btn-outline-success").onclick=function(){
			console.log("Group 채팅");
			location.href="./chatGroup.do";
		}
	}
</script>
</head>
<body>
	<div>
		<button type="button" class="btn btn-outline-primary">1:N채팅</button>
		<button type="button" class="btn btn-outline-success">Group채팅</button>
		<a href="./testChatGroup.do">testChatGroup.do</a>
	</div>
</body>
</html>