<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
		
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
		<c:if test="${visAarOgLengde}">${album.aar} - ${album.lengdeFormatert}</c:if>
	</div>
</c:forEach>
