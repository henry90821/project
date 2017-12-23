<%@ page language="java" contentType="text/html; charset=UTF-8"
  pageEncoding="UTF-8"%>
<html>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<style type="text/css">
  
</style>

 
<body>

 <form  action="<%=request.getContextPath()%>/testbill.do" method="post" name="payForm" id="payForm" target="_blank" >
 <div class="element" style="margin-top:60px;">
                <div class="legend">支付平台下单接口测试 </div><br><br>
            </div>
	<div style="text-align:left;">
	<div class="input">appcode： 
	   <select name="appcode" >
	      <option value="10003">爱星美</option>
	      <option value="10004">商城</option>
	   </select>
	</div>
	
	<div class="input">channel： 
	   <select name="channel" >
	      <option value="WEB">WEB</option>
	      <option value="WAP">WAP</option>
	      <option value="WX">WX</option>
	      <option value="APP">APP</option>
	   
	   </select>
	</div>
	<br>
	
	<div class="input">payType：
      <select name="payType" >
          <option value=""></option>
	      <option value="WX">WX</option>
	      <option value="ALI">ALI</option>
	      <option value="YE">YE</option>
	      <option value="YP">YP</option>
	   
	   </select>
   </div>
	<br>
	
	<div class="input">payPwd：<input type="text" name="payPwd" value="" size="20"/>payType为YE或YP时需填写</div>
	<br>
	
	<div class="input">custId：<input type="text" name="custId" value="3131000000394858" size="20"/></div>
	<br>
	
	<div class="input">billNo：<input type="text" name="billNo" id="billNo" value="" size="20"/></div>
	<br>
	
	<div class="input">billType：
      <select name="billType" > 
	      <option value="1">星美汇购物订单</option>
	      <option value="2">星美汇充值订单</option>
	      <option value="3">爱星美购票订单</option> 
	   
	   </select>
   </div>
	<br>
	
	<div class="input">totalFee：<input type="text" name="totalFee" value="1" size="20"/></div>
	<br>
	
	
	
	<div class="input">title：<input type="text" name="title" value="星美测试订单" size="20"/></div>
	<br>
	
	<div class="input">commodity：
	<textarea  name="commodity" style="width:500px;height:100px"></textarea> </div>
	<br>
	
	<div class="input">returnUrl：<input type="text" name="returnUrl" value="http://www.baidu.com" size="20"/></div>
	<br>
	
	<div class="input">openId：<input type="text" name="openId" value="" size="20"/></div>
	<br>
	<div class="input">expDate：<input type="text" name="expDate" id="expDate" value="" size="20"/></div>
	<br>
	
	 <input type="hidden" name="reqno" value="test10001" size="20"/></div>
	 <input type="hidden" name="sign" value="testsign" size="20"/></div>
	<input type="submit"   value="提交"  style="width:200px;height:50px;font-size:40px"   />
	</div>
</form>  




 <form  action="<%=request.getContextPath()%>/testrefund.do" method="post" name="refundForm" id="refundForm" target="_blank" >
 <div class="element" style="margin-top:60px;">
                <div class="legend">退款接口测试 </div><br><br>
            </div>
	
	<div class="input">custId：<input type="text" name="custId" value="3131000000394858" size="20"/></div>
	<br>
	
	<div class="input">refundFee：<input type="text" name="refundFee" value="1" size="20"/></div>
	<br>
	
	<div class="input">refundNo：<input type="text" name="refundNo" id="billNo" value="" size="20"/></div>
	<br>
	
	<div class="input">billNo：<input type="text" name="billNo" id="billNo" value="" size="20"/></div>
	<br>
	 
	 
	
	<div class="input">commodity：<textarea  name="commodity" style="width:500px;height:100px"></textarea></div>
	<br>
	
	 
	 <input type="hidden" name="appcode" value="10001" size="20"/></div>
	 <input type="hidden" name="reqno" value="test10001" size="20"/></div>
	 <input type="hidden" name="sign" value="testsign" size="20"/></div>
	<input type="submit"   value="提交"  style="width:200px;height:50px;font-size:40px"   />
	</div>
</form>  





</body>

<script language="javascript">
	function GetDateNow() {
		var vNow = new Date();
		var sNow = "";
		sNow += String(vNow.getFullYear());
		sNow += String(vNow.getMonth() + 1);
		sNow += String(vNow.getDate());
		sNow += String(vNow.getHours());
		sNow += String(vNow.getMinutes());
		sNow += String(vNow.getSeconds());
		sNow += String(vNow.getMilliseconds());
		document.getElementById("billNo").value =  'xmh'+sNow;
		 
	}
	GetDateNow();
	
	var d = new Date();
	document.getElementById("expDate").value =  d.getTime();
</script>
</html>

