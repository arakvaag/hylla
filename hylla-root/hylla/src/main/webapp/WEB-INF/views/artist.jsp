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
		<c:forEach var="album" items="${albumene}">
			<div class="album">
				<a href="aapne?spotifyURI=${album.spotifyURI}">
					<img alt="${album.artist.navn} - ${album.navn}" title="${album.artist.navn} - ${album.navn}"
						src="${album.coverartlink}"></a><br> 
				<c:if test="${album.erPaaHylle}">
					<a href="album/fjernFraHylle?albumId=${album.id}">Fjern fra hylla</a>
				</c:if>
				<c:if test="${album.erPaaHylle == false}">
					<a href="album/leggTilPaaHylle?albumId=${album.id}">Legg til p� hylla</a> 
				</c:if><br>
				<span class="artistnavn"><a	href="artist/?artistId=${album.artist.id}"> ${album.artist.kortnavn}</a></span><br>
				<a href="album/?albumId=${album.id}">${album.kortnavn}</a><br> 
			</div>
		</c:forEach>
	</div>
</body>
</html>