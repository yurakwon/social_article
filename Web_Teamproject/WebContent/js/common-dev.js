/*
	@제목 	:	울화통 프로젝트 공통 모듈
	@작성자	:	도란도란팀
	@의존도 :	Jqeury Library가 선행으로 Import 되어야 합니다.	
	@설명	:
		주로 전역 객체와 전역 메소드를 정의합니다.
		유틸 함수가 포함됩니다.
		
		코드의 수정 및 배포는 자유로우나 이 주석은 변경하지 마십시오.
*/

// 현재 로그인된 사용자에 관한 객체입니다.
var User = new Object();
User = {
	id	:	null,
	name : null,
	birthDay : null,
	summary : null,
	profile : null
};

// 전역 변수

// 깊은 복사를 수행하는 전역함수입니다.
// 인자를 복사하여 새로운 복사본을 리턴합니다.
// 내부 레퍼런스 변수의 레퍼런스 관계가 그래프(순환)되지 않도록 주의하십시오.
function deepCopy(obj){
	return JSON.parse(JSON.stringify(obj));
};

var Trig = {
	distanceBetween2Points: function ( point1, point2 ) {
		var dx = point2.x - point1.x;
		var dy = point2.y - point1.y;
		return Math.sqrt( Math.pow( dx, 2 ) + Math.pow( dy, 2 ) );
	},
 
	angleBetween2Points: function ( point1, point2 ) {
		var dx = point2.x - point1.x;
		var dy = point2.y - point1.y;
		return Math.atan2( dx, dy );
	}
}

function adjustRatio(target, w, h){
	var ratio = w / h;
	$(target).attr('height', $(target).attr('width') / ratio);
}




function changeText(target){
	$str1=$("#"+target).val().replace(/(\r\n|\n|\n\n)/gi,'[split]');
	$str1=$str1.replace(/\'/g,"''");
	$str1 = $str1.split("[split]");
	$result='';
	$.each(
		$str1,function(i){
			$str1[i]=$str1[i].replace(' ','[space]');
			if($str1[i]=="")	$result +='<br />';
			else				$result +=$str1[i]+'<br />';
															
		}
	);
		
	return $result;	
}
