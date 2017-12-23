﻿/** 
 处理全选或全部反选
**/
function selectAll(obj){
	var status=jQuery(obj).attr("checked");
	var id=jQuery(obj).attr("id");
  	if(status=="checked"){
		jQuery("#ListForm").find(":checkbox[id!="+id+"]").attr("checked","checked");
	}else{
	    jQuery("#ListForm").find(":checkbox[id!="+id+"]").attr("checked",false);	
	}
}
/* 
系统通用方法，根据参数来决定处理的url和参数
*/
function cmd(){
	var url=arguments[0];
	var mulitId="";
	jQuery("#ListForm").find(":checkbox:checked").each(function(){
	  if(jQuery(this).val()!=""){	  
	    mulitId+=jQuery(this).val()+",";
	  }
	});
	if(mulitId!=""){
	  jQuery("#ListForm #mulitId").val(mulitId);
	  if(confirm("\u786e\u5b9a\u8981\u6267\u884c\u8be5\u64cd\u4f5c\uff1f")){//确定要执行该操作？
	     jQuery("#ListForm").attr("action",url);
	     jQuery("#ListForm").submit();
	  }
	}else{
       alert("\u81f3\u5c11\u9009\u62e9\u4e00\u6761\u6570\u636e\u8bb0\u5f55");//至少选择一条数据记录
	}
}
/**/
/* 火狐下取本地全路径 */
function getFullPath(obj)
{
  if(obj){
    //ie
    if (window.navigator.userAgent.indexOf("MSIE")>=1){
        obj.select();
        if(window.navigator.userAgent.indexOf("MSIE") == 25){
            obj.blur();
         }
        return document.selection.createRange().text;
     } else if(window.navigator.userAgent.indexOf("Firefox")>=1){  //firefox
	    if(obj.files){
          return window.URL.createObjectURL(obj.files.item(0)); 
        }
         return obj.value;
     }
      return obj.value;
  }
}
//自动生成查询条件
function query(){
  jQuery("#queryCondition").empty();
  jQuery.each(jQuery("#queryForm :input"),function(){
	 	if(this.type!="button"&&this.value!=""){
		  jQuery("#queryCondition").append("<input name='q_"+this.name+"'type='hidden' id='q_"+this.name+"' value='"+this.value+"' />");	
		}	
  });
 jQuery("#ListForm").submit();
}
//表单方式分页
function gotoPage(n){
	jQuery("#currentPage").val(n);
	jQuery("#ListForm").submit();
}
/**增加系统提示*/
function tipStyle(){
  if(jQuery.isFunction(jQuery().poshytip)){
    jQuery("input[title!='']").poshytip({
				className: 'tip-skyblue',
				timeOnScreen :2000,
          	    alignTo: 'cursor',
	            alignX: 'right',
	            alignY: 'bottom',
	            offsetX: 3,
	            offsetY: 3
  });
  jQuery("img[title!='']").poshytip({
				className: 'tip-skyblue',
				timeOnScreen :2000,
          	    alignTo: 'cursor',
	            alignX: 'right',
	            alignY: 'bottom',
	            offsetX: 3,
	            offsetY: 3
  });
  jQuery("a[title!='']").poshytip({
				className: 'tip-skyblue',
				timeOnScreen :2000,
          	    alignTo: 'cursor',
	            alignX: 'right',
	            alignY: 'bottom',
	            offsetX: 3,
	            offsetY: 3
  });
  jQuery("textarea[title!='']").poshytip({
				className: 'tip-skyblue',
				timeOnScreen :2000,
          	    alignTo: 'cursor',
	            alignX: 'right',
	            alignY: 'bottom',
	            offsetX: 3,
	            offsetY: 3
  });
 }
}
//模拟alert
var alert_timer_id;
function showDialog() {
  var id=arguments[0];//窗口id
  var title=arguments[1];   //窗口标题
  var content=arguments[2];//提示内容
  var type=arguments[3];//0为倒计时提示框，1为确认框（包含2个按钮，点击确定执行回调）,2为提示确认框（只含有一个确认按钮）
  var icon=arguments[4];//显示图标，包括warning,succeed,question,smile,sad,error
  var second=arguments[5];//倒计时时间数,默认时间5秒，
  var confirm_action=arguments[6];//callback方法
  var back_function_args=arguments[7];//带参数回调函数发送的参数
  if(id==undefined||id==""){
    id=1;
  }
  if(title==undefined||title==""){
    title="\u7cfb\u7edf\u63d0\u793a";//系统提示
  }
  if(type==undefined||type==""){
    type==0;
  }
  if(icon==undefined||icon==""){
     icon="succeed";
  }
  if(second==undefined||second==""){
     second=5;
  }
  var s="<div id='"+id+"'><div class='message_white_content'> <a href='javascript:void(0);' class='white_close' onclick='javascript:jQuery(\"#"+id+"\").remove();'></a><div><div class='message_white_iframe'><h3 class='message_white_title'><span>"+title+"</span></h3><div class='message_white_box'><span class='message_white_img_"+icon+"'></span><span class='message_white_font'>"+content+"</span></div><h3 class='message_white_title_bottom'><span id='time_down'>"+second+"</span>\u79d2\u540e\u7a97\u53e3\u5173\u95ed</h3></div></div></div><div class='black_overlay'></div>";
  
  var c="<div id='"+id+"'><div class='message_white_content'> <a href='javascript:void(0);' class='white_close' onclick='javascript:jQuery(\"#"+id+"\").remove();'></a><div ><div class='message_white_iframe_del'><h3 class='message_white_title'><span>"+title+"</span></h3><div class='message_white_box_del'><span class='message_white_img_"+icon+"'></span><span class='message_white_font' style='font-size:14px;'>"+content+"</span></div>   <div class='message_white_box1'><input id='sure' type='button' value='\u786e\u5b9a'/><input id='cancel' type='button' value='\u53d6\u6d88'/></div>    </div></div></div><div class='black_overlay'></div>";
  var t="<div id='"+id+"'><div class='message_white_content'> <a href='javascript:void(0);' class='white_close' onclick='javascript:jQuery(\"#"+id+"\").remove();'></a><div ><div class='message_white_iframe_del'><h3 class='message_white_title'><span>"+title+"</span></h3><div class='message_white_box_del'><span class='message_white_img_"+icon+"'></span><span class='message_white_font' style='font-size:14px;'>"+content+"</span></div>   <div class='message_white_box2'><input id='ok' type='button' value='\u786e\u5b9a'/></div></div></div></div><div class='black_overlay'></div>";
  if(type==0){//消息框
   jQuery("body").append(s);
  }
  if(type==1){//确认并回调框
   jQuery("body").append(c);	  
  } 
  if(type==2){//确认框
   jQuery("body").append(t);
  }
  var top=jQuery(window).scrollTop()+(jQuery(window).height()-jQuery(document).outerHeight())/2; 
  jQuery(".message_white_content").css("margin-top",jQuery(window).scrollTop()+"px");
  var h=jQuery(document).height();
  jQuery('.black_overlay').css("height",h);
  //设置关闭时间

  if(confirm_action==undefined||confirm_action==""){
	 alert_timer_id=window.setInterval("closewin('"+id+"','')",1000);
  }else{
	  if(back_function_args==undefined||back_function_args==""){
    	 alert_timer_id=window.setInterval("closewin('"+id+"',"+confirm_action+")",1000);
  	}else{
		 alert_timer_id=window.setInterval("closewin('"+id+"',"+confirm_action+",'"+back_function_args+"')",1000);
	} 
  }
  //点击确定执行回调
  jQuery("#sure").click(function(){						 
		jQuery("#"+id).remove();
		runcallback(confirm_action);	
	});
  //点击确定关闭窗口
  jQuery("#ok").click(function(){						 
		jQuery("#"+id).remove();
		runcallback(confirm_action);
	});
  function runcallback(callback){   
    if(confirm_action!=undefined&&confirm_action!=""){
    	if(back_function_args==undefined||back_function_args==""){
    	   callback(); 
  	   }else{
		   callback(back_function_args); 
	   } 
	}
  } 
//点击取消
  jQuery("#cancel").click(function(){
	jQuery("#"+id).remove();							   
	});
  	//点击选择发布类型，将参数添加到页面隐藏域中
  	jQuery("a[id^=share_select_]").click(function(){
	jQuery("#share_select_mark").val(jQuery(this).attr("share_mark"));
	jQuery("#"+id).remove();	
	runcallback(confirm_action);
	});
} 

