package com.github.ilgun.aceeditor.widgetset.client;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

@SuppressWarnings("serial")
public class MarkerSetDiff implements Serializable {
	
	private final Map<String, MarkerAddition> added;
	private final Map<String, MarkerDiff> moved;
	private final Set<String> removed;
	
	public MarkerSetDiff(Map<String, MarkerAddition> added, Set<String> removed) {
		this.added = added;
		this.moved = Collections.emptyMap();
		this.removed = removed;
	}
	
	public MarkerSetDiff(Map<String, MarkerAddition> added,
			Map<String, MarkerDiff> moved, Set<String> removed) {
		this.added = added;
		this.moved = moved;
		this.removed = removed;
	}

	public static MarkerSetDiff diff(Map<String, AceMarker> m1, Map<String, AceMarker> m2, String text2) {

		Map<String, MarkerAddition> added = new HashMap<String, MarkerAddition>();
		Map<String, MarkerDiff> diffs = new HashMap<String, MarkerDiff>();
		for (Entry<String, AceMarker> e : m2.entrySet()) {
			AceMarker c1 = m1.get(e.getKey());
			if (c1 != null) {
				MarkerDiff d = MarkerDiff.diff(c1, e.getValue());
				if (!d.isIdentity()) {
					diffs.put(e.getKey(), d);
				}
			} else {
				added.put(e.getKey(), new MarkerAddition(e.getValue(), text2));
			}
		}

		Set<String> removedIds = new HashSet<String>(m1.keySet());
		removedIds.removeAll(m2.keySet());

		return new MarkerSetDiff(added, diffs, removedIds);
	}

//	public Map<String, TransportMarker> applyTo(Map<String, TransportMarker> markers) {
//		Map<String, TransportMarker> markers2 = new HashMap<String, TransportMarker>();
//		for (Entry<String, TransportMarkerAddition> e : added.entrySet()) {
//			TransportMarker adjusted = e.getValue().marker; // TODO: adjust
//			if (adjusted != null) {
//				markers2.put(e.getKey(), adjusted);
//			}
//		}
//
//		for (Entry<String, TransportMarker> e : markers.entrySet()) {
//			if (removed.contains(e.getKey())) {
//				continue;
//			}
//			TransportMarker m = e.getValue();
//			if (added.containsKey(e.getKey())) {
//				 m = added.get(e.getKey()).marker;
//			}
//			TransportMarkerDiff md = moved.get(e.getKey());
//			if (md != null) {
//				markers2.put(e.getKey(), md.applyTo(m));
//			} else {
//				markers2.put(e.getKey(), m);
//			}
//		}
//
//		return markers2;
//	}
	
	public Map<String, AceMarker> applyTo(Map<String, AceMarker> markers, String text2) {
		Map<String, AceMarker> markers2 = new HashMap<String, AceMarker>();
		for (Entry<String, MarkerAddition> e : added.entrySet()) {
			AceMarker adjusted = e.getValue().getAdjustedMarker(text2);
			if (adjusted != null) {
				markers2.put(e.getKey(), adjusted);
			}
		}

		for (Entry<String, AceMarker> e : markers.entrySet()) {
			if (removed.contains(e.getKey())) {
				continue;
			}
			AceMarker m = e.getValue();
			
			// ???
			if (markers2.containsKey(e.getKey())) {
				 m = markers2.get(e.getKey());
			}
			
			MarkerDiff md = moved.get(e.getKey());
			if (md != null) {
				markers2.put(e.getKey(), md.applyTo(m));
			} else {
				markers2.put(e.getKey(), m);
			}
		}

		return markers2;
	}

	@Override
	public String toString() {
		return "added: " + added + "\n" +
				"moved: " + moved + "\n" +
				"removed: " + removed;
	}

	public boolean isIdentity() {
		return added.isEmpty() && moved.isEmpty() && removed.isEmpty();
	}

	public TransportDiff.TransportMarkerSetDiff asTransportDiff() {
		TransportDiff.TransportMarkerSetDiff msd = new TransportDiff.TransportMarkerSetDiff();
		msd.added = getTransportAdded();
		msd.moved = getTransportMoved();
		msd.removed = getTransportRemoved();
		return msd;
	}

	private Map<String, TransportDiff.TransportMarkerAddition> getTransportAdded() {
		HashMap<String, TransportDiff.TransportMarkerAddition> ta = new HashMap<String, TransportDiff.TransportMarkerAddition>();
		for (Entry<String, MarkerAddition> e : added.entrySet()) {
			ta.put(e.getKey(), e.getValue().asTransport());
		}
		return ta;
	}

	private Map<String, TransportDiff.TransportMarkerDiff> getTransportMoved() {
		HashMap<String, TransportDiff.TransportMarkerDiff> ta = new HashMap<String, TransportDiff.TransportMarkerDiff>();
		for (Entry<String, MarkerDiff> e : moved.entrySet()) {
			ta.put(e.getKey(), e.getValue().asTransport());
		}
		return ta;
	}

	private Set<String> getTransportRemoved() {
		return removed; // No need for a defensive copy??
	}

	public static MarkerSetDiff fromTransportDiff(TransportDiff.TransportMarkerSetDiff td) {
		return new MarkerSetDiff(
				addedFromTransport(td.added),
				movedFromTransport(td.moved),
				removedFromTransport(td.removed));
	}

	private static Map<String, MarkerAddition> addedFromTransport(
			Map<String, TransportDiff.TransportMarkerAddition> added2) {
		HashMap<String, MarkerAddition> added = new HashMap<String, MarkerAddition>();
		for (Entry<String, TransportDiff.TransportMarkerAddition> e : added2.entrySet()) {
			added.put(e.getKey(), MarkerAddition.fromTransport(e.getValue()));
		}
		return added;
	}

	private static Map<String, MarkerDiff> movedFromTransport(
			Map<String, TransportDiff.TransportMarkerDiff> mt) {
		HashMap<String, MarkerDiff> moved = new HashMap<String, MarkerDiff>();
		for (Entry<String, TransportDiff.TransportMarkerDiff> e : mt.entrySet()) {
			moved.put(e.getKey(), MarkerDiff.fromTransport(e.getValue()));
		}
		return moved;
	}

	private static Set<String> removedFromTransport(Set<String> tr) {
		return tr; // No need for a defensive copy??
	}
	
}
