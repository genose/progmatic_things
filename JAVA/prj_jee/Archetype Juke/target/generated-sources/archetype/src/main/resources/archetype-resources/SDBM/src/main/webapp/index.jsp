#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
<%
    session.setAttribute("racine","http://localhost:8081"+request.getContextPath());
 response.sendRedirect("faces/index.jsf");
%>
