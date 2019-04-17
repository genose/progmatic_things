<%@ page language="java" contentType="text/html; charset=ISO-8859-15"
    pageEncoding="ISO-8859-15"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-15">
<base href="/ProjectJ2EE_101/">
<link rel="stylesheet" type="text/css" href="/ProjectJ2EE_101/src/css/theme.css">
<title>Insert title here</title>
</head>
<body>



	

	<div id="header">
		<div>FORMAPOD</div>
		<div id="connexion">
		<div>
			<fieldset>
			<label>Veuillez vous identifier </label>
			<form action="./MyConnexion" method="POST">
			
				<label>Utilisateur :</label>
				<input type="text" value="Nom utilisateur" id="username"  name="username"  />
				<label >Password : </label>
				<input type="text" value="mot de passe" id="password" name="password" />
				<input type="submit" value="Envoyerrr" />
			</fieldset>
			</form>
		</div>
		</div>
	</div>
	<div id="main">
		<div id="contentTable">
			<ul>
				<li>Information sur les formations </li>
				<li>Inscription</li>
			</ul>
		</div>
		<div id="mainContent">
		
		</div>
		
	</div>



</body>
</html>