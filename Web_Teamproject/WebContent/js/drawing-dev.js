/*
	@제목 	:	울화통 프로젝트 드로잉 모듈
	@작성자	:	도란도란팀
	@의존도 :	Jqeury Library가 선행으로 Import 되어야 합니다.	
				common.js 가 선행으로 Import 되어야 합니다.
				simplify-min.js 가 선행으로 Import 되어야 합니다.
	@설명	:
		Canvas를 이용한 드로잉에 관련된 기능을 정의합니다.
		
		코드의 수정 및 배포는 자유로우나 이 주석은 변경하지 마십시오.
*/

// Brush Set
var BrushSet = [];
for(var i = 5 ; i <= 15 ; i=i+5)
{
	BrushSet['type1_'+i+'_black'] = new Image();
	BrushSet['type1_'+i+'_white'] = new Image();
	BrushSet['type1_'+i+'_red'] = new Image();
	BrushSet['type1_'+i+'_green'] = new Image();
	BrushSet['type1_'+i+'_blue'] = new Image();
	BrushSet['type1_'+i+'_purple'] = new Image();
	BrushSet['type1_'+i+'_lime'] = new Image();
	BrushSet['type1_'+i+'_orange'] = new Image();
	
	BrushSet['type1_'+i+'_black'].src = 'images/brush/type1_'+i+'_black'+'.png';
	BrushSet['type1_'+i+'_white'].src = 'images/brush/type1_'+i+'_white'+'.png';
	BrushSet['type1_'+i+'_red'].src = 'images/brush/type1_'+i+'_red'+'.png';
	BrushSet['type1_'+i+'_green'].src = 'images/brush/type1_'+i+'_green'+'.png';
	BrushSet['type1_'+i+'_blue'].src = 'images/brush/type1_'+i+'_blue'+'.png';
	BrushSet['type1_'+i+'_purple'].src = 'images/brush/type1_'+i+'_purple'+'.png';
	BrushSet['type1_'+i+'_lime'].src = 'images/brush/type1_'+i+'_lime'+'.png';
	BrushSet['type1_'+i+'_orange'].src = 'images/brush/type1_'+i+'_orange'+'.png';
	
	BrushSet['type1_'+i+'_black'].name = 'type1_'+i+'_black';
	BrushSet['type1_'+i+'_white'].name = 'type1_'+i+'_white';
	BrushSet['type1_'+i+'_red'].name = 'type1_'+i+'_red';
	BrushSet['type1_'+i+'_green'].name = 'type1_'+i+'_green';
	BrushSet['type1_'+i+'_blue'].name = 'type1_'+i+'_blue';
	BrushSet['type1_'+i+'_purple'].name = 'type1_'+i+'_purple';
	BrushSet['type1_'+i+'_lime'].name = 'type1_'+i+'_lime';
	BrushSet['type1_'+i+'_orange'].name = 'type1_'+i+'_orange';
}

