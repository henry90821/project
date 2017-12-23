<%@ page contentType="text/html; charset=utf-8" language="java" import="java.util.*,java.text.*" pageEncoding="UTF-8" isELIgnored="false"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link href="<%=request.getContextPath()%>/css/base.css" rel="stylesheet" type="text/css" />
<link href="<%=request.getContextPath()%>/css/admin.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.min.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.pager.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/base.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/admin.js"></script>
<script language="javascript" type="text/javascript" src="<%=request.getContextPath()%>/DatePicker/WdatePicker.js"></script>

<title>应用信息</title>
</head>
<BODY class=list style="min-width: 1150px;"> 
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
  <a href="userlist.do" >用户管理</a>&nbsp;&nbsp; &nbsp;&nbsp;
  </c:if>
   <a href="../login.jsp" style="float:right;text-decoration:underline;padding-right:30px">退出</a>
   
   
</div>
<div style="width:90%;margin-left: auto;margin-right: auto;text-align:left">
<FORM id=listForm name=userlist method=post action=callback.do > 
<%int i=1;%>
<TABLE id=listTable class=listTable>
<TBODY>
<TR>
<TH><SPAN>系统编码</SPAN> </TH>
<TH><SPAN>系统描述</SPAN> </TH>
<TH><SPAN>回调类型</SPAN> </TH>
<TH><SPAN>回调路径</SPAN> </TH>
<TH><SPAN>创建时间</SPAN> </TH>
<TH><SPAN>最后更新时间</SPAN> </TH>
<TH><SPAN>操作</SPAN> </TH>
</TR>
<c:forEach items="${page.list}" var="info">
<TR >
<TD ><SPAN>${info.appCode} </SPAN></TD>
<TD ><SPAN>${info.appDesc} </SPAN></TD>
<TD ><SPAN>${info.kind== 0?'支付回调':'退款回调'} </SPAN></TD>
<TD ><SPAN>${info.callBackUrl} </SPAN></TD>
 
<TD ><SPAN><fmt:formatDate value="${info.createDate}" type="both"/></SPAN></TD>
<TD ><SPAN><fmt:formatDate value="${info.lastUpdateDate}" type="both"/></SPAN></TD>
<TD ><SPAN><a href="callbackedit.do?id=${info.id}">修改</a></SPAN></TD>


</TR>
</c:forEach>
</TBODY>
</TABLE>

<DIV class=pagerBar> 
应用信息&nbsp;总记录数:${page.total} (共${page.pages}页)
<DIV class=pager>
<SPAN id=pager>
</SPAN>
<INPUT id=pageNumber type=hidden name=page.pageNumber value=${page.pageNum} > 
<INPUT id=totalSize type=hidden name=page.totalSize value=${page.total} > 
<INPUT id=orderBy type=hidden name=pager.orderBy value=${page.orderBy} > 
</DIV>
</DIV>

</FORM>
</div>
</DIV>
</BODY>
<SCRIPT type=text/javascript>
$().ready( function() {
	
	var $pager = $("#pager");
	
	$pager.pager({
		pagenumber: ${page.pageNum},
		pagecount: ${page.pages},
		buttonClickCallback: $.gotoPage
	});

})


</SCRIPT>
</html>