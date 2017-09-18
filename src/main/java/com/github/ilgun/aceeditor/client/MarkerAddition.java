package com.github.ilgun.aceeditor.client;

public class MarkerAddition {
	private final AceMarker marker;
	private final String startContext;
	private final String endContext;
	public MarkerAddition(AceMarker marker, String text2) {
		this.marker = marker;
		
		// TODO
		startContext = "";
		endContext = "";
	}
	private MarkerAddition(AceMarker marker, String startContext, String endContext) {
		this.marker = marker;
		this.startContext = startContext;
		this.endContext = endContext;
	}
	public AceMarker getAdjustedMarker(String text) {
		// TODO adjust
		return marker;
	}
	public TransportDiff.TransportMarkerAddition asTransport() {
		return new TransportDiff.TransportMarkerAddition(marker.asTransport(), startContext, endContext);
	}
	public static MarkerAddition fromTransport(TransportDiff.TransportMarkerAddition ta) {
		return new MarkerAddition(AceMarker.fromTransport(ta.marker), ta.startContext, ta.endContext);
	}
	

}
