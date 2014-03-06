package org.rakvag.hylla.domain;

import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.SequenceGenerator;

@Entity
public class Hylle implements DBEntitet {

	@Id
	@SequenceGenerator(name="hylle_id_seq", sequenceName="hylle_id_seq", allocationSize=10)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="hylle_id_seq")	
	private Long id;

	@Column(unique = true, nullable = false)
	private String brukernavn;

	@Enumerated(EnumType.STRING)
	@Column(nullable = true)
	private Sjanger valgtSjanger;

	@Enumerated(EnumType.STRING)
	@Column(nullable = true)
	private Tidsperiode valgtTidsperiode;

	@Column(nullable = true)
	private String spotifyURIAapentAlbum;

	@ManyToMany(fetch=FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinTable(name = "HylleAlbum", joinColumns = @JoinColumn(name = "hyller_id"), inverseJoinColumns = @JoinColumn(name = "albumene_id"))
	private Collection<Album> albumene;

	public Long getId() {
		return id;
	}

	public String getBrukernavn() {
		return brukernavn;
	}

	public void setBrukernavn(String brukernavn) {
		this.brukernavn = brukernavn;
	}

	public Sjanger getValgtSjanger() {
		return valgtSjanger;
	}

	public void setValgtSjanger(Sjanger valgtSjanger) {
		this.valgtSjanger = valgtSjanger;
	}

	public Tidsperiode getValgtTidsperiode() {
		return valgtTidsperiode;
	}

	public void setValgtTidsperiode(Tidsperiode valgtTidsperiode) {
		this.valgtTidsperiode = valgtTidsperiode;
	}

	public String getSpotifyURIAapentAlbum() {
		return spotifyURIAapentAlbum;
	}

	public void setSpotifyURIAapentAlbum(String spotifyURIAapentAlbum) {
		this.spotifyURIAapentAlbum = spotifyURIAapentAlbum;
	}

	public Collection<Album> getAlbumene() {
		return albumene;
	}

}
