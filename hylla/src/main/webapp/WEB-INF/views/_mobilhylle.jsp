<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
		
<c:forEach var="album" items="${albumene}">
	<div class="album">
		<a href="${album.spotifyURI}"><img src="${album.coverartlink}" onclick="lagreAapentAlbum('${album.spotifyURI}')"></a><br> 
		<span class="artistnavn">${album.artist.mobilnavn}</span><br>
		${album.mobilnavn}<br> 
	</div>
</c:forEach>
