<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
response.setHeader("Cache-Control","no-cache");   
response.setHeader("Pragma","no-cache");   
response.setDateHeader("Expires",-1);   
%>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<meta name="viewport" content="width=device-width,initial-scale=1" />
		<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.min.js"></script>
		
		<title></title>

		<link rel="stylesheet" href="css/bootstrap.min.css">
		<link rel="stylesheet" href="css/style.css">
		
	</head>
	<body>
	<form name="payForm" action="<%=request.getContextPath()%>/topay.do" method="post" onsubmit="return check()"  >
		<div class="container-fluid">
			<div class="panel">
				<div class="panel-heading">
					<div class="h icon1" style="width:45%">
						<img src="images/wap_dg.png">
					</div>
					<div class="h tips">
						<p><strong>订单提交成功</strong></p>
						<p><strong>应付金额：</strong><span style="color: red">${order.totalFee/100}</span>元</p>
					</div>
					<div class="clearfix"></div>
				</div>
			 <c:if test="${order.billType=='3'}">
				<div class="panel-body">
					<div class="h icon2">
						<img src="images/wap_dbj.png">
					</div>
					<div class="h tips2">
						<p><strong>可用2D/3D电影通兑劵${xminfo['totalYp']}张</strong></p>
						<p><input type="password" placeholder="请输入账户支付密码" name="password2" id="password2" class="form-control" maxlength="6"></p>
					</div>
					<div class="h select1">
						<center><input name="payType" type="radio"  value="2" /></center>
					</div>
				</div>
				</c:if>
				<div class="panel-body">
					<p>请选择支付方式</p>
				</div>
				 <c:if test="${order.billType!='2'}">
				<div>
					<div class="panel-body">
					<div class="h icon2">
						<img src="images/wap_rmb.png">
					</div>
					<div class="h tips2">
						<p><strong>账户余额：</strong><span style="color: red">${xminfo['totalBal']}</span>元</p>
						  <c:if test="${xminfo['xingCoin']!='0'}">
						    <p>${xminfo['xingCoin']}星币可抵用${xminfo['coinBal']}元</p>
						</c:if>
						<p><input type="password" placeholder="请输入账户支付密码" name="password1" id="password1" class="form-control" maxlength="6"></p>
					</div>
					<div class="h select1">
						<center><input name="payType"   type="radio" value="1"  checked  /></center>
					</div>
				</div>
				</div>
				</c:if>
				<div id="alipay">
					<div class="panel-body" >
					<div class="h icon2">
						<img src="images/wap_zfb.png">
					</div>
					<div class="h tips2">
						<p><strong>支付宝</strong></p>
						<p>支持有支付宝网银的用户</p>
					</div>
					<div class="h select1">
						<center><input name="payType" type="radio"   value="3" /></center>
					</div>
				</div>
				</div>
				<div id="wxpay">
					<div class="panel-body">
					<div class="h icon2">
						<img src="images/wap_wx.png">
					</div>
					<div class="h tips2">
						<p><strong>微信支付</strong></p>
						<p>使用微信支付，方便快捷</p>
					</div>
					<div class="h select1">
						<center><input name="payType" type="radio"   value="4" checked="true"/></center>
					</div>
				</div>
				</div>
				<div class="panel-body">
					<button type="button" class="btn btn-info btn-block" onclick="checkorder()">确认支付</button>
				</div>
			<!-- <div> "appId" : "${orderReturn.appId}","timeStamp" : "${orderReturn.timestamp}", "nonceStr" : "${orderReturn.nonceStr}", "package" : "${orderReturn.packageValue}","signType" : "MD5", "paySign" : "${orderReturn.paySign}"</div> -->	
			</div>
		</div>
		
		
		 <input type="hidden" name="channel" value="${order.channel}">
		 <input type="hidden" name="order_id" value="${order.id}">
		 <input type="hidden" name="openId" value="${order.openId}"> 
		 <input type="hidden" name="password" id="password" value=""> 
		
	    </form>
	    
	    
	    <script type="text/javascript">
	function pay()
	{
		//var payType=document.getElementsByName("payType").value;
		var payType = $('input[name="payType"]:checked ').val();
		if(payType=='1')
		{
		   var pd=document.getElementById("password1").value;
		   if(pd=='')
		   {
		    	alert('请输入支付密码!'); 
		   }
		   else
	      {
			   document.getElementById("password").value=pd; 
			   document.payForm.submit();
	      }
		}
		else if( payType=='2')
		{
			//alert(payType);
			var pd=document.getElementById("password2").value;
			//alert(pd);
			   if(pd=='')
			   {
			    	alert('请输入支付密码!'); 
			   }
			   else
		      {
				   document.getElementById("password").value=pd; 
				   document.payForm.submit();
		      }
			
		}
		else if(payType=='3')//支付宝
		{
			document.payForm.submit();
		}
		else if(payType=='4')//微信
		{
			var openId=document.payForm.openId.value;		 
			var channel=document.payForm.channel.value;
			if(channel=='WX' && openId!='' && isWeiXin() )
			{
				wxpay();
			}
			else
			{
			  alert('请在微信中打开此链接!');
			  return false;
			}
		}
		else
		{
			alert('请选择支付方式!');
		}
	}
	
	function check()
	{
		var payType = $('input[name="payType"]:checked ').val();
		if(payType=='1')
		{
		   var pd=document.getElementById("password1").value;
		   if(pd=='')
		   {
		    	alert('请输入支付密码!'); 
		    	return false;
		   }
		   else
		  {
			   document.getElementById("password").value=pd; 
		  }
		}
		else if( payType=='2')
		{
			//alert(payType);
			var pd=document.getElementById("password2").value;
			//alert(pd);
			   if(pd=='')
			   {
			    	alert('请输入支付密码!'); 
			    	return false;
			   }
			   else
			   {
				   document.getElementById("password").value=pd;   
			}
		}
		else if(payType=='4')//微信
		{
			var openId=document.payForm.openId.value;		 
			var channel=document.payForm.channel.value;
			if(channel=='WX' && openId!='' && isWeiXin() )
			{
				wxpay();
				return false;
			}
			else
			{
			  alert('请在微信中打开此链接!');
			  return false;
			}
		}
		
	}
	
	
	function wxpay(){
		 WeixinJSBridge.invoke('getBrandWCPayRequest',{
 		 "appId" : "${orderReturn.appId}","timeStamp" : "${orderReturn.timestamp}", "nonceStr" : "${orderReturn.nonceStr}", "package" : "${orderReturn.packageValue}","signType" : "MD5", "paySign" : "${orderReturn.paySign}" 
  			},function(res){
				WeixinJSBridge.log(res.err_msg);
//				alert(res.err_code + res.err_desc + res.err_msg);
	            if(res.err_msg == "get_brand_wcpay_request:ok"){  
	                //alert("微信支付成功!");  
	            	window.location.href="${order.returnUrl}"; 
	            }else if(res.err_msg == "get_brand_wcpay_request:cancel"){  
	               // alert("用户取消支付!");  
	               
	            }else{  
	               alert(res.err_msg);  
	            }  
			})
		}
	
	function isWeiXin(){
	    var ua = window.navigator.userAgent.toLowerCase();
	    if(ua.match(/MicroMessenger/i) == 'micromessenger'){
	        return true;
	    }else{
	        return false;
	    }
	}
	
	if(isWeiXin())
	{
	 document.getElementById("alipay").style.display = "none";
	}
	
	function checkorder()
	{
	$.get("<%=request.getContextPath()%>/ordercheck.do?order_id=${order.id}", function(result){
	    if(result=='1')
	    {
	    	pay();
	    }
	    else if(result=='0')
	    {
	    	alert('订单已经支付成功，请不要重复支付！');
	    }
	    else if(result=='2')
	    {
	    	alert('订单已超期，请重新下单！');
	    }
	  });
	}
	</script>
	</body>
</html>