// 하나의 Canvas에 대응되는 View 객체 입니다.
function DoodleView(canvas, ownerID, shapes)
{
	
	var canvasObject = canvas;
	var isEditable = false;
	var isDrawing = false;
	var isImaging = false;
	var canvasContext = canvasObject.getContext("2d");
	var shapes = null;
	var lastPoint = null;
	var touchSupported = Modernizr.touch;
	var shapeStack = new Array();
	var tempshape = new Array();
	
	var currentShape = 
	{
		shapeOwnerID	:	ownerID,
		shapeType		:	null,
		points			:	[],
		drawType		:	0
	};
	
	this.setdrawType = function() {
		currentShape.drawType += 1;
	}
	
	// 오버로딩
	if(arguments.length == 3)
	{
		shapes = shapes;
	}
	else
	{
		shapes = new Array();
		currentShape.shapeOwnerID = ownerID;
	}
	
	var currentBrush = BrushSet['type1_5_black'];
	
	// 사진
	var photoDataUrl = null;
	var photo = null;
	
	// 글자
	var textDataUrl = null;
	var text = null;
	
	// private Method Section
	////////////////////////////
	
	// 사용자에 해당하는 모든 Shape을 돌려줍니다.
	this.getOwnerShapes = function(ownerID)
	{
		var shapeList = new Array();
		for(var i=0 ; i<shapes.length ; i++){
			if(shapes[i].shapeOwnerID == ownerID)
			{
				shapeList.push(shapes[i]);
				console.log(shapes[i]);
			}
		}
			
		return shapeList;
	}
	
	this.curShapes = function()
	{
		if(tempshape.length != 0) {
			return tempshape;
		}
		else return null;
	}
	
	this.curShapesReset = function() {
		tempshape = new Array();
	}
	
	// 마우스 포인트 위치 정규화
	var pointsNormalize = function(points)
	{	
		var returnPoints = new Array();
		
		for(var i=0 ; i<points.length ; i++)
		{
			var p = {
				x	:	points[i].x / $(canvasObject).attr('width'),
				y	:	points[i].y / $(canvasObject).attr('height')
			};
			returnPoints.push(p);
		}
		
		return returnPoints;
	}

	// 마우스 포인트 위치 역 정규화
	var pointsSpecialize = function(points)
	{	
		var returnPoints = new Array();
		
		for(var i=0 ; i<points.length ; i++)
		{
			var p = {
				x	:	points[i].x * $(canvasObject).attr('width'),
				y	:	points[i].y * $(canvasObject).attr('height')
			};
			returnPoints.push(p);
		}
		
		return returnPoints;	
	}
	
	// canvas위의 마우스 위치를 point 로 돌려줍니다.
	//var getMousePosition = function(event)
	//{
	//	var rect = event.target.getBoundingClientRect();
	//	return {
	//		x	:	event.clientX - rect.left,
	//		y	:	event.clientY - rect.top	
	//	}
	//}
	
	var getMousePosition = function (event) {
		var target;
		if (touchSupported) {
			target = event.originalEvent.touches[0]
		}
		else {
			target = event;
		}
	
		var offset = $(canvasObject).offset();
		
		return {
			x : target.pageX - offset.left,
			y : target.pageY - offset.top
		}
	}
	
	// 자유곡선을 그립니다.
	var drawPath = function(shape)
	{
		// 포인트가 2개 이상 없으면 종료
		if(shape.points.length < 2)	return;
		
		// 그림 시작
		var cPoints = pointsSpecialize(shape.points);
		//alert("DD:"+JSON.stringify(cPoints));
		
		// Brush 선택
		var originBrush = currentBrush;
		currentBrush = BrushSet[shape.shapeType];
		for(var i=0 ; i<cPoints.length - 1 ; i++)
		{
			drawLine(cPoints[i], cPoints[i+1]);
		}
		
		// Brush 복원
		currentBrush = originBrush;
	}
	
	// 라인 하나를 그립니다.
	var drawLine = function(first, last)
	{
		var halfBrushW = currentBrush.width/2;
		var halfBrushH = currentBrush.height/2;
 
		var start = first;
		var end = last;
 
		var distance = parseInt( Trig.distanceBetween2Points( start, end ) );
		var angle = Trig.angleBetween2Points( start, end );
 
		var x,y;
 
		for ( var z=0; (z<=distance || z==0); z++ ) {
			x = start.x + (Math.sin(angle) * z) - halfBrushW;
			y = start.y + (Math.cos(angle) * z) - halfBrushH;
			canvasContext.drawImage(currentBrush, x, y);
		}
	}
	
	// shape의 리스트를 그립니다.
	var drawShapes = function(shapeList)
	{
		// 그릴 요소가 없으면 나갑니다.
		if(shapeList.length == 0)	return;	
		
		// 모든 요소를 그립니다.
		for(var i=0 ; i<shapeList.length ; i++)
		{		
			drawPath(shapeList[i]);			
		}
		
	}
	
	// canvas를 다시 그립니다.
	var reDraw = function()
	{		
		// canvas를 먼저 지웁니다.
		canvasContext.clearRect(0, 0, $(canvasObject).width(), $(canvasObject).height());
		
		// 배경이미지가 있다면 먼저 그립니다.
		if(photo != null && photoDataUrl!=null){
			canvasContext.drawImage(photo, 0, 0, $(canvasObject).width(), $(canvasObject).height());
		}
		
		// 글씨 이미지가 있다면 그립니다.
		if(text != null && textDataUrl!=null){
			canvasContext.drawImage(text, 0, 0, $(canvasObject).width(), $(canvasObject).height());
		}
		
		// 모든 요소를 그립니다.
		drawShapes(shapes);
	}
	
	// canvas의 Mouse Down 이벤트 입니다.
	var onMouseDown = function(event)
	{
		event.preventDefault();
		if(isEditable == true)
		{
			if(isImaging){
				alert("배경이미지가 그려지는 중입니다. 잠시만 기달려 주세요");
				return;	
			}
			var point = getMousePosition(event);
			isDrawing = true;
			
			// context 초기화, Shape의 소유주 id 설정
			currentShape.shapeOwnerID = ownerID;
			currentShape.shapeType = currentBrush.name;
			
			// 점 지정
			lastPoint = {x : point.x, y : point.y};
				
			// point 저장
			currentShape.points.push({x : point.x, y : point.y});			
		}
	}
	
	// canvas의 Mouse Move 이벤트 입니다.
	var onMouseMove = function(event)
	{
		event.preventDefault();
		if(isEditable == true)
		{
			if(isDrawing == true)
			{
				var point = getMousePosition(event);
				
				// 선 그리기
				drawLine(lastPoint, point);
				
				// 마지막점 지정
				lastPoint = {x : point.x, y : point.y};
				
				// point 저장
				currentShape.points.push({x : point.x, y : point.y});	
			}
		}
	}
	
	// canvas의 Mouse Up 이벤트 입니다.
	var onMouseUp = function(event)
	{
		event.preventDefault();
		if(isEditable)
		{
			if(isDrawing == true){
				isDrawing = false;
				
				// 최적화 후 다시그리기
				currentShape.points = pointsNormalize(simplify(currentShape.points, 0.5, true));
				tempshape.push(deepCopy(currentShape));
				shapes.push(deepCopy(currentShape));
				reDraw();
				
				// 현재 그림객체 리셋
				currentShape.points=[];
			}
		}
	}
	
	// 마우스 이벤트 핸들러 등록
	
	if (touchSupported) {
		$(canvasObject).bind("touchstart", onMouseDown);
		$(canvasObject).bind("touchmove", onMouseMove);	
		$(canvasObject).bind("touchend", onMouseUp);
	}else{
		$(canvasObject).bind("mousedown", onMouseDown);
		$(canvasObject).bind("mousemove", onMouseMove);	
		$(canvasObject).bind("mouseup", onMouseUp);
		$(window).bind("mouseup", onMouseUp);
	}	
	
	// privileged Method Section
	////////////////////////////
	
	// canvas의 그리기를 가능하게 해줍니다.
	this.setEditable = function(bool)
	{
		isEditable = bool;	
	}
	
	// Shape을 추가합니다.
	this.addShape = function(shape)
	{
		shapes.push(shape);
		reDraw();	
	}
	
	// 브러쉬를 설정합니다.
	this.setBrush = function(brushType)
	{
		if(BrushSet[brushType] instanceof Image)
		{
			currentBrush = BrushSet[brushType];
		}
		else
		{
			alert("잘못된 브러쉬");
		}
	}
	
	// OwnerID 에 대한 Shape을 삭제 합니다.
	this.delOwner = function(ownerID)
	{
		for(var i=0 ; i<shapes.length ; i++){
			if(shapes[i].shapeOwnerID == ownerID)
			{
				shapes.splice(i, 1);	
			}
		}
		reDraw();
	}
	
	// canvas를 다시 그리게 합니다. 노출
	this.rePaint = function()
	{
		reDraw();	
	}
	
	// canvas를 모두 지워버립니다.
	this.clear = function(){
		shapes.splice(0, shapes.length);
		this.removeBackground();
		this.removeText();
		shapeStack = new Array();
		reDraw();	
	}
	
	// canvas를 비웁니다.
	this.clearscr = function()
	{		
		// canvas를 먼저 지웁니다.
		canvasContext.clearRect(0, 0, $(canvasObject).width(), $(canvasObject).height());
		
		// 배경이미지가 있다면 먼저 그립니다.
		if(photo != null && photoDataUrl!=null){
			canvasContext.drawImage(photo, 0, 0, $(canvasObject).width(), $(canvasObject).height());
		}
	}
	
	this.redo = function(){
		if(shapeStack.length == 0)	return;
		
		shapes.push(shapeStack.pop());
		reDraw();
	}
	
	this.undo = function(){
		if(shapes.length == 0)	return;
		
		shapeStack.push(shapes.pop());
		reDraw();
	}
	
	
	// Ajax 용 Json String
	this.shapesToJson = function()
	{
		//alert(JSON.stringify(shapes));
		return JSON.stringify(shapes);
	}
	
	// 전달받은 shapes 객체로 교체합니다.
	this.jsonToShapes = function(json)
	{
		//alert(json[0].type);
		//for(var i=0 ; i<json.length ; i++)
		//{
			//alert(json[i].points);
			//json[i].points = JSON.parse(json[i].points);
				
		//}
		
		shapes = json;
	}
	
	this.setBackground = function(dataUrl){
		if(dataUrl == null || dataUrl=="" || dataUrl==" ")	return;
		photoDataUrl = dataUrl;
		photo = new Image();
		photo.onload = function(){
			isImaging = false;
			reDraw();
		}
		isImaging = true;
		photo.src = photoDataUrl;
	}
	
	this.removeBackground = function(){
		photoDataUrl = null;
		photo = null;
		reDraw();
	}
	
	this.getBackground = function(){
		return 	photoDataUrl;
	}
	
	this.setText = function(textUrl){
		if(textUrl == null || textUrl=="" || textUrl==" ")	return;
		textDataUrl = textUrl;
		text = new Image();
		text.onload = function(){
			isImaging = false;
			reDraw();
		}
		isImaging = true;
		text.src = textDataUrl;
	}
	
	this.removeText = function(){
		textDataUrl = null;
		text = null;
		reDraw();
	}
	
	this.getText = function(){
		return textDataUrl;
	}
	
	this.isEmpty = function(){
		return (shapes.length == 0) ? true : false;	
	}
	
	this.removeshape = function(index) {
		var num=0;
		for(var i=0 ; i<shapes.length ; i++){
			if(shapes[i].drawType == index)
			{
				num += 1;	
			}
		}
		
		shapes.splice(index, index+num);
		return shapes;
	}

}

