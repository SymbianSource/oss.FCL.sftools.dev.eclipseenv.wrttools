//
// $Id: CssBackgroundRepeatCSS2.java,v 1.5 2009-02-11 21:41:10 ylafon Exp $
// From Philippe Le Hegaret (Philippe.Le_Hegaret@sophia.inria.fr)
//
// (c) COPYRIGHT MIT and INRIA, 1997.
// Please first read the full copyright statement in file COPYRIGHT.html
package org.w3c.css.properties.css1;

import org.w3c.css.parser.CssStyle;
import org.w3c.css.util.ApplContext;
import org.w3c.css.util.InvalidParamException;
import org.w3c.css.values.CssExpression;
import org.w3c.css.values.CssIdent;
import org.w3c.css.values.CssTypes;
import org.w3c.css.values.CssValue;

/**
 *   <H4>
 *     <A NAME="background-repeat">5.3.4 &nbsp;&nbsp; 'background-repeat'</A>
 *   </H4>
 *   <P>
 *   <EM>Value:</EM> repeat | repeat-x | repeat-y | no-repeat<BR>
 *   <EM>Initial:</EM> repeat<BR>
 *   <EM>Applies to:</EM> all elements<BR>
 *   <EM>Inherited:</EM> no<BR>
 *   <EM>Percentage values:</EM> N/A<BR>
 *   <P>
 *   If a background image is specified, the value of 'background-repeat' determines
 *   how/if the image is repeated.
 *   <P>
 *   A value of 'repeat' means that the image is repeated both horizontally and
 *   vertically. The 'repeat-x' ('repeat-y') value makes the image repeat horizontally
 *   (vertically), to create a single band of images from one side to the other.
 *   With a value of 'no-repeat', the image is not repeated.
 *   <PRE>
 *   BODY {
 *     background: red url(pendant.gif);
 *     background-repeat: repeat-y;
 *   }
 *  </PRE>
 *   <P>
 *   In the example above, the image will only be repeated vertically.
 * @version $Revision: 1.5 $
 */
public class CssBackgroundRepeatCSS2 extends CssProperty
	implements CssBackgroundConstants {

    private static final String property_name = "background-repeat";

    int repeat;

    private static int[] hash_values;

    /**
     * Create a new CssBackgroundRepeatCSS2
     */
    public CssBackgroundRepeatCSS2() {
	repeat = 0;
    }

    /**
     * Set the value of the property
     * @param expression The expression for this property
     * @exception InvalidParamException The expression is incorrect
     */
    public CssBackgroundRepeatCSS2(ApplContext ac, CssExpression expression,
	    boolean check) throws InvalidParamException {

	if(check && expression.getCount() > 1) {
	    throw new InvalidParamException("unrecognize", ac);
	}

	CssValue val = expression.getValue();
	setByUser();

	if (val.getType() == CssTypes.CSS_IDENT) {
	    int hash = val.hashCode();
	    for (int i =0; i < REPEAT.length; i++) {
		if (hash_values[i] == hash) {
		    repeat = i;
		    expression.next();
		    return;
		}
	    }
	}
	throw new InvalidParamException("value", expression.getValue(),
					getPropertyName(), ac);
    }

    public CssBackgroundRepeatCSS2(ApplContext ac, CssExpression expression)
	throws InvalidParamException {
	this(ac, expression, false);
    }

    /**
     * Returns the value of this property
     */
    public Object get() {
	return REPEAT[repeat];
    }

    /**
     * Returns true if this property is "softly" inherited
     * e.g. his value equals inherit
     */
    public boolean isSoftlyInherited() {
	return (repeat == 4);
    }

    /**
     * Returns a string representation of the object.
     */
    public String toString() {
	return REPEAT[repeat];
    }

    /**
     * Returns the name of this property
     */
    public String getPropertyName() {
	return property_name;
    }

    /**
     * Add this property to the CssStyle.
     *
     * @param style The CssStyle
     */
    public void addToStyle(ApplContext ac, CssStyle style) {
	CssBackgroundCSS2 cssBackground = ((Css1Style) style).cssBackgroundCSS2;
	if (cssBackground.repeat != null)
	    style.addRedefinitionWarning(ac, this);
	cssBackground.repeat = this;
    }

    /**
     * Get this property in the style.
     *
     * @param style The style where the property is
     * @param resolve if true, resolve the style to find this property
     */
    public CssProperty getPropertyInStyle(CssStyle style, boolean resolve) {
	if (resolve) {
	    return ((Css1Style) style).getBackgroundRepeatCSS2();
	} else {
	    return ((Css1Style) style).cssBackgroundCSS2.repeat;
	}
    }

    /**
     * Compares two properties for equality.
     *
     * @param value The other property.
     */
    public boolean equals(CssProperty property) {
	return (property instanceof CssBackgroundRepeatCSS2 &&
		repeat == ((CssBackgroundRepeatCSS2) property).repeat);
    }

    /**
     * Is the value of this property is a default value.
     * It is used by all macro for the function <code>print</code>
     */
    public boolean isDefault() {
	return repeat == 0;
    }

    static public boolean checkMatchingIdent(CssIdent idval) {
	for (int i=0 ; i < hash_values.length; i++) {
	    if (hash_values[i] == idval.hashCode()) {
		return true;
	    }
	}
	return false;
    }

    static {
	hash_values = new int[REPEAT.length];
	for (int i = 0; i < REPEAT.length; i++)
	    hash_values[i] = REPEAT[i].hashCode();
    }
}



