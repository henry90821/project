<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<html>
  <head>
    <title>支付订单</title>
    <link href="<%=request.getContextPath()%>/base.css" rel="stylesheet" type="text/css" />
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.min.js"></script>
  </head>
<body >
   <div id="center" style="width:80%;text-align:left">
     <div class="head">
       星美支付平台
     </div>
     
     <div style="line-height:50px;text-align:left">商品信息</div>
     <div class="orderinfo" >
        <div class="tabhead">
            <span  >商品描述</span>
            <span  >订单编号</span>
            <span  >合计金额</span>
        </div>
        <div class="tabline">
            <span>${order.title}</span>
            <span>${order.billNo}</span>
            <span>￥${order.totalFee/100} </span>
        </div>
     </div>
     
     
    
     <div class="payinfo" style="height:410px" >
     
       <form name="payForm" action="<%=request.getContextPath()%>/topay.do" method="post">
        <c:if test="${order.billType!='2'}">
        <div style="color:#aaa">
           <span>  选择支付方式 </span>
        </div>
       
        <div style="border-bottom: #dedede 0px solid; ">
          <span > 
          <input type="radio" name="payType" value="1"
           <c:if test="${xminfo['totalAll']< order.totalFee/100}"> disabled </c:if>
           <c:if test="${xminfo['totalAll']> order.totalFee/100}"> checked="checked"  </c:if>  
          />
          账户余额 ￥${xminfo['totalBal']}
          <c:if test="${xminfo['xingCoin']!='0'}">（可用${xminfo['xingCoin']} 星币抵￥${xminfo['coinBal']}）</c:if>
          
           <c:if test="${order.billType!='1'}">
          <input type="radio" name="payType" value="2" style="margin-left:50px"  <c:if test="${xminfo['totalYp']!='0' && order.billType=='3'}">checked="checked" </c:if>    />电影票 (${xminfo['totalYp']}张)
          </c:if>
           </span>
        </div>
        <div style="color:#aaa;">
		  <span>
          支付密码: 
		  <input type="password" name="password" id="password" style="width:200px;height:30px;margin-left:10px"/>
		  </span>
        </div>
        </c:if> 
         <div style="color:#aaa">
             <span> 支付平台 </span> 
        </div>
        <div style="border-bottom: #dedede 0px solid; ">
         <span>
          <input type="radio" name="payType" value="3" /><img src="images/p1.png" >
          <input type="radio" name="payType" value="4" /><img src="images/p2.png" > 
          <input type="radio" name="payType" value="5" /><img src="images/p3.png" > 
           </span>
        </div>
        
        <div style="color:#aaa;margin-top:10px;padding-bottom:10px">
             <span style="line-height:30px"> 支付网银 </span> 
             <span  style="background:#FFEE99;padding-left:10px;padding-right:10px;margin-left:30px;line-height:30px">支持多家银行储蓄卡、信用卡，开通快捷支付，一步轻松付款</span>
        </div>
         <div style="border-bottom: #dedede 0px solid; ">
         <span>
            <img src="images/p4.png" > 
           </span>
        </div>
     </div>
     
      <input type="hidden" name="order_id" value="${order.id}">
      <input type="hidden" name="channel" value="WEB">
      <image src="images/u40.png" onclick="pay()" /> 
     </form>
     
    
 
   </div>
     <script type="text/javascript">
     function pay()
	  {
    	 var payType = $('input[name="payType"]:checked ').val();
 		if(payType=='1'||payType=='2')
 		{
 		   var pd=document.getElementById("password").value;
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
 		else if(payType=='3'||payType=='4'||payType=='5')//支付宝
 		{
 			document.payForm.submit();
 		}
 		else
 		{
 			alert('请选择支付方式!');
 		}
	  }
     
     </script>
</body>
</html>