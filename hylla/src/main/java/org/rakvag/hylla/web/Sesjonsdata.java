package org.rakvag.hylla.web;

import java.io.Serializable;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("session")
public class Sesjonsdata implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Long hylleId = 1L;

	public Long getHylleId() {
		return hylleId;
	}

	public void setHylleId(Long hylleId) {
		this.hylleId = hylleId;
	}

	
}
