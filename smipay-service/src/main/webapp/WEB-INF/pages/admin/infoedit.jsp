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

</head>
<DIV class=body style="text-align:center">
<div style="background-color:#363636;height:50px;color:#fff;font-size:20px;line-height:50px;margin-bottom:0px">
 星美支付平台
</div>
<div style="width:90%;line-height:50px;font-size:14px;text-align:left;margin-left: auto;margin-right: auto"">
 
  <a href="orderquery.do">付款记录查询</a>&nbsp;&nbsp; &nbsp;&nbsp; 
  <a href="refundquery.do"  >退款记录查询</a>&nbsp;&nbsp; &nbsp;&nbsp; 
  <a href="orderlogquery.do"  >下单日志查询</a>&nbsp;&nbsp; &nbsp;&nbsp;
  <a href="refundlogquery.do">退款日志查询</a>&nbsp;&nbsp; &nbsp;&nbsp; 
   <c:if test="${role eq '0'}">
  <a href="callback.do" style="color:red">回调地址管理</a>&nbsp;&nbsp; &nbsp;&nbsp;
  <a href="userlist.do"  >用户管理</a>&nbsp;&nbsp; &nbsp;&nbsp;
  </c:if>
   <a href="../login.jsp" style="float:right;text-decoration:underline;padding-right:30px">退出</a>
</div>
</DIV>
<BODY class="input levels" sizcache="0" sizset="0" >

<DIV class=bar style="padding-left:200px;width:90%">修改应用信息</DIV>
<DIV class=body sizcache="0" sizset="0">
<FORM id=info class="tabContent" encType=multipart/form-data method=post action="callbackesave.do" sizcache="0" sizset="0" >

<div style="padding-left:20%;padding-right:20%" class="tabContent">
<TABLE  id=infoTable class="inputTable" sizcache="0" sizset="0">
<TBODY sizcache="0" sizset="0">
<TR>
<TH>系统编码 </TH>
<TD><SELECT name="appCode" id=appCode >  
       <OPTION value="10001" ${info.appCode=='10001'?'selected':''}>10001</OPTION> 
       <OPTION value="10002" ${info.appCode=='10002'?'selected':''}>10002</OPTION>
       <OPTION value="10003" ${info.appCode=='10003'?'selected':''}>10003</OPTION>
       <OPTION value="10004" ${info.appCode=='10004'?'selected':''}>10004</OPTION> 
</SELECT> </TR>

<TR>
<TH>系统描述</TH><input type="hidden" name="id" value="${info.id}">
<TD><INPUT class=formText type=text name="appDesc" id=appDesc value="${info.appDesc}"> <LABEL class=requireField>*</LABEL> </TD></TR>
 
 <TR>
<TH>回调类型 </TH>
<TD><SELECT name="kind" id=kind >  
       <OPTION value="0" ${info.kind=='0'?'selected':''}>支付回调</OPTION> 
       <OPTION value="1" ${info.kind=='1'?'selected':''}>退款回调</OPTION>
</SELECT> </TR>

<TH>回调路径</TH> 
<TD><INPUT class=formText type=text name="callBackUrl" id=callBackUrl value="${info.callBackUrl}"  style="width:500px"> <LABEL class=requireField>*</LABEL> </TD></TR>

</TBODY></TABLE>

<DIV class=buttonArea>
<INPUT hideFocus class=formButton value="确  定" type=submit >&nbsp;&nbsp; 
<INPUT hideFocus class=formButton onclick="window.history.back(); return false;" value="返  回" type=button> 
</DIV>

</div>

</FORM>


</BODY>

</html>