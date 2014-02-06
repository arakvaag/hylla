package org.rakvag.hylla;

import static org.junit.Assert.*;

import org.junit.Test;

public class HyllaUtilsTest {

	@Test(expected=IllegalArgumentException.class)
	public void testFormaterStrengTilMaksLengde_MaksLengdeUnder3_KasterException() {
		assertEquals("", HyllaUtils.formaterStrengTilMaksLengde(null, 0));
	}

	@Test
	public void testFormaterStrengTilMaksLengde_NullString_ReturnererTomStreng() {
		assertEquals("", HyllaUtils.formaterStrengTilMaksLengde(null, 5));
	}
	
	@Test
	public void testFormaterStrengTilMaksLengde_MaksLengde3StrengUnderMaksLengde_ReturnererUendretStreng() {
		assertEquals("ab", HyllaUtils.formaterStrengTilMaksLengde("ab", 3));
	}

	@Test
	public void testFormaterStrengTilMaksLengde_StrengAkkuratMaksLengde_ReturnererUendretStreng() {
		assertEquals("abcde", HyllaUtils.formaterStrengTilMaksLengde("abcde", 5));
	}
	
	@Test
	public void testFormaterStrengTilMaksLengde_Streng1OverMaksLengde_ReturnererForkortetStreng() {
		assertEquals("ab...", HyllaUtils.formaterStrengTilMaksLengde("abcdef", 5));
	}
	
}
