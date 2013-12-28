package org.rakvag.hylla.domain;

import java.util.LinkedHashMap;
import java.util.Map;

public enum Sjanger {
	POP("Pop"), 
	ROCK("Rock"), 
	METAL("Metal"), 
	HIPHOP("Hiphop"), 
	SOUL_FUNK_RNB("Soul, Funk, RnB"), 
	ELECTRONICA("Electronica"), 
	JAZZ("Jazz"), 
	IKKE_SATT("Uten sjanger");
	
	private String dekode;
	
	private Sjanger(String dekode) {
		this.dekode = dekode;
	}
	
	public String getDekode() {
		return dekode;
	}
	
	public static Map<String, String> lagSjangerMap() {
		Map<String, String> sjangre = new LinkedHashMap<String, String>();
		for(Sjanger sjanger : Sjanger.values()) {
			sjangre.put(sjanger.name(), sjanger.getDekode());
		}		
		return sjangre;
	}
	
}
