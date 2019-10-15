<%@ page language="java" contentType="text/html; charset=ISO-8859-15"
    pageEncoding="ISO-8859-15" isErrorPage="true" %>
<% response.setStatus(500); %>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-15">
<title>Unexpected Exception while Requesting Ressource</title>
</head>
<body>
Unexpected Exception while Requesting Ressource
<br /><a href="<%=  request.getContextPath() %>">Back ...</a>

</body>
</html>