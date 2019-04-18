<%@ page import="BDDConnector.*,AuthConnexion.*" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-15"
    pageEncoding="ISO-8859-15"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-15">
<base href="<%= request.getAttribute("BASE_PATH") %>/" />
<title>Erreur d authentification</title>
<link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/src/css/theme.css">
</head>
<body>

<a href="<%= request.getContextPath() %>/">Veuillez vous authentifier ...</a>



</body>
</html>