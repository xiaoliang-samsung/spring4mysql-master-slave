<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Mysql switch database</title>
<script type="text/javascript" src="static/js/jquery.min.js"></script>
<script type="text/javascript">
	
</script>
</head>
<body>

	<form id="chatForm" method="post" action="switch">
		<table>
			<tr>
				<th>please select a database to see the content:</th>
				<td><select id="type" name="type">
						<option value="master">master</option>
						<option value="slave">slave</option>

				</select></td>
			</tr>
			<tr>
				<td><input id="send" type="submit" value="switch" /></td>
			</tr>
		</table>
	</form>

</body>
</html>
