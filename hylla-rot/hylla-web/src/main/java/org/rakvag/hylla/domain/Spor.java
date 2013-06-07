package org.rakvag.hylla.domain;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Spor implements SpotifyEntitet {

	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "albumId", nullable = false)
	private Album album;

	@Column(nullable = false)
	private String navn;
	@Column(nullable = false, unique = true)
	private String spotifyURI;
	@Column(nullable = false)
	private int spornummer;
	@Column(nullable = false)
	private int disknummer;
	@Column(nullable = true)
	private Float popularitet;
	@Column(nullable = true)
	private Float lengde;

	public String getLengdeFormatert() {
		if (lengde == null)
			return "0:00";

		int lengdeInt = lengde.intValue();
		return lengdeInt / 60 + ":" + ((lengdeInt % 60) < 10 ? "0" : "") + (lengdeInt % 60);
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

	public Album getAlbum() {
		return album;
	}

	public void setAlbum(Album album) {
		this.album = album;
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

	public Long getId() {
		return id;
	}

	public String getSpotifyURI() {
		return spotifyURI;
	}

	public void setSpotifyURI(String spotifyURI) {
		this.spotifyURI = spotifyURI;
	}

}
