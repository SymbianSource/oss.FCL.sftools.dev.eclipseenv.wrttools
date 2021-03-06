//
// $Id: OutlineColorATSC.java,v 1.2 2005-09-14 15:14:58 ylafon Exp $
// From Philippe Le Hegaret (Philippe.Le_Hegaret@sophia.inria.fr)
//
// (c) COPYRIGHT MIT and INRIA, 1997.
// Please first read the full copyright statement in file COPYRIGHT.html
/**
 *
 */
package org.w3c.css.properties.css2.user;

import org.w3c.css.parser.CssStyle;
import org.w3c.css.properties.css1.CssProperty;
import org.w3c.css.util.ApplContext;
import org.w3c.css.util.InvalidParamException;
import org.w3c.css.values.CssColor;
import org.w3c.css.values.CssExpression;
import org.w3c.css.values.CssIdent;
import org.w3c.css.values.CssValue;

/**
 * @version $Revision: 1.2 $
 */
public class OutlineColorATSC extends UserProperty {

    CssValue color;

    private static final CssIdent invert = new CssIdent("invert");

    /**
     * Create a new OutlineColorATSC
     */
    public OutlineColorATSC() {
	color = invert;
    }

    /**
     * Set the value of the property
     * @param expression The expression for this property
     * @exception InvalidParamException Values are incorrect
     */
    public OutlineColorATSC(ApplContext ac, CssExpression expression,
	    boolean check) throws InvalidParamException {

	if(check && expression.getCount() > 1) {
	    throw new InvalidParamException("unrecognize", ac);
	}

	CssValue val = expression.getValue();
	setByUser();

	ac.getFrame().addWarning("atsc", val.toString());

	if (val.equals(inherit)) {
	    color = inherit;
	    expression.next();
	} else if (val.equals(invert)) {
	    color = invert;
	    expression.next();
	} else if (val instanceof CssColor) {
	    color = val;
	    expression.next();
	} else if (val instanceof CssIdent) {
	    color = new CssColor(ac, (String) val.get());
	    expression.next();
	} else {
	    throw new InvalidParamException("value", val,
					    getPropertyName(), ac);
	}
    }

    public OutlineColorATSC(ApplContext ac, CssExpression expression)
	throws InvalidParamException {
	this(ac, expression, false);
    }

    /**
     * Returns the value of this property
     */
    public Object get() {
	return color;
    }

    /**
     * Returns true if this property is "softly" inherited
     * e.g. his value equals inherit
     */
    public boolean isSoftlyInherited() {
	return color.equals(inherit);
    }

    /**
     * Returns a string representation of the object.
     */
    public String toString() {
	return color.toString();
    }

    /**
     * Add this property to the CssStyle.
     *
     * @param style The CssStyle
     */
    public void addToStyle(ApplContext ac, CssStyle style) {
	OutlineATSC outline = ((Css2Style) style).outlineATSC;
	if (outline.color != null) {
	    style.addRedefinitionWarning(ac, this);
	}
	outline.color = this;
    }

    /**
     * Get this property in the style.
     *
     * @param style The style where the property is
     * @param resolve if true, resolve the style to find this property
     */
    public CssProperty getPropertyInStyle(CssStyle style, boolean resolve) {
	if (resolve) {
	    return ((Css2Style) style).getOutlineColorATSC();
	} else {
	    return ((Css2Style) style).outlineATSC.color;
	}
    }

    /**
     * Compares two properties for equality.
     *
     * @param value The other property.
     */
    public boolean equals(CssProperty property) {
	return (property instanceof OutlineColorATSC &&
		color.equals(((OutlineColorATSC) property).color));
    }

    /**
     * Returns the name of this property
     */
    public String getPropertyName() {
	return "outline-color";
    }

}
