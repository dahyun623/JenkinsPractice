<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>그룹채팅 테스트 화면</title>
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.1/css/bootstrap.min.css">
<style type="text/css">
	fieldset {
		margin-top: 300px;
		margin-left: 300px;
	}
	li {
		margin: 10px;
	}
</style>
</head>
<body>
	<div class="container">
		<fieldset>
			<legend>참여자 목록</legend>
			<div>
				<ul>
					<li><button class="btn btn-success" onclick="goSocket('S','super')">그룹(S)/사용자(super)</button></li>
					<li><button class="btn btn-success" onclick="goSocket('S','higher')">그룹(S)/사용자(higher)</button></li>
				</ul>
				<ul>
					<li><button class="btn btn-primary" onclick="goSocket('H','super')">그룹(H)/사용자(super)</button></li>
					<li><button class="btn btn-primary" onclick="goSocket('H','top')">그룹(H)/사용자(top)</button></li>
				</ul>
			</div>
		</fieldset>
	</div>
	<script type="text/javascript">
		function goSocket(gr_id,mem_id){
			window.open("./socketOpenGr.do?mem_id="+mem_id+"&gr_id="+gr_id,gr_id+"그룹채팅","width=600px,height=800px,toolbar=no,menubar=no,left=300px,top=50px");
			
		}
	</script>
</body>
</html>