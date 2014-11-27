<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %> 
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>회원가입</title>
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
 
 <jsp:include page="mainheader.jsp" />
 
 <div class="container">
   <div>
		  <form class="form-horizontal" action="login" method="POST" enctype="multipart/form-data">
			<fieldset>
        <legend class="legend">Sign Up</legend>
        <c:if test="${method == 'PUT'}">
          <input type="hidden" name="_method" value="PUT"/>
          <input type="hidden" name="userid" value="${user.userid }"/>
        </c:if>
        <c:if test="${method == 'POST'}">
          <input type="hidden" name="_method" value="POST"/>
        </c:if>

				<div class="control-group">
					<label class="control-label" for="userid">ID</label>
					<div class="controls">
						<c:choose>
							<%-- 신규 가입일 때만 아이디 입력창이 나타남--%>
	        				<c:when test="${method == 'POST'}">
								<input type="text" name="userid">
							</c:when>
							<c:otherwise>
								<c:out value="${user.userid}"/>
							</c:otherwise>
						</c:choose>
					</div>
				</div>

				<div class="control-group">
					<label class="control-label" for="pwd">비밀번호</label>
					<div class="controls">
						<input type="password" name="pwd">
					</div>
				</div>

				<div class="control-group">
					<label class="control-label" for="pwd_confirm">비밀번호 확인</label>
					<div class="controls">
						<input type="password" name="pwd_confirm">
					</div>
				</div>

				<div class="control-group">
					<label class="control-label" for="lastname">성</label>
					<div class="controls">
						<input type="text" placeholder="홍" name="lastname" value="${user.lastname}">
					</div>
				</div>

				<div class="control-group">
					<label class="control-label" for="firstname">이름</label>
					<div class="controls">
						<input type="text" placeholder="길동" name="firstname" value="${user.firstname}">
					</div>
				</div>

				<div class="control-group">
					<label class="control-label" for="nickname">닉네임</label>
					<div class="controls">
						<input type="text" placeholder="닉네임" name="nickname" value="${user.nickname}">
					</div>
				</div>

				<div class="control-group">
					<label class="control-label" for="email">E-mail</label>
					<div class="controls">
						<input type="email" placeholder="abc@naver.com" name="email" value="${user.email}">
					</div>
				</div>

				<div class="control-group">
					<label class="control-label">성별</label>
					<div class="controls">
					  <c:forEach var="gender" items="${user.genders}">
					    <label class="radio">
					      <input type="radio" value="${gender[0]}" name="gender" ${user.checkGender(gender[0])}/>
					      ${gender[1]}
					    </label>
					  </c:forEach>
					</div>
				</div>

				<div class="control-group">
					<label class="control-label" for="website">웹사이트</label>
					<div class="controls">
						<input type="text" name="website" value="${user.website}">
					</div>
				</div>

				<div class="control-group">
					<label class="control-label" for="introduce">자기소개</label>
					<div class="controls">
						<textarea rows="3" name="introduce">${user.introduce}</textarea>
					</div>
				</div>

				<div class="control-group">
					<label class="control-label" for="profilephoto">프로필 사진</label>
					<div class="controls">
						<input type="file" name="profilephoto">
						<c:if test="${user.profilephoto != null}">
								<p>등록된 사진 <c:out value="${user.profilephoto}"/> 이 있습니다.</p> 
						</c:if>
						<p>사진의 크기는 최대 2MB까지 가능합니다.</p>
					</div>
				</div>

				<div class="form-actions">
					<c:choose>
						 <c:when test="${method=='POST'}">
	  						<input type="submit" class="btn btn-primary" value="가입">
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