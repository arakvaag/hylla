function spillAlbum(spotifyURI) {
	lagreAapentAlbum(spotifyURI);
	
	var iframe = $('#avspiller');
    if ( iframe.length && iframe.is(":visible")) {
        iframe.attr('src', "https://embed.spotify.com/?uri=" + spotifyURI);  
    } else {
    	win = window.open(spotifyURI);
        win.close();
    }
}

function lagreAapentAlbum(spotifyURI) {
	var baseHref = document.getElementsByTagName('base')[0].href;
	$.post(baseHref + "/lagreAapentAlbum", {spotifyURI:spotifyURI});
}

function oppdaterFiltrering(){
	var albumdel = $('#albumdel');
    if (albumdel.length) {
    	var baseHref = document.getElementsByTagName('base')[0].href;
    	var url = baseHref + "endreFilter"; 
    	var valgtSjanger = $('#sjanger').find(":selected").val();
    	var valgtTidsperiode = $('#tidsperiode').find(":selected").val();
		$.post(url, {sjanger: valgtSjanger, tidsperiode: valgtTidsperiode}, function(data) {
    		$('#albumdel').html(data);
    		window.scrollTo(0, 0);
    	});
    }
}


