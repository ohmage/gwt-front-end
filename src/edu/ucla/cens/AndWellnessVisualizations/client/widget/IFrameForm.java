package edu.ucla.cens.AndWellnessVisualizations.client.widget;

import java.util.Iterator;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.NamedFrame;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class IFrameForm extends Composite {
	public static final String iFrameName = "iFrameId";
	
	private NamedFrame iFrame;
	private FormPanel form;
	private VerticalPanel formChildren;
	
	/**
	 * Create a vertical panel with a hidden form and an iframe target for the form.
	 */
	public IFrameForm(String url) {
		VerticalPanel panel = new VerticalPanel();
		
		// Setup and add the frame
		iFrame = new NamedFrame(iFrameName);
		panel.add(iFrame);
		
		// Setup and add the form
		form = new FormPanel(iFrame);
		// Move this URL into a constant
		form.setAction(url);
		form.setMethod(FormPanel.METHOD_POST);
		form.setVisible(false);
		panel.add(form);

		// Create the panel to hold the form's children
		formChildren = new VerticalPanel();
		form.add(formChildren);
		
		initWidget(panel);
	}
	
	/**
	 * Set the width and height of the frame in pixels.
	 * 
	 * @param width The width in pixels.
	 * @param height The height in pixels.
	 */
	public void setSize(int width, int height) {
		iFrame.setSize(width + "px", height + "px");
	}
	
	/**
	 * Add any data points into the frame form and submit.
	 */
	public void submit() {
		form.submit();
	}
	
	/**
	 * Add or update a name/value pair in the form.
	 */
	public void setNameValue(String name, String value) {
		boolean foundName = false;
		Iterator<Widget> formIter = formChildren.iterator();
		
		// See if the name is already in the form
		while(formIter.hasNext()) {
			TextBox textBox = (TextBox)formIter.next();
			
			if (textBox.getName().equals(name)) {
				textBox.setValue(value);
				foundName = true;
			}
		}
		
		// If not found, add it now
		if (!foundName) {
			TextBox newBox = new TextBox();
			newBox.setName(name);
			newBox.setValue(value);
			formChildren.add(newBox);
		}
	}
}
