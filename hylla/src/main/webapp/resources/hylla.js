function spillAlbum(spotifyURI) {
	lagreAapentAlbum(spotifyURI);
	
	var $iframe = $('#avspiller');
    if ( $iframe.length ) {
        $iframe.attr('src', "https://embed.spotify.com/?uri=" + spotifyURI);  
    } else { //Er ikke i hjem-viewet
    	location.href = document.getElementsByTagName('base')[0].href + "/aapne?spotifyURI=" + spotifyURI;
    }
}

function lagreAapentAlbum(spotifyURI) {
	var baseHref = document.getElementsByTagName('base')[0].href;
	$.post(baseHref + "/lagreAapentAlbum", {spotifyURI:spotifyURI});
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


