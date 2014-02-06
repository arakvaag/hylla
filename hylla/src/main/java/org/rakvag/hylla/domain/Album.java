package org.rakvag.hylla.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;

@Entity
public class Album implements SpotifyEntitet, DBEntitet, Comparable<Album> {

	private final static int MAX_LENGDE_KORTNAVN = 18;
	private final static int MAX_LENGDE_MOBILNAVN = 17;
	private final static int MIN_LENGDE_VANLIG_ALBUM = 20 * 60;

	@Id
	@SequenceGenerator(name="album_id_seq", sequenceName="album_id_seq", allocationSize=10)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="album_id_seq")	
	private Long id;

	@ManyToOne()
	@JoinColumn(name = "artistId", nullable = false)
	private Artist artist;
	@Column(nullable = false)
	private String navn;
	@Column(nullable = false, unique = true)
	private String spotifyURI;
	@Column(nullable = true)
	private Integer aar;
	@Enumerated(EnumType.STRING)
	@Column(nullable = true)
	private Sjanger sjanger;
	@Column(nullable = true)
	private String coverartlink;
	@Column(nullable = false)
	private boolean tilgjengeligINorge;
	@Column(nullable = true)
	private Double lengde;
	@Column(nullable = false)
	private float popularitet;

	@Transient
	private Set<Spor> spor;
	@Transient
	private boolean erPaaHylle;

	public String getKortnavn() {
		if (!StringUtils.isBlank(navn) && navn.length() > MAX_LENGDE_KORTNAVN)
			return navn.substring(0, MAX_LENGDE_KORTNAVN - 3).trim() + "...";

		return navn;
	}
	
	public String getMobilnavn() {
		String retur = navn;
		if (!StringUtils.isBlank(navn) && navn.length() > MAX_LENGDE_MOBILNAVN)
			retur = navn.substring(0, MAX_LENGDE_MOBILNAVN - 3).trim() + "...";

		return retur;
	}	

	public List<Spor> getSorterteSpor() {
		List<Spor> sorterteSpor = new ArrayList<Spor>(spor);
		Comparator<Spor> sporComparator = new Comparator<Spor>() {

			@Override
			public int compare(Spor o1, Spor o2) {
				if (o1.getDisknummer() > o2.getDisknummer())
					return 1;
				if (o1.getDisknummer() < o2.getDisknummer())
					return -1;
				if (o1.getDisknummer().equals(o2.getDisknummer())) {
					if (o1.getSpornummer() > o2.getSpornummer())
						return 1;
					if (o1.getSpornummer() < o2.getSpornummer())
						return -1;
					if (o1.getSpornummer() == o2.getSpornummer())
						return 0;
				}
				return 0;
			}
		};
		Collections.sort(sorterteSpor, sporComparator);
		return sorterteSpor;
	}

	public boolean erEtKortAlbum() {
		return lengde.intValue() < MIN_LENGDE_VANLIG_ALBUM;
	}

	public String getLengdeFormatert() {
		if (lengde == 0D)
			return "";
		else {
			int lengdeInt = lengde.intValue();
			return lengdeInt / 60 + ":" + ((lengdeInt % 60) < 10 ? "0" : "") + (lengdeInt % 60);
		}
	}

	public void setErPaaHylle(boolean erPaaHylle) {
		this.erPaaHylle = erPaaHylle;
	}
	
	@Override
	public int compareTo(Album other) {
		if (this.equals(other))
			return 0;
		
		int orden = Float.compare(this.popularitet, other.getPopularitet()) * -1;
		if (orden != 0)
			return orden;
		else { //Hvis popularitet er lik for begge, sorter på spotifyURI for å få konsekvent resultat
			return this.spotifyURI.compareTo(other.getSpotifyURI());
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((aar == null) ? 0 : aar.hashCode());
		result = prime * result + ((artist == null) ? 0 : artist.hashCode());
		result = prime * result + ((coverartlink == null) ? 0 : coverartlink.hashCode());
		result = prime * result + (erPaaHylle ? 1231 : 1237);
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((lengde == null) ? 0 : lengde.hashCode());
		result = prime * result + ((navn == null) ? 0 : navn.hashCode());
		result = prime * result + Float.floatToIntBits(popularitet);
		result = prime * result + ((sjanger == null) ? 0 : sjanger.hashCode());
		result = prime * result + ((spor == null) ? 0 : spor.hashCode());
		result = prime * result + ((spotifyURI == null) ? 0 : spotifyURI.hashCode());
		result = prime * result + (tilgjengeligINorge ? 1231 : 1237);
		return result;
	}


	@Override
	public boolean equals(Object other) {
		if (other == null)
			return false;
		if (!(other instanceof Album))
			return false;

		Album otherA = (Album) other;
		if (spotifyURI.equals(otherA.getSpotifyURI()))
			return true;

		return false;
	}

	public Long getId() {
		return id;
	}

	public Artist getArtist() {
		return artist;
	}

	public void setArtist(Artist artist) {
		this.artist = artist;
	}

	public String getNavn() {
		return navn;
	}

	public void setNavn(String navn) {
		this.navn = navn;
	}

	public String getSpotifyURI() {
		return spotifyURI;
	}

	public void setSpotifyURI(String spotifyURI) {
		this.spotifyURI = spotifyURI;
	}

	public String getCoverartlink() {
		return coverartlink;
	}

	public void setCoverartlink(String coverartlink) {
		this.coverartlink = coverartlink;
	}

	public void setAar(Integer aar) {
		this.aar = aar;
	}

	public Integer getAar() {
		return aar;
	}

	public Sjanger getSjanger() {
		return sjanger;
	}

	public void setSjanger(Sjanger sjanger) {
		this.sjanger = sjanger;
	}

	public Set<Spor> getSpor() {
		return spor;
	}

	public void setSpor(Set<Spor> spor) {
		this.spor = spor;
	}

	public boolean isTilgjengeligINorge() {
		return tilgjengeligINorge;
	}

	public void setTilgjengeligINorge(boolean tilgjengeligINorge) {
		this.tilgjengeligINorge = tilgjengeligINorge;
	}

	public boolean isErPaaHylle() {
		return erPaaHylle;
	}

	public Double getLengde() {
		return lengde;
	}

	public void setLengde(Double lengde) {
		this.lengde = lengde;
	}

	public float getPopularitet() {
		return popularitet;
	}

	public void setPopularitet(float popularitet) {
		this.popularitet = popularitet;
	}

}
