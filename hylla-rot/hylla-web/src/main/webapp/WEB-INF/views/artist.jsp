<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<html>
<head>
	<title>Hylla - artist</title>
    <c:set var="url">${pageContext.request.requestURL}</c:set>
    <base href="${fn:substring(url, 0, fn:length(url) - fn:length(pageContext.request.requestURI))}${pageContext.request.contextPath}/" />
	<link rel="stylesheet" href="resources/main.css" type="text/css" />
	<link rel="shortcut icon" href="resources/icon.png" />
</head>
<body>
	<div id="artist">
		<a href="">Hjem</a>

		<div id="artistVenstredel">
			<div class="stortbilde">
				<img alt="${artist.navn}" src="${artist.bildelink}">
			</div><br> 
			<span class="artistnavn">${artist.navn}</span>
			<div id="sjanger">
				<form:form id="artistForm" method="POST" action="artist/lagre">
					<form:select path="nasjonalitet" items="${nasjonaliteter}"
						onchange="document.getElementById('artistForm').submit()"
						autofocus="true" />
					<form:hidden path="artistId" />
				</form:form>
			</div>
		</div>
	</div>

	<div id="artistHylledel">
		<c:forEach var="album" items="${albumene}">
			<div class="album">
				<a href="aapne?spotifyURI=${album.spotifyURI}">
					<img alt="${album.artist.navn} - ${album.navn}" title="${album.artist.navn} - ${album.navn}"
						src="${album.coverartlink}"></a><br> 
				<c:if test="${album.erPaaHylle}">
					<a href="album/fjernFraHylle?albumId=${album.id}">Fjern fra hylla</a>
				</c:if>
				<c:if test="${album.erPaaHylle == false}">
					<a href="album/leggTilPaaHylle?albumId=${album.id}">Legg til på hylla</a> 
				</c:if><br>
				<span class="artistnavn"><a	href="artist/?artistId=${album.artist.id}"> ${album.artist.kortnavn}</a></span><br>
				<a href="album/?albumId=${album.id}">${album.kortnavn}</a><br> 
			</div>
		</c:forEach>
	</div>
</body>
</html>