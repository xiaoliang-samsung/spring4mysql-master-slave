<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page session="false"%>
<html>
<head>
<title>Hello Spring MySQL</title>
</head>
<body>
	<h1>Exception Occurred!</h1>

	<h4>error Info:</h4>

	<c:out value="${error}" />

</body>
</html>
