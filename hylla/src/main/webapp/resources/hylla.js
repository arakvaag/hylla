function spillAlbum(spotifyURI) {
	var $iframe = $('#avspiller');
	var baseHref = document.getElementsByTagName('base')[0].href;
    if ( $iframe.length ) {
    	var url = baseHref + "/lagreAapentAlbum"; 
    	$.post(url, {spotifyURI:spotifyURI});
        $iframe.attr('src', "https://embed.spotify.com/?uri=" + spotifyURI);  
    } else { //Er ikke hjem-viewet
    	location.href = baseHref + "/aapne?spotifyURI=" + spotifyURI;
    }
}

function oppdaterFiltrering(){
	var albumdel = $('#albumdel');
    if (albumdel.length) {
    	var href = location.href.split("/");
    	var url = href[0] + "/" + href[1] + "/" + href[2] + "/" + href[3] + "/endreFilter"; 
    	var valgtSjanger = $('#sjanger').find(":selected").val();
    	var valgtTidsperiode = $('#tidsperiode').find(":selected").val();
    	$.post(url, {sjanger: valgtSjanger, tidsperiode: valgtTidsperiode}, function(data) {
    		$('#albumdel').html(data);
    		window.scrollTo(0, 0);
    	});
    }
}


