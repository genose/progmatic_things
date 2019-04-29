<%@ page language="java" contentType="text/html; charset=ISO-8859-15"
    pageEncoding="ISO-8859-15"  isErrorPage="true" %>
<% response.setStatus(500); %>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-15">
<title>Server Application Error while Requesting Ressource</title>
</head>
<body>
Server Application Error while Requesting Ressource<br />
The Formuled request can't be achieve thru this parameters
<br /><a href="<%=  request.getContextPath() %>">Back ...</a>
</body>
</html>