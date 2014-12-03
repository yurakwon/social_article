<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>    
<!DOCTYPE html>

<html>
	<head>
		<meta charset="UTF-8">
		<title>새로운 글 </title>
		<link href="css/bootstrap.min.css" rel="stylesheet">
		<link href="css/bootstrap-responsive.min.css" rel="stylesheet">
		<link href="css/mwLogin.css" rel="stylesheet">
		<link href="css/style.css" rel="stylesheet">
		<link href="css/photo.css" rel="stylesheet">	
	
		<script src="js/jquery-1.8.2.min.js"></script>
		<script src="js/bootstrap.min.js"></script>
		<script src="js/mwLogin.js"></script>
	</head>

	<body>
			
		<div class="container">
		<div>
			<form class="form-horizontal" action="article" method="POST" enctype="multipart/form-data">
				<fieldset>
					<legend class="legend">Write</legend>
				
					<c:if test="${method == 'PUT'}">
          	<input type="hidden" name="_method" value="PUT"/>
          	<input type="hidden" name="postid" value="${article.postid }"/>
       		</c:if>
					
					<c:if test="${article.photo == null}">
					<div class="control-group">
						<label class="control-label" for="photo">사진</label>
						<div class="controls">
							<input type="file" name="photo">
							 
							<p>사진의 크기는 최대 5MB까지 가능합니다.</p>
						</div>
					</div>
					</c:if>

					<div class="control-group">
						<label class="control-label" for="introduce">내용</label>
						<div class="controls">
							<textarea rows="10" name="content">${article.content }</textarea>
						</div>
					</div>
					

					
					<div class="control-group">
						<label class="control-label">카테고리</label>
						<div class="controls">
						<select name="category">
							<c:forEach var="cate" items="${category.list}">
								<option value="${cate}">${cate}</option>
						  </c:forEach>
					  </select>
						</div>	
					</div>	
				
					<div class="form-actions">
					<c:choose>
						 <c:when test="${method=='POST'}">
	  						<input type="submit" class="btn btn-primary" value="작성">
	  					</c:when>
	  					<c:otherwise>
	  						<input type="submit" class="btn btn-primary" value="수정">
	  					</c:otherwise>
  					</c:choose>
  				<a href="javascript:history.go(-1);" class="btn btn-danger">취소</a>
					</div>
				</fieldset>
			</form>
		</div>
		</div>
	</body>
</html>
