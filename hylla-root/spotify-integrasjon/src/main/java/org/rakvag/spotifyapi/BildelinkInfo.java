package org.rakvag.spotifyapi;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class BildelinkInfo {
	public String spotifyURI;
	public String bildelink;

	public BildelinkInfo(String spotifyURI, String bildelink) {
		this.spotifyURI = spotifyURI;
		this.bildelink = bildelink;
	}

	public static Map<String, String> lagMap(Set<BildelinkInfo> infoer) {
		Map<String, String> map = new HashMap<String, String>();

		for (BildelinkInfo info : infoer) {
			if (info != null)
				map.put(info.spotifyURI, info.bildelink);
		}
		
		return map;
	}
}
