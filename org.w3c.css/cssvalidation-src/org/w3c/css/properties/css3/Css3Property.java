//
// $Id: Css3Property.java,v 1.1 2005-08-23 16:24:20 ylafon Exp $
// From Sijtsche de Jong (sy.de.jong@let.rug.nl)
//
// COPYRIGHT (c) 1995-2000 World Wide Web Consortium, (MIT, INRIA, Keio University)
// Please first read the full copyright statement in the file COPYRIGHT.html

package org.w3c.css.properties.css3;

import org.w3c.css.properties.css1.CssProperty;

public abstract class Css3Property extends CssProperty {

   /**
    * Returns true if the property is inherited
    */
   public boolean Inherited() {
      return Css3Properties.getInheritance(this);
   }

}
