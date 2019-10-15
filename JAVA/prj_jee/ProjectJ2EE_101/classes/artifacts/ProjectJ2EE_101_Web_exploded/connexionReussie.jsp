<%@ page language="java" contentType="text/html; charset=ISO-8859-15"
    pageEncoding="ISO-8859-15"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-15">
<title>Connexion Réussi</title>
</head>
<body>
Bonjour  <%= request.getAttribute("loginUsername") %> 

<form action="./MyConnexion" method="post">

<input type="submit" value="aller au panel ... " />


</form>
 
</body>
</html>