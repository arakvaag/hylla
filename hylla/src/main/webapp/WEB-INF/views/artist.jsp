<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>
<head>
	<title>Hylla - artist</title>
    <c:set var="url">${pageContext.request.requestURL}</c:set>
    <base href="${fn:substring(url, 0, fn:length(url) - fn:length(pageContext.request.requestURI))}${pageContext.request.contextPath}/" />
	<link rel="stylesheet" href="resources/main.css" type="text/css" />
	<link rel="shortcut icon" href="resources/icon.png" />
	<script src="http://ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"></script>
	<script src="resources/hylla.js"></script>
</head>
<body>
	<div class="toppMeny">
		<a href="">Hjem</a>
	</div>
		
	<div id="artistVenstre">
		<ul>
			<li class="stortbilde"><img alt="${artist.navn}" src="${artist.bildelink}"></li>
			<li id="artistTittel" class="artistnavn">${artist.navn}</li>
		</ul>
	</div>
	
	<div id="artistHoyre">
		<jsp:include page="_hylle.jsp"></jsp:include>
	</div>
	
</body>
</html>