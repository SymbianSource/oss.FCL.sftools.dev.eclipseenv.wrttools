//
// $Id: Css2Property.java,v 1.2 2005-09-08 12:24:01 ylafon Exp $
// From Philippe Le Hegaret (Philippe.Le_Hegaret@sophia.inria.fr)
//
// (c) COPYRIGHT MIT and INRIA, 1997.
// Please first read the full copyright statement in file COPYRIGHT.html

package org.w3c.css.properties.css2;

import org.w3c.css.properties.aural.ACssProperty;

/**
 * @version $Revision: 1.2 $
 */
public abstract class Css2Property extends ACssProperty {

  /**
   * Returns true if the property is inherited
   */
  public boolean Inherited() {
    return Css2Properties.getInheritance(this);
  }

}
