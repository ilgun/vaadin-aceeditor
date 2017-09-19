package com.github.ilgun.aceeditor.widgetset.client;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class SetDiff<V extends TransportDoc.TransportableAs<T>,T> {
	
	private final Set<V> added;
	private final Set<V> removed;

	public SetDiff(Set<V> added, Set<V> removed) {
		this.added = added;
		this.removed = removed;
	}

	public SetDiff() {
		added = Collections.emptySet();
		removed = Collections.emptySet();
	}

	public static class Differ<V extends TransportDoc.TransportableAs<T>,T> {
		public SetDiff<V,T> diff(Set<V> s1, Set<V> s2) {
			Set<V> removed = new HashSet<V>(s1);
			removed.removeAll(s2);
			
			Set<V> added = new HashSet<V>(s2);
			added.removeAll(s1);
			return new SetDiff<V,T>(added, removed);
		}
		
//		public SetDiff<V,T> fromTransport(TransportSetDiff<T> tsd) {
//			Set<V> added = new HashSet<V>();
//			for (T t : tsd.added) {
//				added.add(t.fromTransport());
//			}
//			Set<V> removed = new HashSet<V>();
//			for (T t : tsd.removed) {
//				removed.add(t.fromTransport());
//			}
//			return new SetDiff<V,T>(added, removed);
//		}
		

	}
	
	// XXX Unnecessary copy-pasting
	public static SetDiff<AceAnnotation.RowAnnotation,TransportDoc.TransportRowAnnotation> fromTransport(TransportDiff.TransportSetDiffForRowAnnotations tsd) {
		Set<AceAnnotation.RowAnnotation> added = new HashSet<AceAnnotation.RowAnnotation>();
		for (TransportDoc.TransportRowAnnotation t : tsd.added) {
			added.add(t.fromTransport());
		}
		Set<AceAnnotation.RowAnnotation> removed = new HashSet<AceAnnotation.RowAnnotation>();
		for (TransportDoc.TransportRowAnnotation t : tsd.removed) {
			removed.add(t.fromTransport());
		}
		return new SetDiff<AceAnnotation.RowAnnotation,TransportDoc.TransportRowAnnotation>(added, removed);
	}
	
	// XXX Unnecessary copy-pasting
	public static SetDiff<AceAnnotation.MarkerAnnotation,TransportDoc.TransportMarkerAnnotation> fromTransport(TransportDiff.TransportSetDiffForMarkerAnnotations tsd) {
		Set<AceAnnotation.MarkerAnnotation> added = new HashSet<AceAnnotation.MarkerAnnotation>();
		for (TransportDoc.TransportMarkerAnnotation t : tsd.added) {
			added.add(t.fromTransport());
		}
		Set<AceAnnotation.MarkerAnnotation> removed = new HashSet<AceAnnotation.MarkerAnnotation>();
		for (TransportDoc.TransportMarkerAnnotation t : tsd.removed) {
			removed.add(t.fromTransport());
		}
		return new SetDiff<AceAnnotation.MarkerAnnotation,TransportDoc.TransportMarkerAnnotation>(added, removed);
	}
	
	public Set<V> applyTo(Set<V> s1) {
		Set<V> s2 = new HashSet<V>(s1);
		s2.removeAll(removed);
		s2.addAll(added);
		return s2;
	}
	
//	public TransportSetDiff<T> asTransport() {
//		HashSet<T> ta = new HashSet<T>();
//		for (V v : added) {
//			ta.add(v.asTransport());
//		}
//		HashSet<T> tr = new HashSet<T>();
//		for (V v : removed) {
//			tr.add(v.asTransport());
//		}
//		return new TransportSetDiff<T>(ta, tr);
//	}
	
	// XXX Unnecessary copy-pasting
	public TransportDiff.TransportSetDiffForRowAnnotations asTransportRowAnnotations() {
		HashSet<TransportDoc.TransportRowAnnotation> ta = new HashSet<TransportDoc.TransportRowAnnotation>();
		for (V v : added) {
			ta.add((TransportDoc.TransportRowAnnotation) v.asTransport());
		}
		HashSet<TransportDoc.TransportRowAnnotation> tr = new HashSet<TransportDoc.TransportRowAnnotation>();
		for (V v : removed) {
			tr.add((TransportDoc.TransportRowAnnotation) v.asTransport());
		}
		return new TransportDiff.TransportSetDiffForRowAnnotations(ta, tr);
	}
	
	// XXX Unnecessary copy-pasting
	public TransportDiff.TransportSetDiffForMarkerAnnotations asTransportMarkerAnnotations() {
		HashSet<TransportDoc.TransportMarkerAnnotation> ta = new HashSet<TransportDoc.TransportMarkerAnnotation>();
		for (V v : added) {
			ta.add((TransportDoc.TransportMarkerAnnotation) v.asTransport());
		}
		HashSet<TransportDoc.TransportMarkerAnnotation> tr = new HashSet<TransportDoc.TransportMarkerAnnotation>();
		for (V v : removed) {
			tr.add((TransportDoc.TransportMarkerAnnotation) v.asTransport());
		}
		return new TransportDiff.TransportSetDiffForMarkerAnnotations(ta, tr);
	}
	
	@Override
	public String toString() {
		return "added: " + added + ", removed: " + removed;
	}
}
