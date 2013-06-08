<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<html>
<head>
	<title>Hylla - masseinnlasting av album</title>
    <c:set var="url">${pageContext.request.requestURL}</c:set>
    <base href="${fn:substring(url, 0, fn:length(url) - fn:length(pageContext.request.requestURI))}${pageContext.request.contextPath}/" />
	<link rel="stylesheet" href="resources/main.css" type="text/css" />
	<link rel="shortcut icon" href="resources/icon.png" />
</head>
<body>
	<div>
		<form:form id="innlastForm" method="POST" action="album/kjoermasseinnlasting">
			Kopier inn liste over spor og album under<br>
			<form:textarea path="data" rows="50" cols="50" /><br>
			<input type="submit" value="Kjør masseinnlesning">
		</form:form>
	</div>
</body>
</html>
