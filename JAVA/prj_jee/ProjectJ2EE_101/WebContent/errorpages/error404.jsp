<%@ page language="java" contentType="text/html; charset=ISO-8859-15"
    pageEncoding="ISO-8859-15" isErrorPage="true" %>
<% response.setStatus(404); %>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-15">
<title>404 Requested Ressource is not availble</title>
</head>
<body>
404 Requested Ressource is not availble <%= request.getHeader("referer") %>

<br /><a href="<%=  request.getContextPath() %>">Back ...</a>

</body>
</html>