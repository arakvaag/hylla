package org.rakvag.spotifyapi.entity;

public abstract class SpotifyEntitet {
	protected String href;
	protected String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

}
