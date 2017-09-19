package com.github.ilgun.aceeditor;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.github.ilgun.aceeditor.widgetset.client.AceAnnotation;
import com.github.ilgun.aceeditor.widgetset.client.AceDoc;
import com.github.ilgun.aceeditor.widgetset.client.AceMarker;
import com.github.ilgun.aceeditor.widgetset.client.MarkerSetDiff;
import com.github.ilgun.aceeditor.widgetset.client.SetDiff;
import com.github.ilgun.aceeditor.widgetset.client.TransportDiff;
import com.github.ilgun.aceeditor.widgetset.client.TransportDoc;

import name.fraser.neil.plaintext.diff_match_patch;
import name.fraser.neil.plaintext.diff_match_patch.Patch;


public class ServerSideDocDiff {
	
	// We could use ThreadLocal but that causes a (valid) complaint
	// of memory leak by Tomcat. Creating a new diff_match_patch every
	// time (in getDmp()) fixes that. The creation is not a heavy operation so this it's ok.
	/*
	private static final ThreadLocal <diff_match_patch> dmp = 
	         new ThreadLocal <diff_match_patch> () {
	             @Override protected diff_match_patch initialValue() {
	                 return new diff_match_patch();
	         }
	     };
	*/
	
	private static diff_match_patch getDmp() {
		return new diff_match_patch();
	}
	
	private final LinkedList<Patch> patches;
	private final MarkerSetDiff markerSetDiff;
	private final SetDiff<AceAnnotation.RowAnnotation,TransportDoc.TransportRowAnnotation> rowAnnDiff;
	private final SetDiff<AceAnnotation.MarkerAnnotation,TransportDoc.TransportMarkerAnnotation> markerAnnDiff;
	
	public static ServerSideDocDiff diff(AceDoc doc1, AceDoc doc2) {
		LinkedList<Patch> patches = getDmp().patch_make(doc1.getText(), doc2.getText());
		MarkerSetDiff msd = MarkerSetDiff.diff(doc1.getMarkers(), doc2.getMarkers(), doc2.getText());
		SetDiff<AceAnnotation.RowAnnotation,TransportDoc.TransportRowAnnotation> rowAnnDiff =
				diffRA(doc1.getRowAnnotations(), doc2.getRowAnnotations());		
		SetDiff<AceAnnotation.MarkerAnnotation,TransportDoc.TransportMarkerAnnotation> markerAnnDiff =
				diffMA(doc1.getMarkerAnnotations(), doc2.getMarkerAnnotations());
		return new ServerSideDocDiff(patches, msd, rowAnnDiff, markerAnnDiff);
	}
	
	public static ServerSideDocDiff diff(String text1, String text2) {
		LinkedList<Patch> patches = getDmp().patch_make(text1, text2);
		return new ServerSideDocDiff(patches);
	}
	

	// XXX Unnecessary copy-pasting
	private static SetDiff<AceAnnotation.MarkerAnnotation, TransportDoc.TransportMarkerAnnotation> diffMA(
			Set<AceAnnotation.MarkerAnnotation> anns1,
			Set<AceAnnotation.MarkerAnnotation> anns2) {
		if (anns2 == null && anns1 != null) {
			return null;
		}
		if (anns1==null) {
			anns1 = Collections.emptySet();
		}
		if (anns2==null) {
			anns2 = Collections.emptySet();
		}
		return new SetDiff.Differ<AceAnnotation.MarkerAnnotation,TransportDoc.TransportMarkerAnnotation>().diff(anns1, anns2);
	}

	// XXX Unnecessary copy-pasting
	private static SetDiff<AceAnnotation.RowAnnotation, TransportDoc.TransportRowAnnotation> diffRA(
			Set<AceAnnotation.RowAnnotation> anns1,
			Set<AceAnnotation.RowAnnotation> anns2) {
		if (anns2 == null && anns1 != null) {
			return null;
		}
		if (anns1==null) {
			anns1 = Collections.emptySet();
		}
		if (anns2==null) {
			anns2 = Collections.emptySet();
		}
		return new SetDiff.Differ<AceAnnotation.RowAnnotation,TransportDoc.TransportRowAnnotation>().diff(anns1, anns2);
	}

	
	public static ServerSideDocDiff fromTransportDiff(TransportDiff diff) {
		return new ServerSideDocDiff(
				(LinkedList<Patch>) getDmp().patch_fromText(diff.patchesAsString),
				MarkerSetDiff.fromTransportDiff(diff.markerSetDiff),
				rowAnnsFromTransport(diff.rowAnnDiff),
				markerAnnsFromTransport(diff.markerAnnDiff));
	}
	
