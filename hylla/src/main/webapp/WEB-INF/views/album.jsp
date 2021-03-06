<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>		

<!DOCTYPE html>
<html>
<head>
	<title>Hylla - album</title>
    <c:set var="url">${pageContext.request.requestURL}</c:set>
    <base href="${fn:substring(url, 0, fn:length(url) - fn:length(pageContext.request.requestURI))}${pageContext.request.contextPath}/" />
	<link rel="stylesheet" href="resources/main.css" type="text/css" />
	<link rel="shortcut icon" href="resources/icon.png" />
	<meta name="viewport" content="width=100%; initial-scale=1; maximum-scale=1; minimum-scale=1; user-scalable=no;" />
	<meta charset="UTF-8">
</head>
<body>
	
	<div class="toppMeny">
		<a href="">Hjem</a>
	</div>
	
	<div id="albumVenstre">
		<div class="stortbilde">
			<a href="aapne?spotifyURI=${album.spotifyURI}"><img alt="${album.artist.navn} - ${album.navn}"
				title="${album.artist.navn} - ${album.navn}"
				src="${album.coverartlink}"></a>
		</div>
		<div id="tittel">
			<a href="artist/?artistId=${album.artist.id}">${album.artist.navn}</a><br> 
			${album.navn} (${album.aar})<br>
		</div>
		<div class="albumNedreVenstreTabell">
			<div id="sjanger">
				<form:form id="albumForm" method="POST" action="album/lagre" accept-charset="UTF-8">
					<form:select path="sjanger" items="${sjangre}"
						onchange="document.getElementById('albumForm').submit()"
						autofocus="true" />
					<form:hidden path="albumId" />
					<c:if test="${album.erPaaHylle}">
						<a href="album/fjernFraHylle?albumId=${album.id}">Fjern fra hylla</a>
					</c:if>
					<c:if test="${album.erPaaHylle == false}">
						<a href="album/leggTilPaaHylle?albumId=${album.id}">Legg til på hylla</a> 
					</c:if><br>
				</form:form>
			</div>
		</div>
	</div>
	
	<div id="albumHoyre">
		<div id="spor">
			<c:forEach var="sporet" items="${album.sorterteSpor}">
				<c:if test="${sporet.disknummer=='2' && sporet.spornummer=='1'}">
					<br>
				</c:if>
				<a href="aapne?spotifyURI=${sporet.spotifyURI}">${sporet.spornummer} - ${sporet.navn} (${sporet.lengdeFormatert})</a>	<br>
			</c:forEach>
		</div>
	</div>
		
</body>
</html>