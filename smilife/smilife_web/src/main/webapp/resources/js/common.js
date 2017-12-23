define(["jquery", "layer", "cookie"], function($, layer) {
	layer.config({
		path: Global.webPath + "/resources/js/layer/"
	});
	var webPath = Global ? Global.webPath : "";

	//获取数据库（$!config.preInputKeys），把数据值有，号换成空格
	$("#keyword").attr("placeholder", Global.preInputKeys.replace(/,/g, " "));

	//触发搜索按钮事件
	$(".h_sear_btn").click(function() {
		//判断是否为空隐形搜索，默认为隐形搜索值
		if ($("#keyword").val().trim() == "") {
			$("#keyword").val($("#keyword").attr("placeholder"));
		}
	});

	search_history_data();
	var head_h = $("#head_h").height();
	//搜索框获取焦点
	$("#keyword").click(function() {
		var top = $(document).scrollTop();
		$("div[id^='head_search_goodslist']").hide();
		if (top > head_h) {
			$("#head_search_history_up").fadeIn();
		} else {
			$("#head_search_history").fadeIn();
		}
	}).keyup(function() {
		if ($(this).val() != '') {
			$("div[id^='head_search_history']").hide();
		};
		var keyword = jQuery(this).val();
		//查询分类
		jQuery.post(Global.webPath + "/search_goodsclass.htm", {
			"keyword": keyword
		}, function(data) {
			if (data) {
				search_goodsclass_initialize();
				$.each(data.parent_gc, function(index, item) {
					if (index == 0) {
						//构造提示框
						$("#head_search_goodslist>dl>dt>ul").append("<li class='this'><span><a href='" + webPath + "/store_goods_list_" + item.id + ".htm' target='_blank'>" + item.name + "</a></span></li>");
						var str = "<dd id='head_search_dd_" + item.id + "'><span class='head_search_history_hot' style='margin-bottom:20px; height:25px; line-height:25px;'>" + item.name + "</span><span onclick='search_gc_close()' class='head_search_history_hot' style='margin-bottom:20px; height:25px;float:right; line-height:15px; color:#999;;cursor:pointer'>关闭</span><ul></ul></dd>"
						$("#head_search_goodslist>dl").append(str);
						//下方的提示框
						$("#head_search_goodslist_up>dl>dt>ul").append("<li class='this'><span><a href='" + webPath + "/store_goods_list_" + item.id + ".htm' target='_blank'>" + item.name + "</a></span></li>");
						str = "<dd id='head_search_up_dd_" + item.id + "'><span class='head_search_history_hot' style='margin-bottom:20px; height:25px; line-height:25px;'>" + item.name + "</span><span onclick='search_gc_close()' class='head_search_history_hot' style='margin-bottom:20px; height:25px;float:right; line-height:15px; color:#999;;cursor:pointer'>关闭</span><ul></ul></dd>"
						$("#head_search_goodslist_up>dl").append(str);
					} else {
						//构造提示框
						$("#head_search_goodslist>dl>dt>ul").append("<li><span><a href='" + webPath + "/store_goods_list_" + item.id + ".htm' target='_blank'>" + item.name + "</a></span></li>");
						var str = "<dd id='head_search_dd_" + item.id + "' style='display:none'><span class='head_search_history_hot' style='margin-bottom:20px; height:25px; line-height:25px;'>" + item.name + "</span><span onclick='search_gc_close()' class='head_search_history_hot' style='margin-bottom:20px; height:25px;float:right; line-height:15px; color:#999;;cursor:pointer'>关闭</span><ul></ul></dd>"
						$("#head_search_goodslist>dl").append(str);
						//下方的提示框
						$("#head_search_goodslist_up>dl>dt>ul").append("<li><span><a href='" + webPath + "/store_goods_list_" + item.id + ".htm' target='_blank'>" + item.name + "</a></span></li>");
						var str = "<dd id='head_search_up_dd_" + item.id + "' style='display:none'><span class='head_search_history_hot' style='margin-bottom:20px; height:25px; line-height:25px;'>" + item.name + "</span><span onclick='search_gc_close()' class='head_search_history_hot' style='margin-bottom:20px; height:25px;float:right; line-height:15px; color:#999;;cursor:pointer'>关闭</span><ul></ul></dd>"
						$("#head_search_goodslist_up>dl").append(str);
					}
				})
				$.each(data.list_child, function(index, childs) {
					$.each(childs, function(i, child) {
						$("#head_search_goodslist>dl>dd").eq(index).find("ul").append("<li><a href='" + webPath + "/store_goods_list_" + child.id + ".htm' target='_blank'>" + child.name + "</a></li>");
						$("#head_search_goodslist_up>dl>dd").eq(index).find("ul").append("<li><a href='" + webPath + "/store_goods_list_" + child.id + ".htm' target='_blank'>" + child.name + "</a></li>");
					})
				})
				var top = $(document).scrollTop();
				if (top > head_h) {
					$("#head_search_goodslist_up").fadeIn();
				} else {
					$("#head_search_goodslist").fadeIn();
				}
				//监听搜索分类提示鼠标上移事件
				$("div[id^='head_search_goodslist']>dl>dt>ul>li").mouseenter(function() {
					var index = $(this).index();
					$("div[id^='head_search_goodslist']>dl>dt>ul>li").removeClass("this");
					$("#head_search_goodslist>dl>dt>ul>li").eq(index).addClass("this");
					$("#head_search_goodslist_up>dl>dt>ul>li").eq(index).addClass("this");
					$("dd[id^='head_search_dd_']").hide();
					$("dd[id^='head_search_dd_']").eq(index).show();
					$("dd[id^='head_search_up_dd_']").hide();
					$("dd[id^='head_search_up_dd_']").eq(index).show();
				});
			} else {
				search_goodsclass_initialize();
			}
		}, "json");
		//查询分类END
	});
	$("div[id^='head_search_history']").mouseenter(function() {
		$(this).show();
	}).mouseleave(function() {
		$(this).hide();
	});
	$(window).scroll(function() {
		var top = $(document).scrollTop();
		if (top > head_h) {
			if ($("#head_search_history").is(":visible")) {
				$("#head_search_history").hide();
				$("#head_search_history_up").show();
			};
			if ($("#head_search_goodslist").is(":visible")) {
				$("#head_search_goodslist").hide();
				$("#head_search_goodslist_up").show();
			};
		} else {
			if ($("#head_search_history_up").is(":visible")) {
				$("#head_search_history_up").hide();
				$("#head_search_history").show();
			};
			if ($("#head_search_goodslist_up").is(":visible")) {
				$("#head_search_goodslist_up").hide();
				$("#head_search_goodslist").show();
			};
		}

	});
	//选定某一搜索历史
	$("#head_search_history").find("dl>dt>ul>li>span").click(function() {
		$("#keyword").val($(this).attr("data"));
		$("#searchForm").submit();
	});
	//鼠标经过用户中心
	$("#top_user_sp").mouseenter(function() {
		if ($("#top_user_hb").size() == 0) {
			$.ajax({
				type: 'POST',
				url: webPath + '/head_ajax_usercenter.htm',
				data: '',
				beforeSend: function() {
					$("#top_user_hid").html('<div class="top_user_hb" id="top_user_hb"><span class="top_user_loading"><img src="' + webPath + '/resources/style/system/front/default/images/loading.gif" /></span><b class="top_user_warning">加载中，请稍后...</b></div>');
					$("#top_user_hid").show();
				},
				success: function(data) {
					$("#top_user_hid").empty();
					$("#top_user_hid").html(data);
				}
			});
		} else {
			$("#top_user_hid").show();
		}
		$("#top_user_sp").addClass("top_user_sp_this");
	});
	$("#top_user").mouseleave(function() {
		$("#top_user_hid").hide();
		$("#top_user_sp").removeClass("top_user_sp_this");
	});

	//鼠标经过购物车
	$("#goodscar_sp").mouseenter(function() {
		var gstatus = $("#goodscar_con_box").attr("mark");
		if (gstatus == "none")
			$.ajax({
				type: 'POST',
				url: webPath + '/cart_menu_detail.htm',
				data: '',
				beforeSend: function() {
					$("#goodscar_con_box").empty().html(' <div><span class="top_user_loading"><img src="' + webPath + '/resources/style/system/front/default/images/loading.gif" /></span><b class="top_user_warning">加载中，请稍后...</b></div>');
					$("#goodscar_con_box").show();
				},
				success: function(data) {
					$("#goodscar_con_box").attr("mark", "show");
					$("#goodscar_con_box").empty();
					$("#goodscar_con_box").html(data);


				}
			});
		$("#goodscar_sp").addClass("goodscar_sp_this");
	});
	$("#goodscar").mouseleave(function() {
		$("#goodscar_con_box").attr("mark", "none");
		$("#goodscar_con_box").hide();
		$("#goodscar_sp").removeClass("goodscar_sp_this");
	});
	$(window).scroll(function() {
		var top = $(document).scrollTop();
		if (top == 0) {
			$("#back_box").hide();
			$(".back_box_x").hide();
		} else {
			$("#back_box").show();
			$(".back_box_x").show();
		}
	});
	//
	$("#toTop").click(function() {
		$('body,html').animate({
			scrollTop: 0
		}, 1000);
		return false;
	});

	//nav
	$("#navul li").each(function() {
		var original_url = $(this).attr("original_url");
		if ("$!{current_url}".indexOf(original_url) >= 0) {
			$(this).addClass("this");
		}
	});
	$(".top_pull").mouseenter(function() {
		$(this).find("div").show();
		$(this).find("em").addClass("em_this");
		$(this).find("s").addClass("this");
	}).mouseleave(function() {
		$(this).find("div").hide();
		$(this).find("em").removeClass("em_this");
		$(this).find("s").removeClass("this");
	});

	if ($(".nav_center").length) {


		$("#navul li a").each(function() {
			var original_url = $(this).attr("href");
			if (window.str_op != "") {
				if (original_url.indexOf(window.str_op) >= 0) {
					$(this).parent().addClass("this");
				}
			}
		});
		//鼠标经过左侧分类							
		$(".left_menu_dl").mouseover(function() {
			var child_count = $(this).attr("child_count");
			if (child_count > 0) {
				var id = $(this).attr("id");
				$("#dts_" + id).addClass("left_menu_this").removeClass("left_menu_dt");
				$("#child_" + id).show();
			}
			var gc_id = $(this).attr("id");
			var gc_color = $(this).attr("gc_color");
			$("#dts_" + gc_id).attr("style", "border:1px solid " + gc_color + "; border-left:3px solid " + gc_color + ";border-right:none");
			$("#left_menu_con_" + gc_id).attr("style", "border:1px solid " + gc_color + "; border-left:1px solid " + gc_color + ";").find(".menu_con_right_top").css("background-color", gc_color); //设置div高度为每行div中最高的高度
			var begin = 0;
			var end = 2;
			$("#child_" + gc_id).find(".left_menu_con_center_left").each(function() {
				var max_height = 0;
				var index = $(this).index();
				var height = $(this).height();
				if (index > end || index < begin) {
					begin = begin + 3;
					end = end + 3;
				}
				if (height > max_height) {
					max_height = height;
				}
				for (var i = begin; i <= end; i++) {
					var temp_height = $("#child_" + gc_id).find(".left_menu_con_center_left").eq(i).height();
					if (temp_height > max_height) {
						max_height = temp_height;
					}
					$("#child_" + gc_id).find(".left_menu_con_center_left").eq(i).height(max_height);
				}
			});
			var top = $("#child_" + gc_id).offset().top;
			var scroll_top = $(document).scrollTop();
			var height = $("#left_menu_con_" + gc_id).height();
			var all_h = top - scroll_top + height;
			var doc_h = $(window).height();
			var margin_top = doc_h - all_h;
			if (margin_top <= 5) {
				margin_top = margin_top - 20;
				$("#left_menu_con_" + gc_id).css('margin-top', margin_top + 'px');
			} else {
				$("#left_menu_con_" + gc_id).css('margin-top', '0px');
			}
		}).mouseleave(function() {
			$("dt[id^=dts_]").removeAttr("style");
			$("div[id^=left_menu_con_]").removeAttr("style");
			var child_count = $(this).attr("child_count");
			if (child_count > 0) {
				var id = $(this).attr("id");
				$("#dts_" + id).removeClass("left_menu_this").addClass("left_menu_dt");
				$("#child_" + id).hide();
			}
		});
		//
		$(".nav_left").mouseover(function() {
			$("#other_menu").show();
		});
		$(".nav_left").mouseleave(function() {
			$("#other_menu").hide();
		});

	}

	if ($(".main_bottom").length) {


		var tempLengths = 0; //临时变量,当前移动的长度
		var viewNums = 5; //设置每次显示图片的个数量
		var moveNums = 4; //每次移动的数量
		var moveTimes = 500; //移动速度,毫秒
		var scrollDivs = $(".bottom_rbox_position"); //进行移动动画的容器
		var scrollItemss = $(".bottom_rbox_position ul"); //移动容器里的集合
		var moveLengths = scrollItemss.eq(0).width() * moveNums; //计算每次移动的长度
		var countLengths = (scrollItemss.length - viewNums) * scrollItemss.eq(0).width(); //计算总长度,总个数*单个长度
		var all_lengths = scrollItemss.eq(0).width() * scrollItemss.length;
		$(".bottom_rbox_position").css("width", all_lengths);
		//下一张
		$("#left").bind("click", function() {
			if (tempLengths < countLengths) {
				if ((countLengths - tempLengths) > moveLengths) {
					scrollDivs.animate({
						left: "-=" + moveLengths + "px"
					}, moveTimes);
					tempLengths += moveLengths;
				} else {
					scrollDivs.animate({
						left: "-=" + (countLengths - tempLengths) + "px"
					}, moveTimes);
					tempLengths += (countLengths - tempLengths);
				}
			}
		});
		//上一张
		$("#right").bind("click", function() {
			if (tempLengths > 0) {
				if (tempLengths > moveLengths) {
					scrollDivs.animate({
						left: "+=" + moveLengths + "px"
					}, moveTimes);
					tempLengths -= moveLengths;
				} else {
					scrollDivs.animate({
						left: "+=" + tempLengths + "px"
					}, moveTimes);
					tempLengths = 0;
				}
			}
		});

	}

	//初始化分类搜索提示模块
	function search_goodsclass_initialize() {
		$("div[class^='head_search_goodslist']").each(function(index, obj) {
			$(obj).remove();
		});
		var str = "<div class='head_search_goodslist' id='head_search_goodslist' style='display:none'><dl><dt><ul></ul></dt></dl></div>";
		$("#head_search").append(str);
		str = "<div class='head_search_goodslist_up' id='head_search_goodslist_up' style='display:none'><dl><dt><ul></ul></dt></dl></div>";
		$("#head_search").append(str);
	}

	function search_gc_close() {
		$("div[id^='head_search_goodslist']").hide();
	}
	//加载搜索历史记录
	function search_history_data() {
		var data = $.cookie("search_history");
		if (data == null) {
			$("div[id^='head_search_history']").remove();
		} else {
			$.each(data.split(","), function(index, item) {
				if (index < 10) {
					$("#head_search_history_up").find("dl>dt>ul").append("<li><span data='" + item + "'><a href='javascript:void(0)'>" + item + "</a></span><i onclick='search_data_del(this)'><a href='javascript:void(0)'>删除</a></i></li>");
					$("#head_search_history").find("dl>dt>ul").append("<li><span data='" + item + "'><a href='javascript:void(0)'>" + item + "</a></span><i onclick='search_data_del(this)'><a href='javascript:void(0)'>删除</a></i></li>");
				}
			});
		}
	}
	//删除某一历史记录
	function search_data_del(obj) {
		var item = $(obj).parent().find("span").attr("data");
		var data = "";
		var temp = $.cookie("search_history");
		$.each(temp.split(","), function(index, value) {
			if (item != value) {
				if (data != "") {
					data = data + "," + value;
				} else {
					data = value;
				}
			}
		});

		if (data != "") {
			$.cookie("search_history", data);
		} else {
			$.cookie("search_history", null);
		}
		search_html_restore();
		search_history_data();
	}
	//删除全部历史记录
	function search_data_del_all() {
		$.cookie("search_history", null);
		search_html_restore();
		search_history_data();
	}
	//清空搜索历史下拉数据
	function search_html_restore() {
		$("#head_search_history_up").find("dl>dt>ul>li").each(function(index, element) {
			if (index != 0) {
				$(this).remove();
			}
		});
		$("#head_search_history").find("dl>dt>ul>li").each(function(index, element) {
			if (index != 0) {
				$(this).remove();
			}
		});
	}

	function search_form() {
		var keyword = arguments[0];
		var type = arguments[1];
		if (keyword != "" && keyword != undefined) {
			$("#keyword").val(keyword);
		}
		if (type != "" && type != undefined) {
			$("#type").val(type);
		}
		$("#searchForm").submit();
		$("#keyword").val("");
	}

	function menu_cart_remove(id) {
		$.post(webPath + "/remove_goods_cart.htm", {
			"ids": id
		}, function(data) {
			if (data.code == "100") {
				$(".goodscar_list li[id=" + id + "]").remove();
				$("#cart_goods_count_top").html(data.count);
				$("#st_count").html(data.count);
				$("#cart_goods_price_top").html(data.total_price);
				if (data.count == 0) { //购物车页面没有商品
					$(".goodscar_list").remove();
					$("#goodscar_con_box").html("<b class='goodscar_none'>购物车还没有商品!</b>");
				} else {
					$("#goodscar_con_box").load(webPath + "/cart_menu_detail.htm");
				}
			}
			if (data.code == "200") {
				layer.alert("系统繁忙，请稍后重试！");
			}

		}, "json");
	}
	return {
		search_data_del_all: search_data_del_all,
		search_form: search_form,
		menu_cart_remove: menu_cart_remove
	}

});

