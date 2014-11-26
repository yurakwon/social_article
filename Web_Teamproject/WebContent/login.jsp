<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>    
<!DOCTYPE html>
<html>
	<head>
		<meta charset="UTF-8">
		<title>Login</title>
		<link href="css/bootstrap.min.css" rel="stylesheet">
		<link href="css/base.css" rel="stylesheet">
		<script src="js/jquery-1.8.2.min.js"></script>
		<script src="js/bootstrap.min.js"></script>
	</head>

	<body>
		<c:if test="${errorMsgs != null || errorMsgs.size() > 0 }">
			<div class="alert alert-error">
		        <ul>
		          <c:forEach var="msg" items="${errorMsgs}">
		            <li>${msg}</li>
		          </c:forEach>
		        </ul>
		    </div>
	    </c:if>
	
	<form method="post" action="login" enctype="multipart/form-data">
		<input type="hidden" name="_method" value="login">
		<p>ID: <input type="text" name="userid"/> </p>
		<p>Password: <input type="password" name="pwd"> </p>
		<p><input type="submit" value="login"> </p>
	</form>
</body>
</html>
<script>
</script>