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
  <jsp:param name="current" value="article"/>
</jsp:include>

  <div class="container">
 		<div class="row">
			<div class="page-info">
				<div class="pull-left">
					Total <b>${posts.numItems }</b> Article
				</div>
				<div class="pull-right">
					<b>${posts.page }</b> page / total <b>${posts.numPages }</b> pages
				</div>
 			</div>
 		</div>
		<table class="table table-bordered table-stripped table-hover">
			<thead>
				<tr>
					<th>No</th>
					<th>작성자ID</th>
					<th>카테고리</th>
					<th>내용</th>
					<th>작성시간</th>
					<th>조회수</th>
					<th>Manage</th>
				</tr>
			</thead>
			<tbody>
			<c:forEach var="post" items="${posts.list }">
				<tr>
					<td>${post.postid}</td>
					<td><a href="admin?op=show&id=${post.userid}"><c:out value="${post.userid}"/></a></td>
					<td>${post.category}</td>
					<td>${post.content}</td>
					<td>${post.postdate}</td>
					<td>${post.hits}</td>
					<td><a href="admin?op=update&id=${post.postid}"
						class="btn btn-mini">modify</a> <a href="#"
						class="btn btn-mini btn-danger" data-action="delete"
						data-id="${post.postid}">delete</a></td>
				</tr>
			</c:forEach>
			</tbody>
		</table> 

    <jsp:include page="page.jsp">
      <jsp:param name="currentPage" value="${posts.page}"/>
      <jsp:param name="url" value="admin"/>
      <jsp:param name="page" value="op=article&page"/>
      <jsp:param name="startPage" value="${posts.startPageNo}"/>
      <jsp:param name="endPage" value="${posts.endPageNo}"/>
      <jsp:param name="numPages" value="${posts.numPages}"/>
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