/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */
package com.arsdigita.portalserver;

import com.arsdigita.bebop.FormSection;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.form.RadioGroup;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.Label;

/**
 * This class represents a drop-in FormSection for color selection.
 * The class includes a title label describing what the color refers to,
 * a radiogroup for the color choices, and a text input field for
 * use when the available pallette does not include a desired color.
 * Note: All color values are represented within the class
 * by strings of the type <code>"#FFCA04"</code>. Neither the input on 
 * the text field, or the addColor method are checked for proper format 
 * - care must be taken by the user to maintain the proper string 
 * representation.
 *
 * <p>The ColorPicker class includes a built in default group
 * of 12 colors. If the developer would rather supply their
 * own colors, <code>clearColors()</code> can be called, and then the 
 * internal radioGroup populated with colors provided by a 
 * radioGroup using the <code>addOptionGroup()</code> method.
 *
 * <p>The <code>getValue()</code> method for this class checks first if
 * the text field is null. If not, its value is returned. If null,
 * the radiogroup is then checked for its value and returned.
 * 
 * @author Jim Parsons
 */

public class ColorPicker extends FormSection {

  TextField m_textfield;
  Label m_label;
  RadioGroup m_grp;
  String m_name;

  static final String BLACK = "#000000";
  static final String DARKGRAY = "#A9A9A9";
  static final String LIGHTGRAY = "#D3D3D3";
  static final String BLUE = "#0000FF";
  static final String CYAN = "#00FFFF";
  static final String GREEN = "#00FF00";
  static final String MAGENTA = "#FF00FF";
  static final String RED = "#FF0000";
  static final String ORANGE = "#FFA500";
  static final String PINK = "#FFC0CB";
  static final String YELLOW = "#FFFF00";
  static final String WHITE = "#FFFFFF";

  public ColorPicker(String name) {

    this(name, "#000000");

  }
 
  public ColorPicker(String name, String defaultValue) {


    super.setClassAttr("colorpicker");

    //Remove pesky white spaces
    String cleanName = name.replace(' ', '_');

    m_grp = new RadioGroup(cleanName); 
    m_grp.setClassAttr("colorchoices");
    m_grp.clearOptions();

    m_grp.addOption(new Option(BLACK,"1"));
    m_grp.addOption(new Option(DARKGRAY,"2"));
    m_grp.addOption(new Option(LIGHTGRAY,"3"));
    m_grp.addOption(new Option(BLUE,"4"));
    m_grp.addOption(new Option(CYAN, "5"));
    m_grp.addOption(new Option(GREEN,"6"));
    m_grp.addOption(new Option(MAGENTA,"7"));
    m_grp.addOption(new Option(RED,"8"));
    m_grp.addOption(new Option(ORANGE,"9"));
    m_grp.addOption(new Option(PINK,"10"));
    m_grp.addOption(new Option(YELLOW,"11"));
    m_grp.addOption(new Option(WHITE,"12"));

    m_label = new Label(name);
    m_name = name;
    m_textfield = new TextField("Colorpicker_" + cleanName);
    m_textfield.setDefaultValue("");
    add(m_label);
    add(m_textfield);
    add(m_grp);

  } 

  public String getValue(PageState ps) {
    String rgroup = null;
    rgroup = (String)m_grp.getValue(ps);

    if(rgroup == null) {
      return (String)m_textfield.getValue(ps);
    }
    else {
      return(rgroup);
    }
  }

  void setValue(PageState ps, String val) {
    m_textfield.setValue(ps,val);
  }

  public void clearColors() {
    m_grp.clearOptions();
  }

  public void addOptionGroup(RadioGroup rgroup) {
   m_grp = rgroup;
  } 

}
