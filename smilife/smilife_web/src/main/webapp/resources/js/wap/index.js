/**
 * baseUrl 配置JS目录
 * path ： 配置路径
 * shim ： 配置不支持AMD模块的插件 
 */
require.config({
	baseUrl: Global.webPath + '/resources/js/',
	paths: { 
		'jquery':'http://apps.bdimg.com/libs/jquery/2.1.4/jquery.min',
		'lazyload' :'lazyload'
	},
	shim : {
		TouchSlide : {
			deps : ['jquery',Global.webPath + '/resources/js/TouchSlide.1.1.js']
		},
		lazyload : {
			deps : ['jquery']
		}
	}
}); 

/**
 * 引用非AMD模块的插件touchSlide
 */
require(['TouchSlide.1.1'],function(t){
	$.get(Global.webPath+"/advert_invoke.htm?id=4", function(x){
		$("#ad1").html(x);
		TouchSlide({slideCell:'#focus',titCell:'.hd ul',mainCell:'.bd ul',effect:'leftLoop',autoPlay:true,autoPage:true});
	});
});

/**
 * 引用jquery插件
 */
require(['jquery'],function($){
	
/**
 * @param  remian_id   String
 */
function timeCount(remain_id){
   function _fresh(){
      var nowDate = new Date(),                                    //当前时间
      endDate = new Date($('#'+remain_id).attr('endtime')),    //截止时间
      totalS  = parseInt((endDate.getTime() - nowDate.getTime()) / 1000);     //总秒数
      _day    = parseInt(totalS / 3600 / 24)+"";
      _hour   = parseInt(totalS / 3600% 24)+"";
      _minute = parseInt((totalS / 60) % 60)+"";
      _second = parseInt(totalS % 60)+"";
	  d_html = "";
	  if(_day.length<2){
			d_html = "<b>0</b>";
	  }
	  for(var n = 0;n<_day.length;n++){
     		var q = _day.substring(n,n+1);
			d_html = d_html+'<b>'+q+'</b>';
	  }
		    d_html = d_html+"<b>天</b>"
	        h_html = "";
	  if(_hour.length<2){
			h_html = "<b>0</b>";
	  }
	  for(var n = 0;n<_hour.length;n++){
     		var q = _hour.substring(n,n+1);
			h_html = h_html+'<b>'+q+'</b>';
	  }
		    m_html = "";
	  if(_minute.length<2){
			m_html = "<b>0</b>";
	  }
	  for(var n = 0;n<_minute.length;n++){
     		var q = _minute.substring(n,n+1);
			m_html =m_html+ '<b>'+q+'</b>'
	  }
		    s_html = "";
	  if(_second.length<2){
			s_html = "<b>0</b>";
	  }
	  for(var n = 0;n<_second.length;n++){
     		var q = _second.substring(n,n+1);
			s_html =s_html+ '<b>'+q+'</b>'
	  }
      $('#'+remain_id).html(d_html+'<strong>:</strong>'+h_html+'<strong>:</strong>'+m_html+'<strong>:</strong>'+s_html);
      if( totalS <= 0){
        $('#'+remain_id).html('该0元试用已结束');
        clearInterval(sh);
      }
  }
   	  _fresh();
      var sh = setInterval(_fresh,1000);
}
window.search_form = function(){
	$("#theForm").submit();
}
function isWeiXin(){
    var ua = window.navigator.userAgent.toLowerCase();
    if(ua.match(/MicroMessenger/i) == 'micromessenger'){
        return true;
    }else{
        return false;
    }
}	
/**
 * @param source  数组
 * @param callback 回掉函数
 */
function loadImg(source,callback){
	var images = {};
	var count = 0;
	for(var i=0;i<source.length;i++){
		images[i] = new Image();
		images[i].index = i;
		images[i].onload = function(){
			count++;
			if(count == source.length){
				callback && callback();
			}	
		}
		images[i].onerror = function(){
			//如果图片加载不出来，就用一张默认的图片代替
			images[this.index] = 'http://img.xingmeihui.com/upload/system/self_goods/taobao/c356595ed226ef8381d082b725665d92.tbi';
			source[this.index] = 'http://img.xingmeihui.com/upload/system/self_goods/taobao/c356595ed226ef8381d082b725665d92.tbi';
		}	
		images[i].src = source[i];
	}
}

function ajax_sel(){
	var ycount = $("#hots_size").val();
	$.ajax({type:'POST',url:Global.webPath+'/wap/goods_hots_ajax.htm',data:{"count":ycount}, 
		 beforeSend:function(data){
		     $('.loading_effect').show();
		 },
		 success:function(data){
			var list = $.parseJSON(data);
			for(var i in list){
				Global.arr_img.push(list[i].img);
			}
			if(list != "" && list != null){
			    $.each(list, function(i,obj){
					$(".hot_goods>ul").append("<li><a href="+Global.webPath+"/wap/goods.htm?id="+obj.id+"><div class='hot_img_box'><img data-original="+obj.img+" src="+Global.webPath+"/resources/style/system/front/wap/images/load.gif style='max-height:100%'/></div><p class='hot_goods_price'>¥"+obj.store_price+"</p><p class='hot_goods_desc'>"+obj.goods_name+"</p></a></li>");
					$("#hots_size").val(obj.count);
			 	});
			}else{
				$(".load_more_goods").html("<a href='javascript:void(0)' class='load_more_goods'>没有更多了！</a>");
		    }
			$("img").lazyload({    
				placeholder:Global.webPath+'/resources/style/system/front/wap/images/load.gif',
				event : "scroll",
				effect : "fadeIn"
			});
		},
		complete:function(){
			$('.loading_effect').hide();
		}
		
	});
}


$(function(){

$("#load_more").click(function(){
	ajax_sel();
});	
	
	//商城未登录情况下发送一次请求到cas
$("[containid='ad2']").each(function(){
	var id = this.getAttribute("adid");
	var _this = this;
	$.get(Global.webPath+"/advert_invoke.htm?id="+ id +"&index=1&random=" + Global.CommUtil , function(x){
		$(_this).html(x);
	});
});
$("[containid='ad3']").each(function(){
	var id = this.getAttribute("adid");
	var _this = this;
	$.get(Global.webPath+"/advert_invoke.htm?id="+ id +"&random=" + Global.CommUtil, function(x){
		$(_this).html(x);
	});
});
$("[containid='ad4']").each(function(){
	var id = this.getAttribute("adid");
	var _this = this;
	$.get(Global.webPath+"/advert_invoke.htm?id="+ id +"&index=2&random=" + Global.CommUtil, function(x){
		$(_this).html(x);
	});
});


$("#keyword").focus(function(){
	$("#hotwords").hide();
}).blur(function(){
	if($.trim(this.value) == ""){
		$("#hotwords").show();
	}
}).val("");
//获取数据库（$!config.preInputKeys），把数据值有，号换成空格
var preInputKeys= Global.preInputKey;
$("#hotwords").html(preInputKeys.replace(/,/g, " ")).click(function(){
	$("#keyword").focus();
});

$("#searchBtn").click(function(){
	if($("#hotwords").css("display") != "none"){
		$("#keyword").val($("#hotwords").hide().html());
	}
});


timeCount("time_down_info");


if(isWeiXin()){
    $(".phone_top").hide();
}else{
	  $(".phone_top").show();
}

$('.re_name').each(function(){
	var _longText = $(this).text();
	$(this).siblings('.re_short_text').html(_longText.substring(0,6));
});

});
	
	
});
/**
 * 引入$ lazyloadJS
 * 图片的延迟加载
 */
require(['jquery','lazyload'],function($,lazyload){
	$("img").lazyload({    
		placeholder:Global.webPath+'/resources/style/system/front/wap/images/load.gif',
		event : "scroll",
		effect : "fadeIn"
	});
	
});

