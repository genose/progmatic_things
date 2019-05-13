<jsp:directive.tag language="java" pageEncoding="UTF-8"/>
<%@ include file="header.jsp"%>
<%@ include file="login.jsp"%>  
<jsp:include page="${param.content}.jsp" /> 
<%@ include file="footer.jsp"%>