	// XXX Unnecessary copy-pasting
	private static SetDiff<AceAnnotation.RowAnnotation,TransportDoc.TransportRowAnnotation> rowAnnsFromTransport(
			TransportDiff.TransportSetDiffForRowAnnotations rowAnnDiff) {
		return rowAnnDiff==null ? null : SetDiff.fromTransport(rowAnnDiff);
	}
	
	// XXX Unnecessary copy-pasting
	private static SetDiff<AceAnnotation.MarkerAnnotation,TransportDoc.TransportMarkerAnnotation> markerAnnsFromTransport(
			TransportDiff.TransportSetDiffForMarkerAnnotations markerAnnDiff) {
		return markerAnnDiff==null ? null :  SetDiff.fromTransport(markerAnnDiff);
	}

	public ServerSideDocDiff(LinkedList<Patch> patches, MarkerSetDiff markerSetDiff,
			SetDiff<AceAnnotation.RowAnnotation,TransportDoc.TransportRowAnnotation> rowAnnDiff,
			SetDiff<AceAnnotation.MarkerAnnotation,TransportDoc.TransportMarkerAnnotation> markerAnnDiff) {
		this.patches = patches;
		this.markerSetDiff = markerSetDiff;
		this.rowAnnDiff = rowAnnDiff;
		this.markerAnnDiff = markerAnnDiff;
	}

	public ServerSideDocDiff(LinkedList<Patch> patches) {
		this(patches, null, null, null);
	}

	public String getPatchesString() {
		return getDmp().patch_toText(patches);
	}
	
	public List<Patch> getPatches() {
		return Collections.unmodifiableList(patches);
	}

	
	public AceDoc applyTo(AceDoc doc) {
		String text = (String)getDmp().patch_apply(patches, doc.getText())[0];
		Map<String, AceMarker> markers = markerSetDiff==null ? doc.getMarkers() : markerSetDiff.applyTo(doc.getMarkers(), text);
		Set<AceAnnotation.RowAnnotation> rowAnns = rowAnnDiff==null ? null : rowAnnDiff.applyTo(doc.getRowAnnotations());
		Set<AceAnnotation.MarkerAnnotation> markerAnns = markerAnnDiff==null ? null : markerAnnDiff.applyTo(doc.getMarkerAnnotations());
		return new AceDoc(text, markers, rowAnns, markerAnns);
	}
	
	public String applyTo(String text) {
		return (String)getDmp().patch_apply(patches, text)[0];
	}

	public TransportDiff asTransport() {
		TransportDiff d = new TransportDiff();
		d.patchesAsString = getPatchesString();
		d.markerSetDiff = markerSetDiff==null ? null : markerSetDiff.asTransportDiff();
		d.rowAnnDiff = rowAnnDiff==null ? null : rowAnnDiff.asTransportRowAnnotations();
		d.markerAnnDiff = markerAnnDiff==null ? null : markerAnnDiff.asTransportMarkerAnnotations();
		return d;
	}

	public boolean isIdentity() {
		return patches.isEmpty() && (markerSetDiff==null || markerSetDiff.isIdentity()); // TODO?
	}
	
	@Override
	public String toString() {
		return "---ServerSideDocDiff---\n" + getPatchesString()+"\n"+markerSetDiff+"\nrad:"+rowAnnDiff+", mad:"+markerAnnDiff;
	}


	public static ServerSideDocDiff newMarkersAndAnnotations(
			MarkerSetDiff msd, SetDiff<AceAnnotation.MarkerAnnotation,TransportDoc.TransportMarkerAnnotation> mad) {
		LinkedList<Patch> patches = new LinkedList<Patch>();
		SetDiff<AceAnnotation.RowAnnotation,TransportDoc.TransportRowAnnotation> rowAnnDiff =
				new SetDiff<AceAnnotation.RowAnnotation, TransportDoc.TransportRowAnnotation>();
		return new ServerSideDocDiff(patches, msd, rowAnnDiff, mad);
	}
	
}
