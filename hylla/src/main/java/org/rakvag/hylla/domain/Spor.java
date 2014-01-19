package org.rakvag.hylla.domain;


public class Spor {

	private String navn;
	private String spotifyURI;
	private int spornummer;
	private int disknummer;
	private Float popularitet;
	private Float lengde;

	public String getLengdeFormatert() {
		if (lengde == null)
			return "0:00";

		int lengdeInt = lengde.intValue();
		return lengdeInt / 60 + ":" + ((lengdeInt % 60) < 10 ? "0" : "") + (lengdeInt % 60);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + disknummer;
		result = prime * result + ((lengde == null) ? 0 : lengde.hashCode());
		result = prime * result + ((navn == null) ? 0 : navn.hashCode());
		result = prime * result + ((popularitet == null) ? 0 : popularitet.hashCode());
		result = prime * result + spornummer;
		result = prime * result + ((spotifyURI == null) ? 0 : spotifyURI.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object other) {
		if (other == null)
			return false;
		if (!(other instanceof Spor))
			return false;

		Spor otherS = (Spor) other;
		if (spotifyURI.equals(otherS.getSpotifyURI()))
			return true;

		return false;
	}

	public String getNavn() {
		return navn;
	}

	public void setNavn(String navn) {
		this.navn = navn;
	}

	public int getSpornummer() {
		return spornummer;
	}

	public void setSpornummer(int spornummer) {
		this.spornummer = spornummer;
	}

	public Integer getDisknummer() {
		return disknummer;
	}

	public void setDisknummer(Integer disknummer) {
		this.disknummer = disknummer;
	}

	public Float getPopularitet() {
		return popularitet;
	}

	public void setPopularitet(Float popularitet) {
		this.popularitet = popularitet;
	}

	public Float getLengde() {
		return lengde;
	}

	public void setLengde(Float lengde) {
		this.lengde = lengde;
	}

	public String getSpotifyURI() {
		return spotifyURI;
	}

	public void setSpotifyURI(String spotifyURI) {
		this.spotifyURI = spotifyURI;
	}

}
