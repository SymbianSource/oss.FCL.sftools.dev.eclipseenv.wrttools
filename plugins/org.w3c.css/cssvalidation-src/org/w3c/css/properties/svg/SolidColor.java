//
// $Id: SolidColor.java,v 1.3 2008-04-07 14:16:24 ylafon Exp $
// From Sijtsche de Jong (sy.de.jong@let.rug.nl)
//
// (c) COPYRIGHT 1995-2000  World Wide Web Consortium (MIT, INRIA, Keio University)
// Please first read the full copyright statement at
// http://www.w3.org/Consortium/Legal/copyright-software-19980720

package org.w3c.css.properties.svg;

import java.util.Vector;

import org.w3c.css.parser.CssStyle;
import org.w3c.css.properties.css1.CssColor;
import org.w3c.css.properties.css1.CssProperty;
import org.w3c.css.util.ApplContext;
import org.w3c.css.util.InvalidParamException;
import org.w3c.css.values.CssExpression;
import org.w3c.css.values.CssFunction;
import org.w3c.css.values.CssIdent;
import org.w3c.css.values.CssNumber;
import org.w3c.css.values.CssOperator;
import org.w3c.css.values.CssValue;

/**
 *  <P>
 *  <EM>Value:</EM> currentColor || &lt;color&gt;[icc-color(
 *   &lt;name&gt;[&lt;icccolorvalue&gt;]*)] || inherit<BR>
 *  <EM>Initial:</EM>none<BR>
 *  <EM>Applies to:</EM>all elements<BR>
 *  <EM>Inherited:</EM>no<BR>
 *  <EM>Percentages:</EM>no<BR>
 *  <EM>Media:</EM>:visual
 */

public class SolidColor extends CssProperty implements CssOperator {

    CssValue solidColor;
    ApplContext ac;
    Vector values = new Vector();

    CssIdent currentColor = new CssIdent("currentColor");

    /**
     * Create a new SolidColor
     */
    public SolidColor() {
	//nothing to do
    }

    /**
     * Create a new SolidColor
     *
     * @param expression The expression for this property
     * @exception InvalidParamException Values are incorrect
     */
    public SolidColor(ApplContext ac, CssExpression expression,
	    boolean check) throws InvalidParamException {
	this.ac = ac;
	setByUser(); // tell this property is set by the user
	CssValue val = expression.getValue();
	boolean correct = true;
	String errorval = "";
	char op = expression.getOperator();

	if (val.equals(inherit)) {
	    solidColor = inherit;
	    expression.next();
	} else if (val.equals(currentColor)) {
	    solidColor = currentColor;
	    expression.next();
	} else {
	    try {
		CssColor color = new CssColor(ac, expression);
		values.addElement(val);
		//expression.next();
	    } catch (InvalidParamException e) {
		correct = false;
		errorval = val.toString();
	    }

	    op = expression.getOperator();
	    val = expression.getValue();

	    if (val != null) {

		if (val instanceof CssFunction) { // icc-color(<name>[,<icccolorvalue>]*)]
		    CssValue function = val;
		    if (!((CssFunction) val).getName().equals("icc-color")) {
			correct = false;
			errorval = val.toString();
		    } else {
			CssExpression params = ((CssFunction) val).getParameters();

			op = params.getOperator();
			val = params.getValue();

			if (!(val instanceof CssIdent)) {
			    correct = false;
			    errorval = val.toString();
			}

			params.next();
			op = params.getOperator();
			val = params.getValue();

			if (!params.end()) { // there are more parameters left
			    int counter = 0;

			    while ((op == COMMA || op == SPACE)
				    && (counter < (params.getCount() - 1) && correct == true)) {

				if ((!(val instanceof CssNumber)) || (((CssNumber) val).getValue() < 0)) {
				    correct = false;
				    errorval = val.toString();
				}

				params.next();
				counter++;
				val = params.getValue();
				op = params.getOperator();
			    }
			}

			if (correct) {
			    params.starts();
			    values.addElement(function);
			}
		    }
		} else {
		    correct = false;
		    errorval = val.toString();
		}
	    } else {
		correct = false;
		errorval = new String("");
	    }


	    expression.next();

	}

	if (!correct) {
	    throw new InvalidParamException("value", errorval, getPropertyName(), ac);
	}
    }

    public SolidColor(ApplContext ac, CssExpression expression)
	    throws InvalidParamException {
	this(ac, expression, false);
    }

    /**
     * Add this property to the CssStyle.
     *
     * @param style The CssStyle
     */
    public void addToStyle(ApplContext ac, CssStyle style) {
	if (((SVGBasicStyle) style).solidColor != null)
	    style.addRedefinitionWarning(ac, this);
	((SVGBasicStyle) style).solidColor = this;
    }

    /**
     * Get this property in the style.
     *
     * @param style The style where the property is
     * @param resolve if true, resolve the style to find this property
     */
    public CssProperty getPropertyInStyle(CssStyle style, boolean resolve) {
	if (resolve) {
	    return ((SVGBasicStyle) style).getSolidColor();
	} else {
	    return ((SVGBasicStyle) style).solidColor;
	}
    }

    /**
     * Compares two properties for equality.
     *
     * @param value The other property.
     */
    public boolean equals(CssProperty property) {
	return (property instanceof SolidColor &&
		solidColor.equals( ((SolidColor) property).solidColor));
    }

    /**
     * Returns the name of this property
     */
    public String getPropertyName() {
	return "solid-color";
    }

    /**
     * Returns the value of this property
     */
    public Object get() {
	if (solidColor != null)
	    return solidColor;
	else
	    return values;
    }

    /**
     * Returns true if this property is "softly" inherited
     */
    public boolean isSoftlyInherited() {
	return solidColor.equals(inherit);
    }

    /**
     * Returns a string representation of the object
     */
    public String toString() {
	if (solidColor != null) {
	    return solidColor.toString();
	} else {
	    String ret = "";
	    for (int i = 0; i < values.size(); i++) {
		ret += " " + values.elementAt(i).toString();
	    }
	    return ret;
	}
    }

    /**
     * Is the value of this property a default value
     * It is used by all macro for the function <code>print</code>
     */
    public boolean isDefault() {
	return false;
    }

}