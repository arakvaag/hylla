<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
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
		
		<form id="soekForm" method="GET" action="album/utfoerSoek">
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

		<iframe id="avspiller" src="https://embed.spotify.com/?uri=${spotifyURI}"
			width="270" height="350" seamless></iframe>

	</div>


	<div id="hylledel">
	
		<form:form id="filterForm" method="POST" action="endreFilter">
			<form:select path="valgtSjanger" items="${sjangre}"
				onchange="document.getElementById('filterForm').submit()"
				autofocus="autofocus" />
			<form:select path="valgtTidsperiode" items="${tidsperioder}"
				onchange="document.getElementById('filterForm').submit()" />
		</form:form>

		<c:forEach var="album" items="${albumene}">
			<div class="album">
				<img alt="${album.artist.navn} - ${album.navn}" title="${album.artist.navn} - ${album.navn}"
					src="${album.coverartlink}" onclick="spillAlbum('${album.spotifyURI}')"><br> 
				<c:if test="${album.erPaaHylle}">
					<a href="album/fjernFraHylle?albumId=${album.id}">Fjern fra hylla</a>
				</c:if>
				<c:if test="${album.erPaaHylle == false}">
					<a href="album/leggTilPaaHylle?albumId=${album.id}">Legg til på hylla</a> 
				</c:if><br>
				<span class="artistnavn"><a	href="artist/?artistId=${album.artist.id}"> 
					${album.artist.kortnavn}</a></span><br>
				<a href="album/?albumId=${album.id}">${album.kortnavn}</a><br> 
			</div>
		</c:forEach>
		
	</div>

</body>
</html>
