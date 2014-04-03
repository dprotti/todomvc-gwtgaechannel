package com.todomvc.client;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.gwt.user.client.ui.TextBox;

public class TextBoxWithPlaceholder extends TextBox {

    /**
     * Sets the placeholder for this textbox.
     * 
     * @param value
     *            the placeholder value
     */
    public void setPlaceholder(String value) {
        checkNotNull(value);
        getElement().setAttribute("placeholder", value);
    }

    /**
     * Gets the placeholder for this textbox.
     */
    public String getPlaceholder() {
        return getElement().getAttribute("placeholder");
    }
}
