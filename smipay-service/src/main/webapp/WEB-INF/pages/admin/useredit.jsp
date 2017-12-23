<%@ page language="java" contentType="text/html; charset=UTF-8"
  pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link href="<%=request.getContextPath()%>/css/base.css" rel="stylesheet" type="text/css" />
<link href="<%=request.getContextPath()%>/css/admin.css" rel="stylesheet" type="text/css" />
<link href="<%=request.getContextPath()%>/css/admin.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.min.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.tools.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.validate.js"></script> 

</head>
<DIV class=body style="text-align:center">
<div style="background-color:#363636;height:50px;color:#fff;font-size:20px;line-height:50px;margin-bottom:0px">
 星美支付平台
</div>

<div style="width:90%;line-height:50px;font-size:14px;text-align:left;margin-left: auto;margin-right: auto">
 
  <a href="orderquery.do">付款记录查询</a>&nbsp;&nbsp; &nbsp;&nbsp; 
  <a href="refundquery.do"  >退款记录查询</a>&nbsp;&nbsp; &nbsp;&nbsp; 
  <a href="orderlogquery.do"  >下单日志查询</a>&nbsp;&nbsp; &nbsp;&nbsp;
  <a href="refundlogquery.do">退款日志查询</a>&nbsp;&nbsp; &nbsp;&nbsp; 
   <c:if test="${role eq '0'}">
  <a href="callback.do" >回调地址管理</a>&nbsp;&nbsp; &nbsp;&nbsp;
  <a href="userlist.do"  style="color:red">用户管理</a>&nbsp;&nbsp; &nbsp;&nbsp;
  </c:if>
   <a href="../login.jsp" style="float:right;text-decoration:underline;padding-right:30px">退出</a>
</div>
</DIV>
<BODY class="input levels" sizcache="0" sizset="0" >
<DIV class=bar style="padding-left:200px;width:90%">修改用户信息</DIV>
<DIV class=body sizcache="0" sizset="0">
<div id="validateErrorContainer" class="validateErrorContainer">
		<div class="validateErrorTitle">以下信息填写有误,请重新填写</div>
		<ul></ul>
</div>
<FORM id=userForm class="tabContent" encType=multipart/form-data method=post action="usersave.do" sizcache="0" sizset="0" >

<div style="padding-left:20%;padding-right:20%" class="tabContent">
<TABLE  id=infoTable class="inputTable" sizcache="0" sizset="0">
<TBODY sizcache="0" sizset="0">
<TR>
<TH>账号 </TH><input type="hidden" name="id" value="${info.id}">
<TD> <input class=formText type="text" name="username" id="username"  value="${info.username}"><LABEL class=requireField>*</LABEL></TD> </TR>

<TR>
<TH>密码 </TH>
<TD> <input type="password" name="password" value="${info.password}"><LABEL class=requireField>*</LABEL></TD> </TR>


<TR>
<TH>姓名</TH>
<TD><INPUT class=formText type=text name="realname" id="realname" value="${info.realname}"><LABEL class=requireField>*</LABEL>  </TD></TR>

<TR>
<TH>手机</TH>
<TD><INPUT class=formText type=text name="mobile" id=mobile value="${info.mobile}">  </TD></TR>
 
 
<TR>
<TH>状态</TH>
<TD><SELECT name="status" id=status >  
       <OPTION value="0" ${info.status=='0'?'selected':''}>正常</OPTION> 
       <OPTION value="1" ${info.status=='1'?'selected':''}>禁用</OPTION>
</SELECT> </TR>

<TR>
<TH>权限 </TH>
<TD><SELECT name="role" id=role >  
       <OPTION value="1" ${info.role=='1'?'selected':''}>查询用户</OPTION>
       <OPTION value="0" ${info.role=='0'?'selected':''}>管理员</OPTION> 
       
</SELECT> </TR>

</TBODY></TABLE>

<DIV class=buttonArea>
<INPUT hideFocus class=formButton value="确  定" type=submit >&nbsp;&nbsp; 
<INPUT hideFocus class=formButton onclick="window.history.back(); return false;" value="返  回" type=button> 
</DIV>

</div>

</FORM>


</BODY>
<script type="text/javascript">
<!--
$().ready( function() {
	var $userForm = $("#userForm");
	// 注册页面表单验证
	$userForm.validate({	 
	    errorClass: "validateError",   
		rules:{
			  username:{
			  required: true,
			  remote:{
                url: "<%=request.getContextPath()%>/admin/checkUserName.do?id="+'${info.id}',     //后台处理程序 
                type: "post",               //数据发送方式
                data: {userName: function() {return $("#username").val();}}
               }					 
			 },
			realname: "required",
			password: "required"
		},
		messages: {
			username: {
             required: "请输入用户名!",
             remote:"用户名重复，请重新输入!"
            },
            realname: "请输入姓名!",
            password: "请输入密码!"
		},
		submitHandler: function(form) {			   
			$(form).find(":submit").attr("disabled", true);
			form.submit();
		},
        errorPlacement: function(error, element) { 
            error.appendTo(element.parent()); 
        }
		
	});	
});
-->

</script>
</html>