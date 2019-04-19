<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Insert title here</title>


    <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/src/js/jsgrid/css/jsgrid.css" />
    <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/src/js/jsgrid/css/theme.css" />

    <link rel="stylesheet" href="http://code.jquery.com/ui/1.11.2/themes/cupertino/jquery-ui.css">
    <script src="http://code.jquery.com/jquery-1.10.2.js"></script>
    <script src="http://code.jquery.com/ui/1.11.2/jquery-ui.js"></script>
    <script src="http://ajax.aspnetcdn.com/ajax/jquery.validate/1.9/jquery.validate.min.js"></script>

    <script src="<%= request.getContextPath() %>/src/js/jsgrid/src/jsgrid.core.js"></script>
    <script src="<%= request.getContextPath() %>/src/js/jsgrid/src/jsgrid.load-indicator.js"></script>
    <script src="<%= request.getContextPath() %>/src/js/jsgrid/src/jsgrid.load-strategies.js"></script>
    <script src="<%= request.getContextPath() %>/src/js/jsgrid/src/jsgrid.sort-strategies.js"></script>
    <script src="<%= request.getContextPath() %>/src/js/jsgrid/src/jsgrid.field.js"></script>
    <script src="<%= request.getContextPath() %>/src/js/jsgrid/src/fields/jsgrid.field.text.js"></script>
    <script src="<%= request.getContextPath() %>/src/js/jsgrid/src/fields/jsgrid.field.number.js"></script>
    <script src="<%= request.getContextPath() %>/src/js/jsgrid/src/fields/jsgrid.field.select.js"></script>
    <script src="<%= request.getContextPath() %>/src/js/jsgrid/src/fields/jsgrid.field.checkbox.js"></script>
    <script src="<%= request.getContextPath() %>/src/js/jsgrid/src/fields/jsgrid.field.control.js"></script>
</head>
<body>
<h1>Basic Scenario</h1>
    <div id="jsGrid"></div>

    <script>
        $(function() {

            $("#jsGrid").jsGrid({
                height: "70%",
                width: "100%",
                filtering: true,
                editing: true,
                inserting: true,
                sorting: true,
                paging: true,
                autoload: true,
                pageSize: 15,
                pageButtonCount: 5,
                deleteConfirm: "Do you really want to delete the client?",
                controller: db,
                fields: [
                    { name: "Name", type: "text", width: 150 },
                    { name: "Age", type: "number", width: 50 },
                    { name: "Address", type: "text", width: 200 },
                    { name: "Country", type: "select", items: db.countries, valueField: "Id", textField: "Name" },
                    { name: "Married", type: "checkbox", title: "Is Married", sorting: false },
                    { type: "control" }
                ]
            });

        });
    </script>
</body>
</html>