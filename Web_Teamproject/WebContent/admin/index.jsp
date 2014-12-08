<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>    
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>회원목록</title>
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<link href="css/bootstrap.min.css" rel="stylesheet">
	<link href="css/bootstrap-responsive.min.css" rel="stylesheet">
	<link href="css/base.css" rel="stylesheet">
	<script src="js/jquery-1.8.2.min.js"></script>
	<script src="js/bootstrap.min.js"></script>
</head>
<body>
<jsp:include page="share/header.jsp">
  <jsp:param name="current" value="Member"/>
</jsp:include>

  <div class="container">
 		<div class="row">
			<div class="page-info">
				<div class="pull-left">
					Total <b>${users.numItems }</b> users 
				</div>
				<div class="pull-right">
					<b>${users.page }</b> page / total <b>${users.numPages }</b> pages
				</div>
 			</div>
 		</div>
		<table class="table table-bordered table-stripped table-hover">
			<thead>
				<tr>
					<th>No</th>
					
					<th>ID</th>
					<th>Last Name</th>
					<th>First Name</th>
					<th>Nick Name</th>
					<th>Email</th>
					<th>Gender</th>
					<th>Date</th>
					<th>Write</th>
					<th>Manage</th>
				</tr>
			</thead>
			<tbody>
			<c:forEach var="user" items="${users.list }" varStatus="status">
				<tr>
					<td>${status.index + (users.page-1) * 10 + 1 }</td>
					<td><a href="admin?op=show&id=${user.userid}"><c:out value="${user.userid}"/></a></td>
					<td><c:out value="${user.lastname}"/></td>
					<td><c:out value="${user.firstname}"/></td>
					<td><c:out value="${user.nickname}"/></td>
					<td><c:out value="${user.email}"/></td>
					<td><c:out value="${user.genderStr}"/></td>
					<td><c:out value="${user.registerdate}"/></td>
					<td> </td>
					<td><a href="admin?op=update&id=${user.userid}"
						class="btn btn-mini">modify</a> <a href="#"
						class="btn btn-mini btn-danger" data-action="delete"
						data-id="${user.userid}">delete</a></td>
				</tr>
			</c:forEach>
			</tbody>
		</table> 

    <jsp:include page="page.jsp">
      <jsp:param name="currentPage" value="${users.page}"/>
      <jsp:param name="url" value="admin"/>
      <jsp:param name="page" value="page"/>
      <jsp:param name="startPage" value="${users.startPageNo}"/>
      <jsp:param name="endPage" value="${users.endPageNo}"/>
      <jsp:param name="numPages" value="${users.numPages}"/>
    </jsp:include>

		<div class="form-action">
			<a href="admin?op=signup" class="btn btn-primary">Sign Up</a>
		</div>	 	
  </div>
<jsp:include page = "share/footer.jsp" />
</body>
<script>
	$("a[data-action='delete']").click(function() {
		if (confirm("정말로 삭제하시겠습니까?")) {
			location = 'admin?op=delete&id=' + $(this).attr('data-id');
		}
		return false;
	});
</script>
</html>