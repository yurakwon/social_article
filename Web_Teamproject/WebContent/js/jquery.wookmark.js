/*!
  jQuery Wookmark plugin 0.5
  @name jquery.wookmark.js
  @author Christoph Ono (chri@sto.ph or @gbks)
  @version 0.5
  @date 3/19/2012
  @category jQuery plugin
  @copyright (c) 2009-2012 Christoph Ono (www.wookmark.com)
  @license Licensed under the MIT (http://www.opensource.org/licenses/mit-license.php) license.
*/
$.fn.wookmark = function(options) {
  
  if(!this.wookmarkOptions) {
    this.wookmarkOptions = $.extend( {
        container: $('body'),
        offset: 2,
        autoResize: false,
        itemWidth: $(this[0]).outerWidth(),
        resizeDelay: 50
      }, options);
  } else if(options) {
    this.wookmarkOptions = $.extend(this.wookmarkOptions, options);
  }

  // Layout variables. 초기화 시켜줌
  if(!this.wookmarkColumns) {
    this.wookmarkColumns = null;
    this.wookmarkContainerWidth = null;
  }
  
  // Main layout function.
  this.wookmarkLayout = function() {
    // Calculate basic layout parameters.
    // 아이템 한칸의 길이
    var columnWidth = this.wookmarkOptions.itemWidth + this.wookmarkOptions.offset;
    // 현재 페이지 가로길이
    var containerWidth = this.wookmarkOptions.container.width();
    // 아이템이 배치될 기둥 수 
    var columns = Math.floor((containerWidth+this.wookmarkOptions.offset)/columnWidth);
    // 아이템이 시작할 가로위치
    var offset = Math.round((containerWidth - (columns*columnWidth-this.wookmarkOptions.offset))/2);
    
    // If container and column count hasn't changed, we can only update the columns.
    // 위에서 필요한 길이들을 계산하고 여기서 레이아웃 함수 호출
    var bottom = 0;
    if(this.wookmarkColumns != null && this.wookmarkColumns.length == columns) {
		// 재계산한 기둥수와 현재 기둥수가 일치할때
        bottom = this.wookmarkLayoutColumns(columnWidth, offset);
    } else {
		// 맨처음 or 나중에 새로 배치할때
        bottom = this.wookmarkLayoutFull(columnWidth, columns, offset);
    }
    
    // Set container height to height of the grid.
    this.wookmarkOptions.container.css('height', bottom+'px');
  };
  
  /**
   * Perform a full layout update.
   */
  this.wookmarkLayoutFull = function(columnWidth, columns, offset) {
    // Prepare Array to store height of columns.
    var heights = [];
    while(heights.length < columns) {
      heights.push(0);
    }
    
    // Store column data. 기둥의 갯수를 구함
    this.wookmarkColumns = [];
    while(this.wookmarkColumns.length < columns) {
      this.wookmarkColumns.push([]);
    }
    
    // Loop over items.             리스트 아이템 갯수
    var item, top, left, i=0, k=0, length=this.length, bottom = 0;
    
    for(; i<length; i++ ) {
		item = $(this[i]);
      
		// 각각의 기둥에 대해서
		if(i < columns) //첫행의 위치는 0
			shortest = 0;
		else // 다음행의 위치는 각각의 기둥의 높이
			shortest = heights[i % columns];
		
		// Postion the item.
		item.css({
			position: 'absolute',
			top: shortest + 'px',
			left: ((i % columns)*columnWidth + offset)+'px' // offset은 왼쪽에서부터 거리
		});
		
		// 지금 기둥의 높이는 현재높이 + 아이템의 높이 + 아이템간 간격
		heights[i % columns] = shortest + item.outerHeight() + this.wookmarkOptions.offset;
		bottom = Math.max(bottom, heights[i % columns]);
		this.wookmarkColumns[i % columns].push(item); // 기둥만큼 줄을 가지고 있어서 해당 기둥에 넣어주는 코드
    }
    
    return bottom;
  };
  
  /**
   * This layout function only updates the vertical position of the 
   * existing column assignments.
   */
  this.wookmarkLayoutColumns = function(columnWidth, offset) {
    var heights = [];

    while(heights.length < this.wookmarkColumns.length) {
      heights.push(0);
    }
    
    var i=0, length = this.wookmarkColumns.length, column;
    var k=0, kLength, item;
    var bottom = 0;
    for(; i<length; i++) {
      column = this.wookmarkColumns[i];
      kLength = column.length;
      for(k=0; k<kLength; k++) {
        item = column[k];
        item.css({
          left: (i*columnWidth + offset)+'px',
          top: heights[i] + 'px'
        });
        heights[i] += item.outerHeight() + this.wookmarkOptions.offset;
        
        bottom = Math.max(bottom, heights[i]);
      }
    }
    
    return bottom;
  };
  
  // Listen to resize event if requested.
  this.wookmarkResizeTimer = null;
  if(!this.wookmarkResizeMethod) {
    this.wookmarkResizeMethod = null;
  }
  if(this.wookmarkOptions.autoResize) {
    // This timer ensures that layout is not continuously called as window is being dragged.
    this.wookmarkOnResize = function(event) {
      if(this.wookmarkResizeTimer) {
        clearTimeout(this.wookmarkResizeTimer);
      }
      this.wookmarkResizeTimer = setTimeout($.proxy(this.wookmarkLayout, this), this.wookmarkOptions.resizeDelay)
    };
    
    // Bind event listener.
    if(!this.wookmarkResizeMethod) {
      this.wookmarkResizeMethod = $.proxy(this.wookmarkOnResize, this);
    }
    $(window).resize(this.wookmarkResizeMethod);
  };
  
  /**
   * Clear event listeners and time outs.
   */
  this.wookmarkClear = function() {
    if(this.wookmarkResizeTimer) {
      clearTimeout(this.wookmarkResizeTimer);
      this.wookmarkResizeTimer = null;
    }
    if(this.wookmarkResizeMethod) {
      $(window).unbind('resize', this.wookmarkResizeMethod);
    }
  };
  
  // Apply layout
  this.wookmarkLayout();
  
  // Display items (if hidden).
  this.show();
};