//星美统计
var _XmAnalysisData = _XmAnalysisData || [];
(function () {
	var XmAnalysis = document.createElement('script');
	XmAnalysis.type = 'text/javascript';
	XmAnalysis.async = true;
	XmAnalysis.id = 'XM-analysis';
	XmAnalysis.src = 'http://log.smi170.com/smilogger/js/XmAnalysisPcWeb.min.js?SmiLife';
	var s = document.getElementsByTagName('script')[0];
	s.parentNode.insertBefore(XmAnalysis, s);

	/**
	 * cookie操作
	 * @param {String} name     必填，字段名，当参数仅有一个name时，为读取cookie
	 * @param {String} value    选填，字段值，当value值为null时为删除cookie
	 * @param {Object} options  选填，cookie详细设置：
	 * {Number|Date}   expires     有效期(number类型:小时，Date类型：有效期结束时刻毫秒单位)，缺省：不设置
	 * {String}        domain      有效域，缺省：当前域
	 * {String}        path        有效目录，缺省：当前目录
	 * {Boolean}       secure      secure值为true时，在http模式中不会向服务回发Cookie的验证信息；在https模式中会认为是安全的，会回发数据。
	 */
	var cookie=function (name, value, options) {
		//console.log(value !== undefined)
		if (value !== undefined) {
			options = options || {};
			if (value === null) {
				value = '';
				//options = $.extend({}, options);
				options.expires = -1;
			}
			var expires = '';
			if (options.expires && ( typeof options.expires == 'number' || options.expires.toUTCString)) {
				var date;
				if (typeof options.expires == 'number') {
					date = new Date();
					date.setTime(date.getTime() + (options.expires * 60 * 60 * 1000));
				} else {
					date = options.expires;
				}
				expires = '; expires=' + date.toUTCString();
			}
			var path = options.path ? '; path=' + (options.path) : '';
			var domain = options.domain ? '; domain=' + (options.domain) : '';
			var secure = options.secure ? '; secure' : '';
			document.cookie = [name, '=', encodeURIComponent(value), expires, path, domain, secure].join('');
			//document.cookie = [name, '=', (value), expires, path, domain, secure].join('');
		} else {

			var cookieValue = null;
			if (document.cookie && document.cookie != '') {
				var cookies = document.cookie.split(';');
				for (var i = 0; i < cookies.length; i++) {
					var cookie = cookies[i].replace(/(^\s*)|(\s*$)/g, '');
					if (cookie.substring(0, name.length + 1) == (name) + '=') {

						cookieValue = decodeURIComponent(cookie.substring(name.length + 1));
						break;
					}
				}
			}
			return cookieValue;
		}
	};

	var userId =cookie("uid");
	if(userId&&userId !=null){//保存用户userid
		_XmAnalysisData.push(['_setUserSignIn','app',userId,2]);
	}else{
		_XmAnalysisData.push(['_setUserSignOff']);
	}
})();