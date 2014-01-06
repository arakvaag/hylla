<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>
<head>
	<title>Hylla</title>
    <c:set var="url">${pageContext.request.requestURL}</c:set>
    <base href="${fn:substring(url, 0, fn:length(url) - 
    	fn:length(pageContext.request.requestURI))}${pageContext.request.contextPath}/" />
	<link rel="stylesheet" href="resources/mobil.css" type="text/css" />
	<link rel="shortcut icon" href="resources/icon.png" />
	<script src="http://ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"></script>
	<script src="resources/hylla.js"></script>
	<meta name="viewport" content="width=100%; initial-scale=1; maximum-scale=1; minimum-scale=1; user-scalable=no;" />
</head>
<body>

		<form:form id="filter">
			<form:select id="sjanger" autofocus="autofocus" path="valgtSjanger" items="${sjangre}" onchange="oppdaterFiltrering()"/>
			<form:select id="tidsperiode" path="valgtTidsperiode" items="${tidsperioder}" onchange="oppdaterFiltrering()"/>
		</form:form>
		
		<div id="albumdel">
			<jsp:include page="_mobilhylle.jsp"></jsp:include>
		</div>

</body>
</html>


