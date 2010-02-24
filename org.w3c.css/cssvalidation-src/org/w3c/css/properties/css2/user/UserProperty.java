//
// $Id: UserProperty.java,v 1.1 2005-08-23 16:33:51 ylafon Exp $
// From Philippe Le Hegaret (Philippe.Le_Hegaret@sophia.inria.fr)
//
// (c) COPYRIGHT MIT and INRIA, 1997.
// Please first read the full copyright statement in file COPYRIGHT.html
/*
 */

package org.w3c.css.properties.css2.user;

import org.w3c.css.properties.css1.CssProperty;

/**
 * @version $Revision: 1.1 $
 */
public abstract class UserProperty extends CssProperty {

  /**
   * Returns true if the property is inherited
   */
  public boolean Inherited() {
    return UserProperties.getInheritance(this);
  }

}
