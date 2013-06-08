package org.rakvag.hylla.domain;

import java.util.LinkedHashMap;
import java.util.Map;

public enum Nasjonalitet {
	NORSK("Norsk"), 
	SVENSK("Svensk"), 
	DANSK("Dansk"), 
	BRITISK("Britisk"), 
	FRANSK("Fransk"),
	AMERIKANSK("Amerikansk"), 
	ANNEN("Annen nasjonalitet"),
	IKKE_SATT("Uten nasjonalitet"); 

	private String dekode;

	private Nasjonalitet(String dekode) {
		this.dekode = dekode;
	}

	public String getDekode() {
		return dekode;
	}

	public static Map<String, String> lagNasjonalitetMap() {
		Map<String, String> nasjonaliteter = new LinkedHashMap<String, String>();
		for (Nasjonalitet nasjonalitet : Nasjonalitet.values()) {
			nasjonaliteter.put(nasjonalitet.name(), nasjonalitet.getDekode());
		}
		return nasjonaliteter;
	}

}
