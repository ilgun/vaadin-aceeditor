package com.github.ilgun.aceeditor.widgetset.client;


import com.vaadin.shared.communication.ServerRpc;

public interface SuggesterServerRpc extends ServerRpc {
	
	// TODO: it may not be necessary to send the whole text here
	// but I guess it's simplest...
	
	public void suggest(String text, TransportDoc.TransportRange selection);

	public void suggestionSelected(int index);
	
	
}
