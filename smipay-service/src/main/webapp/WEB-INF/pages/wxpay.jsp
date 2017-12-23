<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<html>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<style>
  .input {
    text-align:center;
    height:40px;
	font-size:40px;
}
 input{
   
	font-size:40px;
}
</style>
 <body>
 
 

<p style="text-align:center;font-size:50px;" >支付测试openid=${openid}</p>
 <form action="<%request.getContextPath();%>/smipay/wxpay.do">
 <div style="text-align:center;">
	<div class="input">测试用户ID：<input type="text" name="userId" value="test001"  readonly size="20"/></div>
	<br><br><br>
	<div style="text-align:center;">
	<div class="input">openid：<input type="text" name="openid" value="${openid}"  readonly size="20"/></div>
	<br><br><br>
	<div style="text-align:center;">
	<div class="input">输入订单号：<input type="text" name="orderNo" value="smi0001" size="20"/></div>
	<br><br><br>
	<div class="input">请输入金额：<input type="text" name="money" value="1" size="20"/></div>
	<br><br><br>
	<input type="submit"   value="提交"  style="width:200px;height:50px;font-size:40px"   />
	</div>
	</form>
</body>
</html>
