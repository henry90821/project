<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<!DOCTYPE html>
<html>
<style>
.title{color:red;font-size:30px;line-height:50px;}

.info{font-size:20px;line-height:40px;}

</style>
	
	<body style="text-align:center">
		           <div class="title">
				   <c:if test="${order.status=='0'}">
					恭喜您，支付成功
				   </c:if>
				    <c:if test="${order.status!='0'}">
					订单支付失败，原因：${order.callBackMemo}
				   </c:if>
				  </div>
			 
				<div class="info">
						您的订单号:
					 
						<strong>${order.billNo}</strong>
				</div>
				 <div class="info">
						您的账号:
					 
						<strong>${order.custId}</strong>
				</div>
				<div class="info">
						您的支付金额:
					 
						<strong>￥${order.totalFee/100}</strong>
				</div>
		<div style="text-align:center;font-size:20px;padding-top:20px">
		<a href="${order.returnUrl}" ><span id="time">5</span>秒后，将自动跳转</a>
		</div>
	</body>
<script>
    var timer1=setInterval(djs,1000);
    var i = 5;
    function djs(){
	    document.getElementById('time').innerHTML=i;
	    if(i>0)
	    {
	    	i--;
	    }
	    else
	    {
	        clearInterval(timer1);
	    	window.location.href="${order.returnUrl}"; 
	    }
	    
}
   </script>
</html>


