<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<html>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.min.js"></script>
<html>
  <head>
    <title>支付订单</title>
    <link href="<%=request.getContextPath()%>/base.css" rel="stylesheet" type="text/css" />
  </head>
<body >
   <div id="center" style="width:80%;text-align:left">
     <div class="head">
       星美支付平台
     </div>
     
     <div style="line-height:50px;text-align:left">商品信息</div>
     <div class="orderinfo" >
        <div class="tabhead">
            <span  >用户名</span>
            <span  >订单编号</span>
            <span  >合计金额</span>
        </div>
        <div class="tabline">
            <span>${order.custId}</span>
            <span>${order.billNo}</span>
            <span>￥${order.totalFee/100} </span>
        </div>
     </div>
     <div class="payinfo" style="height:500px">
        <div style="border-bottom: #dedede 0px solid; ">
           <span>  微信支付:请使用微信扫一扫，扫码支付 </span>
        </div>
        <div style="text-align:center;border-bottom: #dedede 0px solid;" >
        <img src="barcode/${fileName}" >
        <img src="images/phone-bg.png"
     </div>
  <!--    <div style="border-bottom: #dedede 0px solid; ">支付成功</div> -->
   </div>
</body>

<script>
	setInterval(checkstatus,1000);
    function checkstatus(){
    	$.get("<%=request.getContextPath()%>/checkOrder.do",{"order_id":${order.id}, "time": new Date().getTime()}, function(result){
    	      if(result=='0')
    	     {
    	    	  window.location.href="<%=request.getContextPath()%>/payresult.do?order_id=${order.id}";  
    	     }
    	  });
	    	//window.location.href="${order.returnUrl}";
	    
}
</script>
</html>