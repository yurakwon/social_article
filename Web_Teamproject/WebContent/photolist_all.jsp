<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" import="bean.Member"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="utf-8">
<title>Pinterest</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">

<link href="css/bootstrap.min.css" rel="stylesheet">
<link href="css/bootstrap-responsive.min.css" rel="stylesheet">
<link href="css/mwLogin.css" rel="stylesheet">
<link href="css/style.css" rel="stylesheet">
<link href="css/photo.css" rel="stylesheet">

<script src="js/jquery-1.8.2.min.js"></script>
<script type="text/javascript" src="js/common-dev.js"></script>
<script type="text/javascript" src="js/simplify-min.js"></script>
<script type="text/javascript" src="js/modernizr.custom.js"></script>
<script type="text/javascript" src="js/drawing-dev.js"></script>
<script src="js/bootstrap.min.js"></script>
<script src="js/mwLogin.js"></script>
</head>

<body>

	<jsp:include page="mainheader.jsp" />

	<!-- 카테고리 -->
	<nav id="menubar">
		<ul class="LiquidContainer HeaderContainer" style="width: 1170px;">
			<li><a href="article">전체</a></li>
			<c:forEach var="cate" items="${category.list}" varStatus="status">
				<li><a href="article?op=category&cate=${status.index + 1}"
					class="nav">${cate}</a></li>
			</c:forEach>
		</ul>
	</nav>

	<div id="list">
		<ul id="tiles">
		</ul>
	</div>

	<div id="loader">
		<div id="loaderCircle"></div>
	</div>

	<div class="popup">
		<div class="pbg"></div>
		<div id="photopage">
			<div id="name"></div>
			<div id="photo">
				<div id="photodetail"></div>
			</div>
			<c:if test="${sessionScope.user.userid != null}">
				<div id="form">
					<form method="post" action="Comment"></form>
				</div>
			</c:if>
			<div id="comment"></div>
		</div>
	</div>

</body>

</html>

<script src="js/jquery.wookmark.js"></script>
<script src="js/jquery.imagesloaded.js"></script>
<script type="text/javascript">

var sessionID = null; 
var sessionNick = null; 
var sessionPhoto = null; 

<% Member user = (Member) session.getAttribute("user");
   if(user != null) { %>
      sessionID = "<%=user.getUserid() %>";
      sessionNick = "<%=user.getNickname() %>";
      sessionPhoto = "<%=user.getProfilephoto() %>";
<% }   %>

$(function($){
   var isLoading = false;
   var handler = null;
  var page = 1;
  
   // Prepare layout options.
   var options = {
      autoResize : true, // This will auto-update the layout when the browser window is resized.
      container : $('#list'), // Optional, used for some extra CSS styling
      offset : 5, // Optional, the distance between grid items
      itemWidth : 222 // Optional, the width of a grid item
   };
  
  //최초의 한번 실행
  loadData();
  
  //스크롤되었을때 함수결합
  $(document).bind('scroll', onScroll);
   
   // 스크롤이 밑에 도착했을때 체크하는 함수
   function onScroll(event) {
       // Only check when we're not still waiting for data.
       if(!isLoading) {
         // Check if we're within 100 pixels of the bottom edge of the broser window.
         var closeToBottom = ($(window).scrollTop() + $(window).height() > $(document).height() - 100);
         if(closeToBottom) {
           loadData();
         }
       }
     };
     
   function loadData() {
       isLoading = true;
       $('#loaderCircle').show();
       
       $.ajax({
         url: "AjaxServlet",
         dataType: 'json',
         data: {page: page, op : "page"}, // Page parameter to make sure we load new data
         success: onLoadData
       });
     };   
     
   function onLoadData(data) {
      isLoading = false;
      $('#loaderCircle').hide();
      // Increment page index for future calls.
      page++;

      // Create HTML for the images.
      var html = '';
      var i=0, length=data.post.length, postitem;
      
      for(; i<length; i++) {
         postitem = data.post[i];
         html += '<li>'; 
         if(sessionID != null && sessionID == postitem.article.userid) {
               html += '<img class="deleteon" src="images/delete.png">';
          }
         html += '<section class="item" id="' + postitem.article.postid + '">';
         
          // photo
            html += '<article class="itemcontents">'; 
            html += '<img class="popupTrigger" src="images/photo/sm' + postitem.article.photo + '">'; 
            html += '<p>' + postitem.article.content + '</p>'; 
            html += '</article>';

            // comment
            if(postitem.comment != null) {
               html += '<article class="itemcomment">'; 
               $(postitem.comment).each(function(i, comm) {
                  html += '<p><span> <img class="profile-size2" src="images/profile/sm' + comm.userphoto + '"></span>';
                  html += '<span> <b>' + comm.usernick + '</b>' + comm.commentcontent + '</span></p>'; 
               });
               html += '</article>'; 
            }
            
            // form
             if(sessionID != null) {
               html += '<article class="itemform">'; 
               html += '<span><img class="profile-size2" src="images/profile/sm${sessionScope.user.profilephoto}"/></span>'; 
               html += '<form method="post" action="Comment">'; 
               html += '<input type="hidden" name="postid" value="' + postitem.article.postid +'"/>'; 
               html += '<input required  type="text" name="comment">'; 
               html += '</form>'; 
               html += '</article>'; 
            }
 
            html += '</section>'; 
            html += '</li>'; 
      } 
      
      // Add image HTML to the page.
      $('#tiles').append(html);
      
      //Apply layout.
       $('#tiles').imagesLoaded(function() {
            // Get a reference to your grid items.
            var handler = $('#tiles li');
            
            // Call the layout function.
            handler.wookmark(options);
       });
   };  
});

