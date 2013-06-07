package org.rakvag.hylla.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;

@Entity
public class Album implements SpotifyEntitet, Entitet, Comparable<Album> {

	private final static int MAX_LENGDE_KORTNAVN = 20;
	private final static int MIN_LENGDE_VANLIG_ALBUM = 20 * 60;

	@Id
	@GeneratedValue
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
	@ManyToMany(mappedBy = "albumene", cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	private Set<Hylle> hyller;
	@OneToMany(mappedBy = "album", cascade = CascadeType.ALL)
	private Set<Spor> spor;
	@Column(nullable = true)
	private String coverartlink;
	@Column(nullable = false)
	private boolean tilgjengeligINorge;
	@Column(nullable = true)
	private Double lengde;
	@Column(nullable = false)
	private float popularitet;

	@Transient
	private boolean erPaaHylle;

	public String getKortnavn() {
		if (!StringUtils.isBlank(navn) && navn.length() > MAX_LENGDE_KORTNAVN)
			return navn.substring(0, MAX_LENGDE_KORTNAVN - 3).trim() + "...";

		return navn;
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
				if (o1.getDisknummer() == o2.getDisknummer()) {
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

	public void setErPaaHylle(long hylleId) {
		erPaaHylle = false;
		if (hyller != null) {
			for (Hylle hylle : hyller) {
				if (hylle.getId() == hylleId) {
					erPaaHylle = true;
					break;
				}
			}
		}
	}

	@Override
	public int compareTo(Album other) {
		if (this.equals(other))
			return 0;
		
		return Float.compare(this.popularitet, other.getPopularitet()) * -1;
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

	public Set<Hylle> getHyller() {
		return hyller;
	}

	public void setHyller(Set<Hylle> hyller) {
		this.hyller = hyller;
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
