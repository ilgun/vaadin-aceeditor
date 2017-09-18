package com.github.ilgun.aceeditor.client;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class AceDoc implements Serializable {
	private static final long serialVersionUID = 1L;

	private final String text;
	
	// key: markerId
	private final Map<String, AceMarker> markers;

	private final Set<AceAnnotation.RowAnnotation> rowAnnotations;

	private final Set<AceAnnotation.MarkerAnnotation> markerAnnotations;

	public AceDoc() {
		this("");
	}

	public AceDoc(String text) {
        this(text,
                Collections.<String, AceMarker> emptyMap(),
                Collections.<AceAnnotation.RowAnnotation>emptySet(),
                Collections.<AceAnnotation.MarkerAnnotation>emptySet());
	}
	
	public AceDoc(String text, Map<String, AceMarker> markers) {
        this(text, markers,
                Collections.<AceAnnotation.RowAnnotation>emptySet(),
                Collections.<AceAnnotation.MarkerAnnotation>emptySet());
	}

	public AceDoc(String text, Map<String, AceMarker> markers,
			Set<AceAnnotation.RowAnnotation> rowAnnotations,
			Set<AceAnnotation.MarkerAnnotation> markerAnnotations) {
        if (text == null) {
            text = "";
        }

		this.text = text;
		this.markers = markers;
		this.rowAnnotations = rowAnnotations;
		this.markerAnnotations = markerAnnotations;
	}
	
	public String getText() {
		return text;
	}
	
	public Map<String, AceMarker> getMarkers() {
		return Collections.unmodifiableMap(markers);
	}
	
	public Set<AceAnnotation.RowAnnotation> getRowAnnotations() {
		if (rowAnnotations==null) {
			return Collections.emptySet();
		}
		return Collections.unmodifiableSet(rowAnnotations);
	}
	
	public Set<AceAnnotation.MarkerAnnotation> getMarkerAnnotations() {
		if (markerAnnotations==null) {
			return Collections.emptySet();
		}
		return Collections.unmodifiableSet(markerAnnotations);
	}
	
	public boolean hasRowAnnotations() {
		return rowAnnotations != null;
	}
	
	public boolean hasMarkerAnnotations() {
		return markerAnnotations != null;
	}

	@Override
	public String toString() {
		return text + "\n/MARKERS: "+markers+"\nra:"+rowAnnotations+", ma:"+markerAnnotations;
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof AceDoc) {
			AceDoc od = (AceDoc) other;
			return textEquals(text, od.text) &&
					Util.sameMaps(this.markers, od.markers) &&
					Util.sameSets(this.markerAnnotations, od.markerAnnotations) &&
					Util.sameSets(this.rowAnnotations, od.rowAnnotations);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return getText().hashCode();
	}

    public boolean textEquals(String a, String b) {
        return a == null ? b == null : a.equals(b);
    }

	public AceDoc withText(String newText) {
		return new AceDoc(newText, markers, rowAnnotations, markerAnnotations);
	}

	public TransportDoc asTransport() {
		TransportDoc td = new TransportDoc();
		td.text = text;

		td.markers = getTransportMarkers();
		td.markerAnnotations = getTransportMarkerAnnotations();
		td.rowAnnotations = getTransportRowAnnotations();

		return td;
	}

	/* TODO private */ Map<String, TransportDoc.TransportMarker> getTransportMarkers() {
		HashMap<String, TransportDoc.TransportMarker> ms = new HashMap<String, TransportDoc.TransportMarker>(
				markers.size());
		for (Entry<String, AceMarker> e : markers.entrySet()) {
			ms.put(e.getKey(), e.getValue().asTransport());
		}
		return ms;
	}
	

	private Set<TransportDoc.TransportRowAnnotation> getTransportRowAnnotations() {
		if (rowAnnotations==null) {
			return null;
		}
		HashSet<TransportDoc.TransportRowAnnotation> anns = new HashSet<TransportDoc.TransportRowAnnotation>(
				rowAnnotations.size());
		for (AceAnnotation.RowAnnotation ra : rowAnnotations) {
			anns.add(ra.asTransport());
		}
		return anns;
	}

	private Set<TransportDoc.TransportMarkerAnnotation> getTransportMarkerAnnotations() {
		if (markerAnnotations==null) {
			return null;
		}
		HashSet<TransportDoc.TransportMarkerAnnotation> anns = new HashSet<TransportDoc.TransportMarkerAnnotation>(
				markerAnnotations.size());
		for (AceAnnotation.MarkerAnnotation ma : markerAnnotations) {
			anns.add(ma.asTransport());
		}
		return anns;
	}

	public static AceDoc fromTransport(TransportDoc doc) {
		String text = doc.text;
		Map<String, AceMarker> markers = markersFromTransport(doc.markers, doc.text);
		Set<AceAnnotation.RowAnnotation> rowAnnotations = rowAnnotationsFromTransport(doc.rowAnnotations);
		Set<AceAnnotation.MarkerAnnotation> markerAnnotations = markerAnnotationsFromTransport(doc.markerAnnotations);
		return new AceDoc(text, markers, rowAnnotations, markerAnnotations);
	}

	private static Map<String, AceMarker> markersFromTransport(
            Map<String, TransportDoc.TransportMarker> markers, String text) {
		HashMap<String, AceMarker> ms = new HashMap<String, AceMarker>();
		for (Entry<String, TransportDoc.TransportMarker> e : markers.entrySet()) {
			ms.put(e.getKey(), AceMarker.fromTransport(e.getValue()));
		}
		return ms;
	}

	private static Set<AceAnnotation.MarkerAnnotation> markerAnnotationsFromTransport(
			Set<TransportDoc.TransportMarkerAnnotation> markerAnnotations) {
		if (markerAnnotations==null) {
			return null;
		}
		HashSet<AceAnnotation.MarkerAnnotation> anns = new HashSet<AceAnnotation.MarkerAnnotation>(markerAnnotations.size());
		for (TransportDoc.TransportMarkerAnnotation ta : markerAnnotations) {
			anns.add(ta.fromTransport());
		}
		return anns;
	}

	private static Set<AceAnnotation.RowAnnotation> rowAnnotationsFromTransport(
			Set<TransportDoc.TransportRowAnnotation> rowAnnotations) {
		if (rowAnnotations==null) {
			return null;
		}
		HashSet<AceAnnotation.RowAnnotation> anns = new HashSet<AceAnnotation.RowAnnotation>(rowAnnotations.size());
		for (TransportDoc.TransportRowAnnotation ta : rowAnnotations) {
			anns.add(ta.fromTransport());
		}
		return anns;
	}
	
	// TODO?
	public AceDoc withMarkers(Set<AceMarker> newMarkers) {
		HashMap<String, AceMarker> markers2 = new HashMap<String, AceMarker>(newMarkers.size());
		for (AceMarker m : newMarkers) {
			markers2.put(m.getMarkerId(), m);
		}
		return new AceDoc(text, markers2, rowAnnotations, markerAnnotations);
	}
	
	public AceDoc withMarkers(Map<String, AceMarker> newMarkers) {
		return new AceDoc(text, newMarkers, rowAnnotations, markerAnnotations);
	}
	public AceDoc withAdditionalMarker(AceMarker marker) {
		HashMap<String, AceMarker> markers2 = new HashMap<String, AceMarker>(markers);
		markers2.put(marker.getMarkerId(), marker);
		return new AceDoc(text, markers2, rowAnnotations, markerAnnotations);
	}
	public AceDoc withAdditionalMarkers(Map<String, AceMarker> addMarkers) {
		HashMap<String, AceMarker> newMarkers = new HashMap<String, AceMarker>(markers);
		newMarkers.putAll(addMarkers);
		return new AceDoc(text, newMarkers, rowAnnotations, markerAnnotations);
	}

	public AceDoc withoutMarker(String markerId) {
		HashMap<String, AceMarker> markers2 = new HashMap<String, AceMarker>(markers);
		markers2.remove(markerId);
		return new AceDoc(text, markers2, rowAnnotations, markerAnnotations);
	}

	public AceDoc withoutMarkers() {
		Map<String, AceMarker> noMarkers = Collections.emptyMap();
		return new AceDoc(text, noMarkers, rowAnnotations, markerAnnotations);
	}
	
	public AceDoc withoutMarkers(Set<String> without) {
		Map<String, AceMarker> newMarkers = new HashMap<String, AceMarker>(markers);
		for (String m : without) {
			newMarkers.remove(m);
		}
		return new AceDoc(text, newMarkers, rowAnnotations, markerAnnotations);
	}

	public AceDoc withRowAnnotations(Set<AceAnnotation.RowAnnotation> ranns) {
		return new AceDoc(text, markers, ranns, markerAnnotations);
	}
	
	public AceDoc withMarkerAnnotations(Set<AceAnnotation.MarkerAnnotation> manns) {
		return new AceDoc(text, markers, rowAnnotations, manns);
	}

	public AceDoc withAdditionalMarkerAnnotation(AceAnnotation.MarkerAnnotation mann) {
		HashSet<AceAnnotation.MarkerAnnotation> manns = markerAnnotations==null?new HashSet<AceAnnotation.MarkerAnnotation>():new HashSet<AceAnnotation.MarkerAnnotation>(markerAnnotations);
		manns.add(mann);
		return new AceDoc(text, markers, rowAnnotations, manns);
	}
	
	public AceDoc withAdditionalRowAnnotation(AceAnnotation.RowAnnotation rann) {
		HashSet<AceAnnotation.RowAnnotation> ranns = rowAnnotations==null?new HashSet<AceAnnotation.RowAnnotation>():new HashSet<AceAnnotation.RowAnnotation>(rowAnnotations);
		ranns.add(rann);
		return new AceDoc(text, markers, ranns, markerAnnotations);
	}
}