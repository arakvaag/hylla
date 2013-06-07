package org.rakvag.hylla.domain;

import java.util.LinkedHashMap;
import java.util.Map;

public enum Tidsperiode {
	FOER_80("Før 1980"), I_80("80-tallet"), I_90("90-tallet"), I_00("(20)00-tallet"), ETTER_00("Etter 2010");
	
	public static Tidsperiode hentTidsperiode(int aar) {
		if (aar < 1980)
			return FOER_80;
		else if (aar < 1990)
			return I_80;
		else if (aar < 2000)
			return I_90;
		else if (aar < 2010)
			return I_00;
		else 
			return ETTER_00;
	}
	
	private String dekode;
	
	private Tidsperiode(String dekode) {
		this.dekode = dekode;
	}
	
	public String getDekode() {
		return dekode;
	}
	
	public static Map<String, String> lagTidsperiodeMap() {
		Map<String, String> tidsperioder = new LinkedHashMap<String, String>();
		for(Tidsperiode periode : Tidsperiode.values()) {
			tidsperioder.put(periode.name(), periode.getDekode());
		}		
		return tidsperioder;
	}
		
}