function closewin(id,callback,args) {
  var count=parseInt(jQuery("#"+id+" span[id=time_down]").text());
  count--;
  if(count==0){
   	  window.clearInterval(alert_timer_id);
     if(callback!=""){
		 if(args==undefined||args==""){
    		callback(); 
  	 	 }else{
			callback(args); 
		  } 
  		}else{//没有回调，移除当前弹框
		     jQuery("#"+id).remove();	
		}
  }else{
   jQuery("#"+id+" span[id=time_down]").text(count);
  }
} 
//打开系统聊天组件 
function open_im(){
  var goods_id=arguments[0];
  var url=arguments[1];
  var type=arguments[2];  //打开类型，user为用户打开，store为商家打开，plat为平台打开
  var to_type=arguments[3];
  var store_id=arguments[4];
  if(type=="store"){
  window.open (url+"/store_chatting.htm",'store','height=660,width=1000,top=200,left=400,toolbar=no,menubar=no,scrollbars=no, resizable=no,location=no, status=no');	  
	  }
  if(type=="user"){
  window.open (url+"/user_chatting.htm?gid="+goods_id+"&type="+to_type+"&store_id="+store_id,'','height=660,width=1000,top=200,left=400,toolbar=no,menubar=no,scrollbars=no, resizable=no,location=no, status=no');		  
	 }
  if(type=="plat"){
  window.open (url+"/admin/plat_chatting.htm",'plat','height=660,width=1000,top=200,left=400,toolbar=no,menubar=no,scrollbars=no, resizable=no,location=no, status=no');		  
	 }
}
/**
系统加载
*/
jQuery(document).ready(function(){
  //改变系统提示的样式
  jQuery("span .w").mousemove(function(){
	var id=jQuery(this.parentNode).attr("id");
	if(id="nothis"){
	   jQuery(this.parentNode).attr("id","this")
	}
  }).mouseout(function(){
     var id=jQuery(this.parentNode).attr("id");
	 if(id="this"){
	   jQuery(this.parentNode).attr("id","nothis")
	 }
  });
  //
  tipStyle();
  //单独弹出一个编辑窗口
  jQuery(document).delegate("a[dialog_uri],input[dialog_uri],dt[dialog_uri]", "click",function(e){
    var dialog_uri=jQuery(this).attr("dialog_uri");
	var dialog_title=jQuery(this).attr("dialog_title");
    var dialog_id=jQuery(this).attr("dialog_id");
	var dialog_height=jQuery(this).attr("dialog_height");
	var dialog_width=jQuery(this).attr("dialog_width");
	var dialog_top=jQuery(this).attr("dialog_top");
	var dialog_left=300;
	jQuery("#"+dialog_id).remove();
	 if(dialog_uri!="undefined"){
       jQuery("body").append("<div id='"+dialog_id+"'><div class='white_content'> <a href='javascript:void(0);' dialog_uri='undefined' class='white_close' onclick='javascript:jQuery(\"#"+dialog_id+"\").remove();'></a><div class='white_box'><h1>"+dialog_title+"</h1><div class='content_load'></div></div></div><div class='black_overlay'></div></div>");
	   e.preventDefault(); 
	   if(dialog_top==undefined||dialog_top==""){
		    dialog_top=80;
	     //dialog_top=jQuery(window).scrollTop()+(jQuery(window).height()-jQuery(document).outerHeight())/2-dialog_height/2;
	   }else{
		 //dialog_top=parseInt(dialog_top)+jQuery(window).scrollTop();
		  dialog_top=80;
	   }
	   var h=jQuery(document).height();
       jQuery('.black_overlay').css("height",h);
	   var dialog_left=(jQuery(document).width()-dialog_width)/2;
	    //jQuery(".white_content").css("position","absolute").css("top",parseInt(dialog_top)+"px").css("left",parseInt(dialog_left)+"px");
       jQuery(".white_content").css("position","absolute").css("top",parseInt(80)+"px").css("left",parseInt(dialog_left)+"px");
	   jQuery.ajax({type:'POST',url:dialog_uri,async:false,data:'',success:function(html){
	    	jQuery(".content_load").remove(); 
			jQuery("#"+dialog_id+" .white_content").css("width",dialog_width);
		    jQuery("#"+dialog_id+" .white_box h1").after(html);
		    jQuery("#"+dialog_id).show();  
	   }});
	   jQuery("#"+dialog_id+" .white_box h1").css("cursor","move")
	   jQuery("#"+dialog_id+" .white_content").draggable({handle:" .white_box h1"});
	 }
  });
  //需要结合页面中的checkbox选择框，必须选择一条数据才可以操作
  jQuery(document).delegate("a[ck_dialog_uri],input[ck_dialog_uri],dt[ck_dialog_uri]","click",function(e){
      var mulitId="";
	  jQuery("#ListForm").find(":checkbox:checked").each(function(){
	    if(jQuery(this).val()!=""){	  
	      mulitId+=jQuery(this).val()+",";
	    }
	  });
	  var dialog_uri=jQuery(this).attr("ck_dialog_uri");
	  var dialog_title=jQuery(this).attr("ck_dialog_title");
      var dialog_id=jQuery(this).attr("ck_dialog_id");
	  var dialog_height=jQuery(this).attr("ck_dialog_height");
	  var dialog_width=jQuery(this).attr("ck_dialog_width");
	  var dialog_top=jQuery(this).attr("ck_dialog_top");
	  var dialog_left=300;
	  if(dialog_uri!=undefined&&dialog_uri!=""){
	  if(mulitId!=""){  
        jQuery("body").append("<div id='"+dialog_id+"'><div class='white_content'> <a href='javascript:void(0);' class='white_close' onclick='javascript:jQuery(\"#"+dialog_id+"\").remove();'></a><div class='white_box'><h1>"+dialog_title+"</h1><div class='content_load'></div></div></div><div class='black_overlay'></div></div>");
	   e.preventDefault(); 
	   if(dialog_top==undefined||dialog_top==""){
	     dialog_top=jQuery(window).scrollTop()+(jQuery(window).height()-jQuery(document).outerHeight())/2-dialog_height/2;
	   }else{
		 dialog_top=parseInt(dialog_top)+jQuery(window).scrollTop();
	   }
	   var h=jQuery(document).height();
       jQuery('.black_overlay').css("height",h);
	   var dialog_left=(jQuery(document).width()-dialog_width)/2;
	   if(dialog_uri.indexOf("?")>=0){
	     dialog_uri=dialog_uri+"&mulitId="+mulitId;
	   }else{
	     dialog_uri=dialog_uri+"?mulitId="+mulitId;
	   }
       jQuery(".white_content").css("position","absolute").css("top",parseInt(dialog_top)+"px").css("left",parseInt(dialog_left)+"px");
	   jQuery.ajax({type:'POST',url:dialog_uri,async:false,data:'',success:function(html){
	    	jQuery(".content_load").remove(); 
			jQuery("#"+dialog_id+" .white_content").css("width",dialog_width);
		    jQuery("#"+dialog_id+" .white_box h1").after(html);
		    jQuery("#"+dialog_id).show();  
	   }});
	   jQuery("#"+dialog_id+" .white_box h1").css("cursor","move")
	   jQuery("#"+dialog_id+" .white_content").draggable({handle:" .white_box h1"});
	  }else{
	    alert("至少选择一条记录");
	  }
	 }
  });
  //判断是全球购订单就省略一部分订单号码让全球购logo显示
  jQuery(".fl img").siblings("span").addClass("fl-order");
});

