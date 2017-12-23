/**
 * baseUrl 配置JS目录
 * path ： 配置路径
 * shim ： 配置不支持AMD模块的插件 
 */



/**
 * 引用jquery插件
 */
require(['jquery', 'lazyload', 'superslide'],function($){
	
	$.get(Global.webPath + "/advert_invoke.htm?id=10", function(x){
    		$("#ad1").html(x)
    	});
    	$.get(Global.webPath + "/advert_invoke.htm?id=1", function(x){
    		$("#rotary").html(x);
    		$('.top_mid_slide').slide({mainCell:'.top_mid_slider_ul',titCell:'.banner_top li',titOnClassName:'this',trigger:'mouseover',autoPlay:true});
    	});
    	$.get(Global.webPath + "/advert_invoke.htm?id=8", function(x){
    		$("#ad2").html(x)
    	});
        $(document).ready(function () {
        	
        	var amounts = $("#amountAllowed").val().split(",");
        	$.each(amounts, function(i,val){
        		if(i == 0) {
        			$("#rc_amount").append("<option value='" + val + "' selected='selected'>" + val + "元</option>");
        		} else {
        			$("#rc_amount").append("<option value='" + val + "'>" + val + "元</option>");
        		}        		
        	});
        	
        	// 弹窗关闭
        	
        	$(".black-cover-colse").click(function(){
        		$(this).parents(".black-msg-content").hide();
        		$(".black-cover").hide();
        	});
        	
        	// 设置30秒自动关闭弹窗
        	
        	setTimeout(function(){
        		$(".black-msg-content").hide();
        		$(".black-cover").hide();
        	},30000);
        	
        	
            //鼠标经过左侧分类
            $(".sale_two_img img").lazyload({ effect: "fadeIn"});
            $(".left_menu_dl").mouseenter(function () {
                var child_count = $(this).attr("child_count");
                if (child_count > 0) {
                    var id = $(this).attr("id");
                    $("#dts_" + id).addClass("left_menu_this").removeClass("left_menu_dt");
                    $("#child_" + id).show();
                }
                var gc_id = $(this).attr("id");
                var gc_color = $(this).attr("gc_color");
                $("#dts_" + gc_id).attr("style", "border:1px solid " + gc_color + "; border-left:3px solid " + gc_color + ";border-right:none");
                $("#left_menu_con_" + gc_id).attr("style", "border:1px solid " + gc_color + "; border-left:1px solid " + gc_color + ";").find(".menu_con_right_top").css("background-color", gc_color);
                //设置div高度为每行div中最高的高度
                var begin = 0;
                var end = 2;
                $("#child_" + gc_id).find(".left_menu_con_center_left").each(function () {
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
            }).mouseleave(function () {
                $("dt[id^=dts_]").removeAttr("style");
                $("div[id^=left_menu_con_]").removeAttr("style");
                var child_count = $(this).attr("child_count");
                if (child_count > 0) {
                    var id = $(this).attr("id");
                    $("#dts_" + id).removeClass("left_menu_this").addClass("left_menu_dt");
                    $("#child_" + id).hide();
                }
            });
            //鼠标经过推荐商品行
            $("#index_sale_tab ul li").mouseenter(function () {
                $(this).siblings().removeClass("this");
                $(this).addClass("this");
                var i = $(this).index();
                $("#index_sale_tab").siblings().hide();
                $("#index_sale_tab").siblings().eq(i).show();
                $("#sale_change").attr("mark", $(this).attr("id").replace("goodscase", ""));
            });
            
            var head_h = $("#head_h").height();
            $("#head_unbomb").height(head_h);
            $(window).scroll(function () {
                var top = $(document).scrollTop();
                //顶部搜索框跟随
                if (top == 0) {
                    $("#top").attr("style", "");
                } else {
                    $("#top").attr("style", "position:fixed;top:0px");
                }
                if (top > head_h) {
                    $("#head_h").addClass("head_fixd");
                } else {
                    $("#head_h").removeClass("head_fixd");
                }
                //楼层导航跟随
                $("li[floor_id^=floor_] b").css("display", "block");
                $("div[id^=floor_]").each(function () {
                    var floor_top = $(this).offset().top - top;
                    if (floor_top <= 580 && floor_top >= 0) {
                        var floor_id = $(this).attr("id");
                        $("li[floor_id=" + floor_id + "] b").css("display", "none");
                    }
                });
            });
            //右上角公告切换
            $(".top_mr_tab li").mouseenter(function () {
                $(this).addClass("this").siblings().removeClass("this");
                var i = $(this).index();
                $(".top_mr_box").hide().eq(i).show();
            });

            
            var shopone = $(".pinkfooter .shopone");
            if(shopone.length > 6){
            	shopone.filter(":gt(5)").remove();
            }
            
        });
        
        $(".phone_btn").click(function () {
            var mobile = $("#mobile");
            var label = mobile.next();
            if (!mobile.val()) {
                label.html("会员手机号不能为空").show();
                return false;
            }
            var flag = true;
            $.ajax({
            	type: 'GET',
            	url: Global.webPath + '/verify_username2.htm', 
            	async: false,
            	data: {"mobile": mobile.val()},
            	success: function(data) {
            		if(data == "false") {
            			flag = false;
            			label.html("手机号对应的会员账号不存在").show();
            		}
            	}
            });
            return flag;
        });



        var goods_random = 1;//随机次数
        function change_case_goods() {
            var caseid = $("#sale_change").attr("mark");
            goods_random = parseInt($(".index_sale_tab li[class='this']").attr("goods_random"));
            $.ajax({
                type: 'POST', url: Global.webPath + '/switch_case_goods.htm', data: { "goods_random": goods_random, "caseid": caseid },
                beforeSend: function () {
                    $("#index_sale_con_" + caseid).empty();
                    $("#index_sale_box_" + caseid).html("<div style='width:100%;height:301px;text-align:center;'><img src='"+ Global.webPath +"/resources/style/common/images/loader.gif' style='margin-top:145px;' /></div>");
                },
                success: function (html) {
                    $("#index_sale_box_" + caseid).html(html);
                    goods_random++;
                    $(".index_sale_tab li[class='this']").attr("goods_random", goods_random);
                }
            });
        }
        $("#sale_change").click(change_case_goods);

        
        $(".storey_list li").mouseenter(function () {
            $(".storey_list li").stop();
            $(this).parent().parent().find("li").animate({ "opacity": "0.2" }, { queue: false, duration: 1000 });
            $(this).animate({ "opacity": "0.9" }, { queue: false, duration: 1000 });
        }).mouseleave(function () {
            $(".storey_list li").stop();
            $(this).animate({ "opacity": "0.9" }, { queue: false, duration: 1000 });
        });
        $(".storey_list").mouseleave(function () {
            $(".storey_list li").stop();
            $(this).parent().parent().find("li").css("opacity", "1.0");
        });
        $(".hot_sell_ul li").mouseenter(function () {
            $(".hot_sell_ul li").stop();
            $(this).parent().find("li").animate({ "opacity": "0.2" }, { queue: false, duration: 1000 });
            $(this).animate({ "opacity": "0.9" }, { queue: false, duration: 1000 });
        }).mouseleave(function () {
            $(".hot_sell_ul li").stop();
            $(this).animate({ "opacity": "1.0" }, { queue: false, duration: 1000 });
        });
        $(".hot_sell_ul").mouseleave(function () {
            $(".hot_sell_ul li").stop();
            $(this).parent().find("li").css("opacity", "1.0");
        });
        //
        $(".storey_show_2_right li").mouseenter(function (event) {
            $(".storey_show_2_right li").stop();
            $(".storey_show_2_left").stop();
            $(".storey_show_2_right li").animate({ "opacity": "0.2" }, { queue: false, duration: 1000 });
            $(".storey_show_2_left").animate({ "opacity": "0.2" }, { queue: false, duration: 1000 });
            $(this).animate({ "opacity": "0.9" }, { queue: false, duration: 1000 });
            $(this).find(".storey_show_on").show();
        }).mouseleave(function () {
            $(".storey_show_2_right li").stop();
            $(".storey_show_2_left").stop();
            $(this).animate({ "opacity": "1.0" }, { queue: false, duration: 1000 });
            $(this).find(".storey_show_on").hide();
        });
        $(".storey_show_2_right").mouseleave(function () {
            $(".storey_show_2_right li").stop();
            $(".storey_show_2_left").stop();
            $(".storey_show_2_right li").css("opacity", "1.0");
            $(".storey_show_2_left").css("opacity", "1.0");
            $(this).find(".storey_show_on").hide();
        });
        //
        $(".storey_show_2_left").mouseenter(function () {
            $(".storey_show_2_right li").stop();
            $(".storey_show_2_left").stop();
            $(this).css("opacity", "0.2");
            $(".storey_show_2_right li").animate({ "opacity": "0.2" }, { queue: false, duration: 1000 });
            $(this).animate({ "opacity": "0.9" }, { queue: false, duration: 1000 });
            $(this).find(".storey_show_on").show();
        }).mouseleave(function () {
            $(".storey_show_2_right li").stop();
            $(".storey_show_2_left").stop();
            $(".storey_show_2_right li").css("opacity", "1.0");
            $(this).css("opacity", "1.0");
            $(this).find(".storey_show_on").hide();
        });
        //
        
        //back_floor
		$(".back_floor li").click(function() {
			var id = $(this).attr("floor_id");
			var top = $("#" + id).offset().top - 80;
			$('body,html').animate({
				scrollTop: top
			}, 1000);
		});
		
		(function() {
			var index = 1;
	
			function render(x) {
				x = x[0];
				var i, j, len, len2, cls = "",
					dis, o;
				var html = '<div class="storey ' + x.gf_css + '" id="floor_' + x.fnum + '">';
				html += '<div class="storey_left">';
				html += '<h1><b>' + x.fnum + 'F</b><span>' + x.gf_name + '</span></h1>';
				html += '<div class="storey_bd">';
				html += '<div class="storey_ul">';
				html += '<ul>';
				for (i = 0, len = x.gcs.length; i < len; i++) {
					html += '<li><a href="' + x.gcs[i].goodslist_url + '" target="_blank">' + x.gcs[i].gcname + '</a></li>';
				}
				html += '</ul></div>';
				html += '<div class="advert">' + x.gf_left_adv + '</div></div></div>';
	
				html += '<div class="storey_right">';
				html += '<div class="storey_tab"><ul>';
				for (i = 0, len = x.children.length; i < len; i++) {
					cls = i == 0 ? "this" : "";
					html += '<li class="' + cls + '" style="cursor:pointer;" id="' + x.children[i].id + '" store_gc="' + x.id + '"><a href="javascript:void(0);">' + x.children[i].gf_name + '</a><s></s></li>';
				}
				html += '</ul></div>';
				for (i = 0, len = x.children.length; i < len; i++) {
					dis = i == 0 ? "" : "display:none";
					if(x.children[i].style == "style1"){
						html += '<div class="storey_list" style="' + dis + '">';
						html += '<ul>';
						for (j = 0, len2 = x.children[i].goods.length; j < len2; j++) {
							o = x.children[i].goods[j];
							html += '<li>';
							html += '<div class="bd_right">';
							html += '<div class="storey_ps">';
							html += '<div class="storey_hover"> <span class="goods_name"><a href="' + o.goods_url + '" target="_blank">' + o.goods_name + '</a></span> <span class="goods_price">¥<b>' + o.goods_current_price + '</b><em>¥' + o.goods_price + '</em></span> <span class="goods_pic"><a href="' + o.goods_url + '" target="_blank"><span class="img_cspan">';
							html += '<p><img style="display: inline;" src="" data-original="' + o.img + '" onerror="" height="170" width="170"></p>';
							html += '</span></a></span> </div></div></div></li>';
						}
						html += '</ul></div>';
					} else if (x.children[i].style == "style2") {
						html += '<div class="storey_show_2" style="' + dis + '">';
						o = x.children[i].goods[0];
						html += '<div class="storey_show_2_left storey_style2">';
						html += '<a href="' + o.href_url + '" target="_blank"><img src="" data-original="' + o.img_url + '" onerror=""></a>';
						if (o.goods_name && o.goods_price) {
							html += '<div class="storey_show_on" style="display:none;">';
							html += '<div class="storey_show_2_right_word"><a href="' + o.href_url + '" target="_blank">' + o.goods_name + '</a></div>';
							html += '<div class="storey_show_2_right_price">¥' + o.store_price + '</div>';
							html += '</div>';
						}
						html += '</div>';
						html += '<div class="storey_show_2_right">';
						html += '<ul>';
						for (j = 1, len2 = x.children[i].goods.length; j < len2; j++) {
							o = x.children[i].goods[j];
							html += '<li>';
							html += '<div class="storey_show_2_right_img  storey_style2">';
							html += '<a href="' + o.href_url + '" target="_blank"> <img src="" data-original="' + o.img_url + '" height="137" width="137"></a>';
							if (o.goods_name && o.goods_price) {
								html += '<div class="storey_show_on" style="display:none;">';
								html += '<div class="storey_show_2_right_word"><a href="' + o.href_url + '" target="_blank">' + o.goods_name + '</a></div>';
								html += '<div class="storey_show_2_right_price">¥' + o.store_price + '</div>';
								html += '</div>';
							}
							html += '</li>';
						}
						html += '</ul></div></div>';
					}
				}
				html +='<div class="hot_sell">';
				html += '<div class="hot_sell_br"><h2>' + x.hot.title + '</h2>';
				html += '<ul class="hot_sell_ul">';
				for (i = 0, len = x.hot.goods.length; i < len; i++) {
					o = x.hot.goods[i];
					html += '<li> <em class="number_01"></em> <span class="hot_goods_pic"><a href="' + o.goods_url + '" target="_blank"><span class="img_cspan">';
					html += '<p><img src="' + o.img + '" height="80" width="80"></p>';
					html += '</span></a></span>';
					html += '<div class="hot_describe">';
					html += '<p class="hot_goods_promo">已售' + o.goods_salenum + '件</p>';
					html += '<p class="hot_goods_name"><a href="' + o.goods_url + '" target="_blank">' + o.goods_name + '</a></p>';
					html += '<p class="hot_goods_price">¥<b>' + o.goods_current_price + '</b></p>';
					html += '</div></li>';
				}
				html += '</ul></div></div></div></div>';
				var dom = $(html);
				
				$(".storey_right img", dom).lazyload();
				
	            $("#contain_floor").append(dom);
			}
			
			function requestData(index){
				$.ajax({
					type:"get",
					url:Global.webPath + "/floor_data.htm?fnum=" + index,
					dataType : "json",
					success : function(x){
						if(!x.length){return;}
						index++;
						requestData(index);
						render(x);
					}
				});
			}
			requestData(index);
			$("#contain_floor").on("mouseenter", ".storey_style2", function(){
				if($(this.lastChild).hasClass("storey_show_on")){
					this.lastChild.style.display = "block";
				}
			}).on("mouseleave", ".storey_style2", function(){
				if($(this.lastChild).hasClass("storey_show_on")){
					this.lastChild.style.display = "none";
				}
			}).on("mouseenter", ".storey_tab li", function(){
                $(this).addClass("this").siblings().removeClass("this");
                var i = $(this).index();
                $(this).parent().parent().siblings().not(".hot_sell").hide();
                $(this).parent().parent().siblings().eq(i).show();
			});
		})();

    $(function(){

        //焦点广告统计
        $('#rotary').on('click','.top_mid_slider_ul_li a',function(){
            var selfId =$(this).data('aid');
            _XmAnalysisData.push(['_trackEvent', 'ad', 'adClick', 'adId',selfId]);
        });
    })
	
});


