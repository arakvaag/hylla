package org.rakvag.hylla.domain;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;

import org.apache.commons.lang3.StringUtils;

@Entity
public class Artist implements SpotifyEntitet {

	public static final String URI_VARIOUS_ARTISTS_ARTIST = "spotify:artist:VA";

	private final static int MAX_LENGDE_KORTNAVN = 17;
	private final static int MAX_LENGDE_MOBILNAVN = 16;
	
	@Id
	@SequenceGenerator(name="artist_id_seq", sequenceName="artist_id_seq", allocationSize=10)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="artist_id_seq")	
	private Long id;

	@Column(nullable = false)
	private String navn;
	@Column(nullable = false, unique = true)
	private String spotifyURI;
	@Column(nullable = true)
	private String bildelink;

	@Column(nullable = false)
	private boolean erAlleAlbumLastet;
	@OneToMany(mappedBy = "artist")
	private Set<Album> album;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Sjanger defaultSjanger = Sjanger.IKKE_SATT;
	
	public String getKortnavn() {
		if (!StringUtils.isBlank(navn) && navn.length() > MAX_LENGDE_KORTNAVN)
			return navn.substring(0, MAX_LENGDE_KORTNAVN - 3).trim() + "...";

		return navn;
	}

	public String getMobilnavn() {
		if (!StringUtils.isBlank(navn) && navn.length() > MAX_LENGDE_MOBILNAVN)
			return navn.substring(0, MAX_LENGDE_MOBILNAVN - 3).trim() + "...";

		return navn;
	}	

	public Long getId() {
		return id;
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

	public Set<Album> getAlbum() {
		return album;
	}

	public void setAlbum(Set<Album> album) {
		this.album = album;
	}

	public String getBildelink() {
		return bildelink;
	}

	public void setBildelink(String bildelink) {
		this.bildelink = bildelink;
	}

	public boolean isErAlleAlbumLastet() {
		return erAlleAlbumLastet;
	}

	public void setErAlleAlbumLastet(boolean erAlleAlbumLastet) {
		this.erAlleAlbumLastet = erAlleAlbumLastet;
	}

	public Sjanger getDefaultSjanger() {
		return defaultSjanger;
	}

	public void setDefaultSjanger(Sjanger defaultSjanger) {
		this.defaultSjanger = defaultSjanger;
	}

}
