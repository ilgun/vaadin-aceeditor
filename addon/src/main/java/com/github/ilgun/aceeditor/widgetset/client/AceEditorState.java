package com.github.ilgun.aceeditor.widgetset.client;

import java.util.HashMap;
import java.util.Map;

import com.github.ilgun.aceeditor.widgetset.client.TransportDoc.TransportRange;
import com.vaadin.v7.shared.AbstractFieldState;

@SuppressWarnings("serial")
public class AceEditorState extends AbstractFieldState {
	
	public String changeMode = "LAZY";
	public int changeTimeout = 400;
	
	public String mode = "text";
	
	public String theme = "textmate";
	
	public TransportRange selection = null;
	
	public boolean listenToSelectionChanges = false;
	
	public boolean listenToFocusChanges = false;
	
	public boolean useWorker = true;
	
	public boolean wordwrap = false;

    public boolean showGutter = true;

    public boolean showPrintMargin = true;

    public boolean highlightActiveLine = true;

	public Map<String,String> config = new HashMap<String,String>();
	
	public int diff_editCost = 4;
	
	public TransportDoc initialValue = null;
	
	public int scrollToRow = -1;

    public String fontSize= "12px";

    public boolean highlightSelectedWord = true;

    public boolean showInvisibles = false;
    
    public boolean displayIndentGuides = true;
    
	public int tabSize = 4;

	public boolean softTabs = true;
}