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

<title>付款记录</title>
</head>
<BODY class=list style="min-width: 1150px;"> 
<DIV class=body style="text-align:center">
<div style="background-color:#363636;height:50px;color:#fff;font-size:20px;line-height:50px;margin-bottom:0px">
 星美支付平台
</div>
<div style="width:90%;line-height:50px;font-size:14px;text-align:left;margin-left: auto;margin-right: auto"">
  <a href="orderquery.do" style="color:red">付款记录查询</a>&nbsp;&nbsp; &nbsp;&nbsp; 
  <a href="refundquery.do">退款记录查询</a>&nbsp;&nbsp; &nbsp;&nbsp; 
  <a href="orderlogquery.do" >下单日志查询</a>&nbsp;&nbsp; &nbsp;&nbsp;
  <a href="refundlogquery.do">退款日志查询</a>&nbsp;&nbsp; &nbsp;&nbsp; 
   <c:if test="${role eq '0'}">
  <a href="callback.do" >回调地址管理</a>&nbsp;&nbsp; &nbsp;&nbsp;
  <a href="userlist.do" >用户管理</a>&nbsp;&nbsp; &nbsp;&nbsp;
  </c:if>
   <a href="../login.jsp" style="float:right;text-decoration:underline;padding-right:30px">退出</a>
</div>
<div style="width:90%;margin-left: auto;margin-right: auto;text-align:left">
<FORM id=listForm name=userlist method=post action=orderquery.do > 
<DIV class=listBar>
  &nbsp;&nbsp; <LABEL>订单来源: </LABEL>
 <SELECT name=appcode id=name=appcode >  
     <OPTION value="" ${param.appcode==''?'selected':''}>所有</OPTION> 
     <OPTION value="10003" ${param.appcode=='10003'?'selected':''}>10003</OPTION> 
     <OPTION value="10004" ${param.appcode=='10004'?'selected':''}>10004</OPTION> 
</SELECT>
  &nbsp;&nbsp; <LABEL>订单号: </LABEL>
 <INPUT type=text name=billNo value="${param.billNo}" size="40"> 
 &nbsp;&nbsp;
 <LABEL>下单状态: </LABEL> 
 <SELECT name=status id=status >  
     <OPTION value=""  ${param.status==''?'selected':''}>所有</OPTION> 
     <OPTION value="0" ${param.status=='0'?'selected':''}>成功</OPTION> 
     <OPTION value="1" ${param.status=='1'?'selected':''}>失败</OPTION> 
</SELECT>
 
 &nbsp;&nbsp; 
 <LABEL>开始时间: </LABEL>
 <input type="text" name="beginDate" onclick="WdatePicker()"  
	 <c:if test="${not empty param.beginDate}">
	   value="${param.beginDate}"
	 </c:if> readonly/> 
 &nbsp;&nbsp; 
 <LABEL>结束时间: </LABEL>
 <input type="text" name="endDate" onclick="WdatePicker()"  
 <c:if test="${not empty param.endDate}">
   value="${param.endDate}"
 </c:if> readonly />
 <INPUT hideFocus id=searchButton class=formButton value="搜 索" type=button > &nbsp;&nbsp; 
 <LABEL>每页显示: </LABEL>
 <SELECT id=pageSize name=page.pageSize> 
 <OPTION value=10 ${page.pageSize==10?'selected':''}>10</OPTION> 
 <OPTION value=20 ${page.pageSize==20?'selected':''}>20</OPTION> 
 <OPTION value=50 ${page.pageSize==50?'selected':''}>50</OPTION> 
 <OPTION value=100 ${page.pageSize==100?'selected':''}>100</OPTION>
 </SELECT> 
</DIV>
<%int i=1;%>
<TABLE id=listTable class=listTable>
<TBODY>
<TR>
<TH><SPAN>订单来源</SPAN> </TH>
<TH><SPAN>订单号</SPAN> </TH>
<TH><SPAN>下单渠道</SPAN> </TH>
<TH><SPAN>支付方式</SPAN> </TH>
<TH><SPAN>标题</SPAN> </TH>
<TH><SPAN>金额</SPAN> </TH>
<TH><SPAN>创建时间</SPAN> </TH>
<TH><SPAN>是否成功</SPAN> </TH>
<TH> <SPAN>异常描述</SPAN> </TH>
<TH> <SPAN>回调</SPAN> </TH>
</TR>
<c:forEach items="${page.list}" var="orderlog">
<TR >
<TD ><SPAN>${orderlog.appcode} </SPAN></TD>
<TD ><SPAN>${orderlog.billNo} </SPAN></TD>
<TD ><SPAN>${orderlog.channel} </SPAN></TD>
<TD ><SPAN>${orderlog.payType} </SPAN></TD>
<TD title="${orderlog.title}"><SPAN>
<c:if test="${fn:length(orderlog.title)>'13'}">  
                    ${fn:substring(orderlog.title,0,13)}...  
            </c:if>  
            <c:if test="${fn:length(orderlog.title)<='13'}">  
                ${orderlog.title}  
            </c:if> 
</SPAN></TD>
<TD ><SPAN>${orderlog.totalFee/100} </SPAN></TD>
<TD ><SPAN><fmt:formatDate value="${orderlog.createTime}" type="both"/></SPAN></TD>

<TD ><SPAN> 
         <c:if test="${orderlog.status == 0}">  
              成功      
         </c:if>  
          <c:if test="${orderlog.status == 1}">  
              失败      
         </c:if>  
          <c:if test="${empty orderlog.status}">  
              未支付     
         </c:if>  
</SPAN></TD>
<TD title="${orderlog.callBackMemo}"><SPAN>
            <c:if test="${fn:length(orderlog.callBackMemo)>'13'}">  
                    ${fn:substring(orderlog.callBackMemo,0,13)}...  
            </c:if>  
            <c:if test="${fn:length(orderlog.callBackMemo)<='13'}">  
                ${orderlog.callBackMemo}  
            </c:if>   
 </SPAN></TD>
 
 <TD ><SPAN> 
         <c:if test="${orderlog.noticeStatus == 0}">  
              成功      
         </c:if>  
          <c:if test="${orderlog.noticeStatus == 1}">  
              失败      
         </c:if>  
          
</SPAN></TD>
</TR>
</c:forEach>
</TBODY>
</TABLE>

<DIV class=pagerBar> 
付款记录&nbsp;总记录数:${page.total} (共${page.pages}页)
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