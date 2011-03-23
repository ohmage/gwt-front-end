package edu.ucla.cens.AndWellnessVisualizations.client.widget;

import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.logging.Logger;

import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.NamedFrame;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class IFrameForm extends Composite {
	private static Logger _logger = Logger.getLogger(IFrameForm.class.getName());
	
	public static final String iFrameName = "iFrameId";
	
	private NamedFrame iFrame;
	private FormPanel form;
	private VerticalPanel formChildren;
	
	/* Save this so we can count its length */
	private final String url;
	
	/**
	 * Create a vertical panel with a hidden form and an iframe target for the form.
	 */
	public IFrameForm(String url) {
		this.url = url;
		
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
	 * Returns the total length in characters of the POST that would results from
	 * a submit.  Counts the length of every name/value plus two for the & and =, plus
	 * the length of the base url.
	 * 
	 * @return The total character count of the POST.
	 */
	public int length() {
		int length = 0;
		
		// Add the URL
		length += URL.encode(url).length();
		
		// Iterate over the name.value pairs
		Iterator<Widget> formIter = formChildren.iterator();
		while(formIter.hasNext()) {
			TextBox textBox = (TextBox)formIter.next();
			
			length += textBox.getName().length();
			length += URL.encode(textBox.getValue()).length();

			// One for the =, one for the &
			length += 2;
		}
		
		return length;
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
	
	/**
	 * Add or remove the iFrame border
	 */
	public void setBorder(boolean border) {
		if (border) {
			iFrame.getElement().setAttribute("frameborder", "1");
		}
		else {
			iFrame.getElement().setAttribute("frameborder", "0");
		}
	}
}