var view = null;
$(document).on('click', '.popupTrigger', function(event){
    
   var PopupWindow = $('.popup');
   var SelectItem = $(this.parentNode.parentNode);
   var id;
   var listHtml = '';
   
   id = SelectItem.attr('id');
   SelectItem.css("visibility", "hidden");
   
   document.body.style.overflow = 'hidden'; 
   PopupWindow.addClass('open');
   PopupWindow.css("top", $(window).scrollTop());
   
   // AJAX 요청
   $.ajax({
         url : "AjaxServlet",
         data : { postid : id , op : "popup"},
         type : "GET",
         dataType : "json",
         success : function(data) {
         


            // name 
            $('<img src=\"images/profile/' + data.user.profilephoto + '\">').appendTo('#name');
            $('<b><p style="font-size:12pt" id=\"name_name\">' + data.user.nickname + '</p></b>').appendTo('#name');
            $('<p style="font-size:10pt" id=\"name_time\">' + data.article.postdate + '</p>').appendTo('#name');


            if(sessionID == data.user.userid) {
               $('<button class="btn btn-mini modify" type="button">수정</button>').appendTo('#name');
            }

            // photo 
            $('<canvas id=\"photo_picture\"></canvas>').appendTo('#photodetail');
            var canvas = $('#photo_picture').get(0);
            var context = canvas.getContext('2d');
            var img = new Image();

            $('<p id=\"photo_content\">' + data.article.content + '</p>').appendTo('#photo');

            // form 
            if(sessionID != null) {
               $('<img src=\"images/profile/' + data.loginphoto + '\">').appendTo('#form form');
               $('<input type=\"hidden\" name=\"postid\" value=' + id + ' />').appendTo('#form form');
               $('<input required type=\"text\"name=\"comment\"/>').appendTo('#form form');
               $('<input type=\"submit\" class=\"btn btn-primary\" value=\"댓글\"/>').appendTo('#form form');
            }

            // comment 
            $(data.comment).each(function(i, comm) {
               $('<div class=\"commentitem'+ i + '\"></div>').appendTo('#comment');
               $('<img src=\"images/profile/' + comm.userphoto + '\">').appendTo('.commentitem' + i);
               $('<b><p>' + comm.usernick + '</p></b>').appendTo('.commentitem' + i);
               $('<p style="font-size:12pt">' + comm.commentcontent + '</p>').appendTo('.commentitem' + i);
               $('<p style="font-size:10pt">' + comm.commentdate + '</p>').appendTo('.commentitem' + i);
               if(sessionID == comm.userid) {
                  $('<button class=\"btn btn-mini btn-danger comment\" type=\"button\" value=\"'+ comm.commentid +'\">삭제</button>').appendTo('.commentitem' + i);
               }
            });      


               // 이미지띄우기
               view = new DoodleView(canvas, sessionID);
               view.setEditable(true);
            
               // 이미지 로딩이 끝나면 하는 처리
                  img.onload = function(){ 
                  $(canvas).attr('width', 300);
                  $(canvas).attr('height', 300 * (img.height / img.width));
                  var curheight = ($('#photopage').height() > $(window).height()) ? $('#photopage').height()-50 : $(window).height()-100;
               
                  
                  
                  curheight += (data.comment.length + 1) * 50;
                  $('.pbg').css('height', curheight);                   
                  view.setBackground(img.src);
       
               };
               
               $('.btn.btn-mini.modify').bind('click', function() {
                  location = 'article?op=update&id=' + id;
               });

               
               img.src = "images/photo/" + data.article.photo;
         },
         complete : function(xhr, status) {  }
   });
      // Hide Window
   PopupWindow.find('>.pbg').mousedown(function(event) {
      PopupWindow.removeClass('open');
      SelectItem.css("visibility", "visible");
         
      
         $('.popup #name').empty();
         $('#photodetail').empty();
         $('p#photo_content').remove();
         $('.popup #form form').empty();
         $('.popup #comment').empty();
   
         document.body.style.overflow = 'visible';
      return false;
   });
      
});

