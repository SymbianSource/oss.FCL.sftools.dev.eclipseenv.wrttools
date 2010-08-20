//
// $Id: AtRuleMediaCSS1.java,v 1.5 2007-11-26 05:07:17 ot Exp $
// From Philippe Le Hegaret (Philippe.Le_Hegaret@sophia.inria.fr)
//
// (c) COPYRIGHT MIT and INRIA, 1997.
// Please first read the full copyright statement in file COPYRIGHT.html
/*
 * AtRuleMedia.java
 * $Id: AtRuleMediaCSS1.java,v 1.5 2007-11-26 05:07:17 ot Exp $
 */
package org.w3c.css.parser;

import java.util.Enumeration;

import org.w3c.css.util.ApplContext;
import org.w3c.css.util.InvalidParamException;

/**
 * This class manages all media defines by CSS2
 *
 * @version $Revision: 1.5 $
 * @author  Philippe Le Hegaret
 */
public class AtRuleMediaCSS1 extends AtRuleMedia {

    static final String[] mediaCSS1 = {
	"all"
    };

    String[] media = new String[mediaCSS1.length];

    boolean empty = true;

    /**
     * Creates a new AtRuleMediaCSS1
     */
    public AtRuleMediaCSS1() {
        media[0] = mediaCSS1[0];
    }


    /**
     * Adds a medium.
     *
     * @exception InvalidParamException the medium doesn't exist
     */
    public AtRuleMedia addMedia(String medium,
				ApplContext ac) throws InvalidParamException {

	// do nothing
	return this;
    }

    /**
     * Returns the at rule keyword
     */
    public String keyword() {
	return "media";
    }

    public boolean isEmpty() {
	return false;
    }

    /**
     * The second must be exactly the same of this one
     */
    public boolean canApply(AtRule atRule) {
	if (atRule instanceof AtRuleMedia) {
	    return true;
	} else {
	    return false;
	}
    }

    /**
     * The second must only match this one
     */
    public boolean canMatched(AtRule atRule) {
	if (atRule instanceof AtRuleMedia) {
	    return true;
	} else {
	    return false;
	}
    }

    public Enumeration elements() {
	return new MediaEnumeration(this);
    }

    /**
     * Returns a string representation of the object.
     */
    public String toString() {
	return "@" + keyword() + " all ";
    }


}
