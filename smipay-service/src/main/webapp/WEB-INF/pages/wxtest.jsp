<%@ page language="java" contentType="text/html; charset=UTF-8"
  pageEncoding="UTF-8"%>
<html>


<script language="javascript">
function wxpay(){
	 WeixinJSBridge.invoke('getBrandWCPayRequest',{
	 "appId" : "wx172c25161190e29e","timeStamp" : "1462958839", "nonceStr" : "1727186354", "package" : "prepay_id=wx201605111727192fada904730533359853","signType" : "MD5", "paySign" : "FC4755C066F0444DC589236849F5AA10" 
			},function(res){
			WeixinJSBridge.log(res.err_msg);
//			alert(res.err_code + res.err_desc + res.err_msg);
           if(res.err_msg == "get_brand_wcpay_request:ok"){  
               //alert("微信支付成功!");  
           	window.location.href="http://testmall.xingmeihui.com/smiweb/resources/wx-movie/pages/my_movieTickets.html"; 
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

if(isWeiXin)
{
	wxpay();
}

</script>
</html>

