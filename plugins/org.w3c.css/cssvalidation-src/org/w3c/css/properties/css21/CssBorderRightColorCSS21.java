// $Id: CssBorderRightColorCSS21.java,v 1.2 2005-09-14 15:14:58 ylafon Exp $
// Author: Jean-Guilhem Rouel
// (c) COPYRIGHT MIT, ERCIM and Keio, 2005.
// Please first read the full copyright statement in file COPYRIGHT.html
package org.w3c.css.properties.css21;

import org.w3c.css.properties.css1.CssBorderFaceColorCSS2;
import org.w3c.css.properties.css1.CssBorderRightColorCSS2;
import org.w3c.css.util.ApplContext;
import org.w3c.css.util.InvalidParamException;
import org.w3c.css.values.CssExpression;

/**
 * CssBorderRightColorCSS21<br />
 * Created: Aug 31, 2005 2:09:31 PM<br />
 */
public class CssBorderRightColorCSS21 extends CssBorderRightColorCSS2 {

    /**
     * Create a new CssBorderRightColorCSS21 with an another CssBorderFaceColorCSS2
     *
     * @param another An another face.
     */
    public CssBorderRightColorCSS21(CssBorderFaceColorCSS2 another) {
	super(another);
    }

    /**
     * Create a new CssBorderRightColor
     *
     * @param expression The expression for this property.
     * @exception InvalidParamException Values are incorrect
     */
    public CssBorderRightColorCSS21(ApplContext ac, CssExpression expression,
	    boolean check) throws InvalidParamException {

	if(check && expression.getCount() > 1) {
	    throw new InvalidParamException("unrecognize", ac);
	}

	setByUser();
	setFace(new CssBorderFaceColorCSS21(ac, expression));
    }

    public CssBorderRightColorCSS21(ApplContext ac, CssExpression expression)
    throws InvalidParamException {
	this(ac, expression, false);
    }
}