// 하나의 Canvas에 대응되는 PreView 객체 입니다.
function DoodlePreview(canvas)
{
	var canvasObject = canvas;
	var canvasContext = canvasObject.getContext("2d");
	
	// 사진
	var photoDataUrl = null;
	var photo = null;
	
	// canvas를 다시 그립니다.
	var reDraw = function()
	{		
		// canvas를 먼저 지웁니다.
		canvasContext.clearRect(0, 0, $(canvasObject).width(), $(canvasObject).height());
		
		// 배경이미지가 있다면 먼저 그립니다.
		if(photo != null && photoDataUrl!=null){
			canvasContext.drawImage(photo, 0, 0, $(canvasObject).width(), $(canvasObject).height());
		}
	}
	
	// canvas를 다시 그리게 합니다. 노출
	this.rePaint = function()
	{
		reDraw();	
	}
	
	// canvas를 모두 지워버립니다.
	this.clear = function(){
		this.removeBackground();
		reDraw();	
	}
	
	this.setBackground = function(dataUrl){
		if(dataUrl == null || dataUrl=="" || dataUrl==" ")	return;
		photoDataUrl = dataUrl;
		photo = new Image();
		photo.onload = function(){
			isImaging = false;
			reDraw();
		}
		isImaging = true;
		photo.src = photoDataUrl;
	}
	
	this.removeBackground = function(){
		photoDataUrl = null;
		photo = null;
		reDraw();
	}
	
	this.getBackground = function(){
		return 	photoDataUrl;
	}
}
