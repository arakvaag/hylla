package org.rakvag.hylla.web;


public class DetaljerForm {
	private String sjanger;
	private long albumId;
	private long artistId;

	public String getSjanger() {
		return sjanger;
	}

	public void setSjanger(String sjanger) {
		this.sjanger = sjanger;
	}

	public long getAlbumId() {
		return albumId;
	}

	public void setAlbumId(long albumId) {
		this.albumId = albumId;
	}

	public long getArtistId() {
		return artistId;
	}

	public void setArtistId(long artistId) {
		this.artistId = artistId;
	}
	
}
