function test() {
	alert('test');
}

function spillAlbum(spotifyURI) {
	var $iframe = $('#avspiller');
    if ( $iframe.length ) {
    	var href = location.href.split("/");
    	var url = href[0] + "/" + href[1] + "/" + href[2] + "/" + href[3] + "/lagreAapentAlbum"; 
    	$.post(url, {spotifyURI:spotifyURI});
        $iframe.attr('src', "https://embed.spotify.com/?uri=" + spotifyURI);  
    }
}