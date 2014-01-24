<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>		

<!DOCTYPE html>
<html>
<head>
	<title>Hylla - søk album</title>
    <c:set var="url">${pageContext.request.requestURL}</c:set>
    <base href="${fn:substring(url, 0, fn:length(url) - fn:length(pageContext.request.requestURI))}${pageContext.request.contextPath}/" />
	<link rel="stylesheet" href="resources/main.css" type="text/css" />
	<link rel="shortcut icon" href="resources/icon.png" />
	<script src="http://ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"></script>
	<script src="resources/hylla.js"></script>
	<meta name="viewport" content="width=100%; initial-scale=1; maximum-scale=1; minimum-scale=1; user-scalable=no;" />
	<meta charset="UTF-8">
</head>
<body>
	
	<div class="toppMeny">
		<a href="">Hjem</a>
	</div>

	<div id="soekmeny">
		<form method="GET" action="album/utfoerSoek" accept-charset="UTF-8">
			<div class="element">
				<input type="text" name="artist" value="" autofocus="autofocus"> <input type="text"
					name="album" value=""> <input type="submit" value="Søk i Spotify"> <span
					class="spacer10">&nbsp;</span> <input type="checkbox" name="taMedKorteAlbum" 
						value="true">Ta med korte album?<br>
			</div>
		</form>
	</div>

	<div id="soekhoveddelSide">
		<jsp:include page="_hylle.jsp"></jsp:include>
	</div>
	
</body>
</html>
