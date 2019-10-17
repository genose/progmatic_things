<%
    session.setAttribute("racine","http://localhost:8081"+request.getContextPath());
 response.sendRedirect("faces/index.jsf");
%>
