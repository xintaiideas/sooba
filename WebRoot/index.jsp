<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="html" uri="/struts-tags"%>


<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<meta http-equiv=Content-Type content="text/html;charset=gb2312">
		<title>企业内部文档搜索系统</title>
		<link href="css/souba.css" rel="stylesheet" type="text/css">
		<script type="text/javascript">
			function check(){
			var val= document.forms[0].fieldname.value;
			
			if(val==""){
				   return false;
			   }
			   else
			   {
			   document.forms[0].submit();
			   }
			}
		</script>
	</head>
	<body>
        
        <br>
		<br>
		<br>
		<br>
		<br>
		<center>
			<img src="images/searchba.GIF" width=180 height=84 usemap="#mp" id=lg>
			<br>
			<br>
			<br>
			<br>
			

			<html:form action="SearchAction" theme="simple" >
			
			
				<html:textfield  name="fieldname" size="42" maxlength="100" ></html:textfield> 
								
				<input type="button" value="搜吧"  onclick="check()"/>
                <html:a href="advancesearch.jsp">高级</html:a>
			</html:form>
			
	
			
			
			<p id=km>

			</p>
			<p style="height: 60px">
			<table cellpadding=0 cellspacing=0 id=lk>
				<tr>
					<td></td>
				</tr>
			</table>

			<p style="height: 30px">
				<a
					
					href="#"><br>
				</a>
			</p>
			<p style="height: 14px">
			</p>
			<br>
			<br>
			<br>
			<br>
			<br>
			<br>
			<br>
			<br>

			<p id=b>
				<span>Copyright &copy; 1999 - 2010&nbsp; All Rights Reserved</span>
			</p>

		</center>
	</body>
</html>
