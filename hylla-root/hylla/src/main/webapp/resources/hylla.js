function spillAlbum(spotifyURI) {
	var $iframe = $('#avspiller');
    if ( $iframe.length ) {
    	var href = location.href.split("/");
    	var url = href[0] + "/" + href[1] + "/" + href[2] + "/" + href[3] + "/lagreAapentAlbum"; 
    	$.post(url, {spotifyURI:spotifyURI});
        $iframe.attr('src', "https://embed.spotify.com/?uri=" + spotifyURI);  
    }
}

function oppdaterFiltrering(){
	var $albumene = $('#albumene');
    if ($albumene.length) {
    	var href = location.href.split("/");
    	var url = href[0] + "/" + href[1] + "/" + href[2] + "/" + href[3] + "/endreFilter"; 
    	var valgtSjanger = $('#sjanger').find(":selected").val();
    	var valgtTidsperiode = $('#tidsperiode').find(":selected").val();
    	$.post(url, {sjanger: valgtSjanger, tidsperiode: valgtTidsperiode}, function(data) {
    		$('#albumene').html(data);
    		window.scrollTo(0, 0);
    	});
    }
    
}


