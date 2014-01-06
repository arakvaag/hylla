<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>
<head>
	<title>Hylla - hjem</title>
    <c:set var="url">${pageContext.request.requestURL}</c:set>
    <base href="${fn:substring(url, 0, fn:length(url) - 
    	fn:length(pageContext.request.requestURI))}${pageContext.request.contextPath}/" />
	<link rel="stylesheet" href="resources/main.css" type="text/css" />
	<link rel="shortcut icon" href="resources/icon.png" />
	<script src="http://ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"></script>
	<script src="resources/hylla.js"></script>
	<meta name="viewport" content="width=100%; initial-scale=1; maximum-scale=1; minimum-scale=1; user-scalable=no;" />
</head>
<body>

	<div id="soekespilledel">
		
		<form id="soekform" method="GET" action="album/utfoerSoek">
			<label class="soekinputlabel" for="soekinputartist">Artist:</label>
			<span class="soekinput"><input type="text" name="artist" id="soekinputartist"></span>
			<label class="soekinputlabel" for="soekinputalbum">Album:</label>
			<span class="soekinput"><input type="text" name="artist" id="soekinputalbum"></span>
			<input id="gjoersoek" type="submit" value="Søk i Spotify">
		</form>

		<iframe id="avspiller" seamless src="https://embed.spotify.com/?uri=${spotifyURI}"></iframe>

	</div>

	<div id="filterdel">
		<form:form id="filter">
			<form:select id="sjanger" autofocus="autofocus" path="valgtSjanger" items="${sjangre}" onchange="oppdaterFiltrering()"/>
			<form:select id="tidsperiode" path="valgtTidsperiode" items="${tidsperioder}" onchange="oppdaterFiltrering()"/>
		</form:form>
		<div id="myk_kant"></div>
	</div>
	
	<div id="albumdel">
		<jsp:include page="_hylle.jsp"></jsp:include>
	</div>

</body>
</html>
