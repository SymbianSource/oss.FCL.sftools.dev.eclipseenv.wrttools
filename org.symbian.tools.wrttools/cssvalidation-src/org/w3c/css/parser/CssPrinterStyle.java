//
// $Id: CssPrinterStyle.java,v 1.4 2005-09-08 12:23:33 ylafon Exp $
// From Philippe Le Hegaret (Philippe.Le_Hegaret@sophia.inria.fr)
//
// (c) COPYRIGHT MIT and INRIA, 1997.
// Please first read the full copyright statement in file COPYRIGHT.html

package org.w3c.css.parser;

import org.w3c.css.properties.css1.CssProperty;

/**
 * This class is invoke by all propperties when a print is required.
 *
 * @version $Revision: 1.4 $
 * @see org.w3c.css.parser.CssStyle#print
 */
public interface CssPrinterStyle {

  /**
   * Print this property.
   *
   * @param property The property to print.
   * @see org.w3c.css.properties.css1.CssProperty#toString
   * @see org.w3c.css.properties.css1.CssProperty#getPropertyName
   */
  public void print(CssProperty property);
}
