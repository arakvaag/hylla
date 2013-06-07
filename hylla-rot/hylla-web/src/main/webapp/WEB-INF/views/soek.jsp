<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<html>
<head>
	<title>Hylla - søk album</title>
    <c:set var="url">${pageContext.request.requestURL}</c:set>
    <base href="${fn:substring(url, 0, fn:length(url) - fn:length(pageContext.request.requestURI))}${pageContext.request.contextPath}/" />
	<link rel="stylesheet" href="resources/main.css" type="text/css" />
	<link rel="shortcut icon" href="resources/icon.png" />
</head>
<body>
	<script type="text/javascript">
	
	
	
	</script>
	<div class="toppmeny">
		<form method="GET" action="album/utfoerSoek">
			<div class="element">
				<a href="">Hjem</a>
			</div>
			<div class="element">
				<input type="text" name="artist" value="" autofocus="autofocus"> <input type="text"
					name="album" value=""> <input type="submit" value="Søk i Spotify"> <span
					class="spacer10">&nbsp;</span> <input type="checkbox" name="taMedKorteAlbum" value="true">Ta
				med korte album?<br>
			</div>
		</form>
	</div>

	<div class="hoveddelSide">
		<div class="soekeResultater">
			<c:forEach var="album" items="${albumliste}">
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
					${album.aar} - ${album.lengdeFormatert}
				</div>
			</c:forEach>
		</div>
	</div>
</body>
</html>
