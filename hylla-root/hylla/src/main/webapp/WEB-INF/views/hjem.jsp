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
</head>
<body>

	<div id="soekespilledel">
		
		<form id="soekform" method="GET" action="album/utfoerSoek">
			<div class="element">
				<table class="soeketabell">
					<tr>
						<td>Artist:</td>
						<td><input id="inputSoekArtist" type="text" name="artist"
							value=""></td>
					</tr>
					<tr>
						<td>Album:</td>
						<td><input id="inputSoekAlbum" type="text" name="album"
							value=""></td>
					</tr>
				</table>
				<input id="gjoersoek" type="submit" value="Søk i Spotify">
			</div>
		</form>

		<iframe id="avspiller" seamless src="https://embed.spotify.com/?uri=${spotifyURI}"></iframe>

	</div>

	<form:form id="filter">
		<form:select id="sjanger" autofocus="autofocus" path="valgtSjanger" items="${sjangre}" onchange="oppdaterFiltrering()"/>
		<form:select id="tidsperiode" path="valgtTidsperiode" items="${tidsperioder}" onchange="oppdaterFiltrering()"/>
	</form:form>
	
	<div id="myk_kant"></div>

	<div id="albumene">
		<jsp:include page="_hylle.jsp"></jsp:include>
	</div>

</body>
</html>