// 글을 지우기 위해 icon을 띄우는 부분 

$(document).on('mouseover', '.itemcontents', function(event){
   var SelectItem = $(this.parentNode.parentNode);
   $(SelectItem).find('>.deleteon').addClass('on');
});

$(document).on('mouseenter', '.itemcontents', function(event){
   var SelectItem = $(this.parentNode.parentNode);
   $(SelectItem).find('>.deleteon').addClass('on');
  
});

$(document).on('mouseleave', '.itemcontents', function(event){
   var SelectItem = $(this.parentNode.parentNode);
   $(SelectItem).find('>.deleteon').removeClass('on');
});

$(document).on('mouseout', '.itemcontents', function(event){
   var SelectItem = $(this.parentNode.parentNode);
   $(SelectItem).find('>.deleteon').removeClass('on');
});

$(document).on('mouseenter', '.deleteon', function(event){
   var SelectItem = $(this);
   SelectItem.addClass('on');
});

$(document).on('mouseover', '.deleteon', function(event){
   var SelectItem = $(this);
   SelectItem.addClass('on');
});

$(document).on('mouseleave', '.deleteon', function(event){
   var SelectItem = $(this);
   SelectItem.removeClass('on');
});

$(document).on('mouseout', '.deleteon', function(event){
   var SelectItem = $(this);
   SelectItem.removeClass('on');
});

// 삭제
$(document).on('click', '.deleteon', function(event){
   var SelectItem = this.nextSibling;
   var id = $(SelectItem).attr('id');
   if (confirm("정말로 삭제하시겠습니까?")) {
      location = 'article?op=delete&id=' + id;
   }
   return false;
});

//엔터쳐서 빈칸 생기는 거 방지
$(document).on('keydown', 'input[type="text"]', function(e) {
   if(e.keyCode == 13) {
      if($(this).val() == null || $(this).val() == "") {
         alert("내용을 입력하세요");
         return false;
      }
   }
   else return;
});

//코멘트 삭제
$(document).on('click', '.btn.btn-mini.btn-danger.comment', function(e) {
   var id = $(this).val();

   if (confirm("정말로 삭제하시겠습니까?")) {
      $(this).parent().remove();
       $.ajax({
         url : "Comment",
         data : { id : id, op : "remove_comment"},
         type : "GET",
         dataType : "json",
         success : function(data) { 
               if(data.result == 'ok') { 
                  alert("삭제하였습니다.");
               }
               else if(data.result == 'no') {
                  alert("삭제에 실패하였습니다");
               }
         },
         error : function() { alert("삭제실패"); }
      }); 
   }
   return false;
});

 

</script>