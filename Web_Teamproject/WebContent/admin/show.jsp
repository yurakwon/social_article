<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" import="java.util.*"  import="java.sql.*"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %> 
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>회원목록</title>
	<link href="css/bootstrap.min.css" rel="stylesheet">
  <link href="css/base.css" rel="stylesheet">
  <script src="js/jquery-1.8.2.min.js"></script>
  <script src="js/bootstrap.min.js"></script>
</head>
<body>
<jsp:include page="share/header.jsp">
  <jsp:param name="current" value="Member"/>
</jsp:include>

 <div class="container">
    <div>
      <h3><c:out value="${user.lastname}"/> <c:out value="${user.firstname}"/></h3>
      <ul>
        <li>ID: <c:out value="${user.userid}"/></li>
        <li>Email: <a href="mailto:${user.email}"><c:out value="${user.email}"/></a></li>
        <li>별명: ${user.nickname }</li>
        <li>Gender: ${user.genderStr }</li>
        <li>웹사이트: ${user.website }</li>
        <li>자기소개: ${user.introduce }</li>
        <li>가입일시: ${user.registerdate}</li>
      </ul>
    </div>      

	  <div class="form-actions">
	    <a href="admin" class="btn">목록으로</a>
 	    <a href="admin?op=update&id=${user.userid}" class="btn btn-primary">수정</a>
      <a href="#" class="btn btn-danger" data-action="delete" data-id="${user.userid}" >삭제</a>
    </div>
	<script>
	    $("a[data-action='delete']").click(function() {
	      if (confirm("정말로 삭제하시겠습니까?")) {
	        location = 'admin?op=delete&id=' + $(this).attr('data-id');
	      }
	      return false;
	    });
	</script>  
  </div>
</body>
</html>