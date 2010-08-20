//
// $Id: CssColumnGap.java,v 1.3 2009-12-15 17:38:46 ylafon Exp $
// From Sijtsche de Jong (sy.de.jong@let.rug.nl)
//
// COPYRIGHT (c) 1995-2000 World Wide Web Consortium, (MIT, INRIA, Keio University)
// Please first read the full copyright statement at
// http://www.w3.org/Consortium/Legal/copyright-software-19980720

package org.w3c.css.properties.css3;

import org.w3c.css.parser.CssStyle;
import org.w3c.css.properties.css1.CssProperty;
import org.w3c.css.util.ApplContext;
import org.w3c.css.util.InvalidParamException;
import org.w3c.css.values.CssExpression;
import org.w3c.css.values.CssIdent;
import org.w3c.css.values.CssLength;
import org.w3c.css.values.CssNumber;
import org.w3c.css.values.CssPercentage;
import org.w3c.css.values.CssTypes;
import org.w3c.css.values.CssValue;

/**
 *  <P>
 *  <EM>Value:</EM> &lt;length&gt; || normal <BR>
 *  <EM>Initial:</EM>normal<BR>
 *  <EM>Applies to:</EM>multicol elements<BR>
 *  <EM>Inherited:</EM>no<BR>
 *  <EM>Percentages:</EM>no<BR>
 *  <EM>Media:</EM>:visual
 *  <EM>Computed value: absolute length or 'normal'
 *  <P>The ‘column-gap’ property sets the gap between columns. 
 *  If there is a column rule between columns, it will appear in the 
 *  middle of the gap.
 *  The ‘normal’ value is UA-specific. A value of ‘1em’ is suggested.
 *  Column gaps cannot be negative.
 *
 */

public class CssColumnGap extends CssProperty {

    CssValue columngap;

    static CssIdent normal;
    static {
	normal = new CssIdent("normal");
    }
    /**
     * Create a new CssColumnGap
     */
    public CssColumnGap() {
	columngap = normal;
    }

    /**
     * Create a new CssColumnGap
     */
    public CssColumnGap(ApplContext ac, CssExpression expression,
			boolean check) throws InvalidParamException {
	setByUser();
	CssValue val = expression.getValue();
	Float value;

	switch(val.getType()) {
	case CssTypes.CSS_NUMBER:
	    val = ((CssNumber)val).getLength();
	case CssTypes.CSS_LENGTH:
	    value = (Float) ((CssLength)val).get();
	    if (value == null || value.floatValue() < 0.0) {
		throw new InvalidParamException("negative-value", 
						expression.getValue(),
						getPropertyName(), ac);
	    }
	    columngap = val;
	    break;
	case CssTypes.CSS_IDENT:
	    if (normal.equals(val)) {
		columngap = normal;
		break;
	    }
	    if (inherit.equals(val)) {
		columngap = inherit;
		break;
	    }
	default:
	    throw new InvalidParamException("value", expression.getValue(),
					    getPropertyName(), ac);
	}
	expression.next();
    }

    public CssColumnGap(ApplContext ac, CssExpression expression)
	throws InvalidParamException {
	this(ac, expression, false);
    }

    /**
     * Add this property to the CssStyle.
     *
     * @param style The CssStyle
     */
    public void addToStyle(ApplContext ac, CssStyle style) {
	if (((Css3Style) style).cssColumnGap != null)
	    style.addRedefinitionWarning(ac, this);
	((Css3Style) style).cssColumnGap = this;

    }

    /**
     * Get this property in the style.
     *
     * @param style The style where the property is
     * @param resolve if true, resolve the style to find this property
     */
    public CssProperty getPropertyInStyle(CssStyle style, boolean resolve) {
	if (resolve) {
	    return ((Css3Style) style).getColumnGap();
	} else {
	    return ((Css3Style) style).cssColumnGap;
	}
    }

    /**
     * Compares two properties for equality.
     *
     * @param value The other property.
     */
    public boolean equals(CssProperty property) {
	return (property instanceof CssColumnGap &&
		columngap.equals( ((CssColumnGap) property).columngap));
    }

    /**
     * Returns the name of this property
     */
    public String getPropertyName() {
	return "column-gap";
    }

    /**
     * Returns the value of this property
     */
    public Object get() {
	return columngap;
    }

    /**
     * Returns true if this property is "softly" inherited
     */
    public boolean isSoftlyInherited() {
	return columngap.equals(inherit);
    }

    /**
     * Returns a string representation of the object
     */
    public String toString() {
	return columngap.toString();
    }

    /**
     * Is the value of this property a default value
     * It is used by all macro for the function <code>print</code>
     */
    public boolean isDefault() {
	return (columngap == normal);
    }

}
