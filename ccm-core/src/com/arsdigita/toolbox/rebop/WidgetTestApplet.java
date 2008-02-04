/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.toolbox.rebop;

import java.applet.Applet;
import java.applet.AppletContext;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.TextField;
import java.awt.Toolkit;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.io.InputStream;
import java.io.IOException;

import java.net.URL;

/**
 * WidgetTestApplet.java
 *
 *
 * Created: Mon Sep 24 05:21:48 2001
 *
 * @author Gavin Doughtie
 * @version $Date: 2004/08/16 $
 */

public class WidgetTestApplet extends Applet {
    private GenericList m_sp = new GenericList();
    private GenericList m_messageArea = new GenericList();
    private GenericLabel m_statusLabel = new GenericLabel("status");
    private GenericComponent m_container = new GenericComponent();
    private boolean m_doubleBuffer = false;
    private boolean m_isApp = false;

    private GenericImage m_mainBackground = null;
    private GenericImage m_itemIcon = null;
    private GenericImage m_listBackground = null;
    private Image m_bgImage = null;
    private Image m_iconImage = null;
    private Image m_listBackgroundImage = null;

    private MediaTracker m_tracker = null;

    private TextField m_tf = new TextField();

    public WidgetTestApplet() {
        this(true);
    }

    public WidgetTestApplet(boolean doubleBuffer) {
        m_doubleBuffer = doubleBuffer;
        m_tracker = new MediaTracker(this);

        m_sp.setName("m_sp");
        m_container.setName("m_container");
    }

    public void setIsApp(boolean isApp) {
        m_isApp = isApp;
    }

    public void setIconImage(Image itemIcon) {
        m_iconImage = itemIcon;
        m_itemIcon = new GenericImage(itemIcon);
        if (null != itemIcon) {
            m_sp.setDefaultItemIcon(itemIcon);
        }
    }

    public void setBackgroundImage(Image mainBackground) {
        if (null == mainBackground) {
            m_container.setDrawable(
                                    new BevelBox(Color.orange, null, null, 0, false));
        } else {
            m_mainBackground = new GenericImage(mainBackground);
            m_container.setDrawable(m_mainBackground);
        }
    }

    public void setListBackgroundImage(Image listBackground) {
        m_listBackground = new GenericImage(listBackground);
        if (null != listBackground) {
            m_sp.setDrawable(m_listBackground);
        }
    }

    private Image loadImage(String imageSpec, boolean loadLocal) {
        Image img = null;
        AppletContext context = getAppletContext();
        URL newurl = null;
        try {
            String docbase =
                "file://localhost/home/gavin/develop/open/toolbox/build/classes";
            newurl = new URL(docbase + "/" + imageSpec);
            System.out.println("New URL: " + newurl);
            img = context.getImage(newurl);
            if (null != img) {
                return img;
            }
        } catch (java.net.MalformedURLException me) {
            me.printStackTrace();
        }

        URL docBase = getCodeBase();
        // Hack around Netscape jdk 1.1 bug:
        if (loadLocal) {
            Class c = this.getClass();
            URL url = c.getResource(
                                    imageSpec.substring(imageSpec.lastIndexOf("/")+1));
            Toolkit toolkit = Toolkit.getDefaultToolkit();
            System.out.println("attempting to load: " + url);
            img = toolkit.getImage(url);
        } else {
            img = getImage(docBase,
                           imageSpec);
        }

        if (null == img) {
            System.out.println("can't find image: " + imageSpec +
                               " in " + docBase);
            return img;
        }

        int id = 1;
        m_tracker.addImage(img, id);
        try {
            m_tracker.waitForAll();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return img;
    }

    private Image alternateLoadImage(String imageSpec) {
        Image img = null;
        try {
            InputStream in = getClass().getResourceAsStream(imageSpec);
            if (in == null) {
                System.err.println("Image not found");
            } else {
                byte[] buffer = new byte[in.available()];
                in.read(buffer);
                img = getToolkit().createImage(buffer);
            }
        } catch(IOException e) {
        }
        return img;
    }

    public void init() {
        if (!m_isApp) {
            m_bgImage = getImage(getCodeBase(), getParameter("bgImage"));
            m_iconImage = getImage(getCodeBase(), getParameter("iconImage"));
            setIconImage(m_iconImage);
            setBackgroundImage(m_bgImage);
        }
        m_container.setSize(600, 400);

        setLayout(new BorderLayout());

        m_sp.setBounds(15, 15, 200, 175);
        m_sp.setMaxItems(50);

        m_sp.setDrawable(
                         new BevelBox(null, Color.lightGray, Color.darkGray, 2, true));
        m_sp.setItemUpDrawable(null);
        m_sp.setItemOverDrawable(
                                 new BevelBox(null, Color.lightGray, Color.darkGray, 2, false));
        m_sp.setItemDownDrawable(
                                 new BevelBox(GenericListItem.DEFAULT_SELECTED_COLOR,
                                              null, null, 0, false));

        GenericButton iconButton = new GenericButton(m_iconImage, null, 0, 0);
        BevelBox whiteBevelUp = new BevelBox(
                                             Color.white,
                                             Color.lightGray,
                                             Color.gray,
                                             2,
                                             false);
        BevelBox whiteBevelDown = new BevelBox(
                                               Color.white,
                                               Color.lightGray,
                                               Color.gray,
                                               2,
                                               true);
        iconButton.setUpDrawable(whiteBevelUp);
        iconButton.setOverDrawable(whiteBevelUp);
        iconButton.setDownDrawable(whiteBevelDown);
        iconButton.setBounds(320, 10, 38, 38);


        GenericButton add = new GenericButton("add");
        add.setBounds(320, 60, 80, 30);
        BevelBox buttonUp = new BevelBox(null, Color.white, Color.black, 1, false);
        BevelBox buttonDown = new BevelBox(null, Color.white, Color.black, 2, true);
        BevelBox buttonOver = new BevelBox(null, Color.white, Color.black, 2, false);

        add.setUpDrawable(buttonUp);
        add.setOverDrawable(buttonOver);
        add.setDownDrawable(buttonDown);

        add.addActionListener(new ActionListener() {
                private int count = 0;
                public void actionPerformed(ActionEvent e) {
                    m_sp.add("Person" +
                             count + ":", "Text is " + count * 1000 +
                             " words of babble and other nonsense",
                             "UserData for list item " + count);
                    count++;
                }
            });

        GenericComponent testComposite = new GenericComponent();
        GenericComponent multiples = new GenericComponent();
        multiples.setLayout(new GridBagLayout());
        multiples.setDrawable(new BevelBox(
                                           Color.red,
                                           Color.white,
                                           Color.yellow,
                                           4,
                                           true));
        GenericLabel lab = new GenericLabel("TEST");
        lab.setForeground(Color.green);
        multiples.add(lab);

        testComposite.setDrawable(multiples);
        testComposite.setBounds(320, 250, 80, 80);

        final GenericLabel multiFontTest = new GenericLabel();
        FormattedText label = new FormattedText(
                                                null,
                                                Color.darkGray,
                                                "Now are\nthe times that try men's souls. ");

        FormattedText body = new FormattedText(
                                               null,
                                               Color.red,
                                               "And when in the world will this ever work?");

        StyledText stx = new StyledText();
        stx.addRun(label);
        stx.addRun(body);
        multiFontTest.setDrawable(stx);

        GenericButton labelTest = new GenericButton("wrap test");
        labelTest.setUpDrawable(buttonUp);
        labelTest.setOverDrawable(buttonOver);
        labelTest.setDownDrawable(buttonDown);
        labelTest.setBounds(400, 60, 100, 30);

        multiFontTest.setBounds(415, 250, 100, 100);

        GenericButton remove = new GenericButton("remove");
        remove.setUpDrawable(buttonUp);
        remove.setOverDrawable(buttonOver);
        remove.setDownDrawable(buttonDown);
        remove.setBounds(320, 120, 50, 30);

        remove.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    m_sp.remove(m_sp.getSelectedIndex());
                }
            });

        m_sp.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    System.out.println("Item: " + e);
                    Object di = e.getItem();
                    if (null == di) {
                        di = "";
                    }
                    m_statusLabel.setName(di.toString());
                    m_statusLabel.repaint();
                }
            });

        m_statusLabel.setBounds(320, 200, 100, 30);



        GenericButton db = new GenericButton("Double Buffer");
        db.setToggleButton(true);
        db.setUpDrawable(buttonUp);
        db.setOverDrawable(buttonOver);
        db.setDownDrawable(buttonDown);
        db.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    m_container.setDoubleBuffered(!m_container.getDoubleBuffered());
                }
            });
        db.setBounds(320, 150, 50, 30);

        GenericListItem wrapLabel = new GenericListItem(null,
                                                        "Test",
                                                        "This is a test",
                                                        null);
        wrapLabel.setBounds(450, 200, 150, 30);

        m_tf.setBounds(450, 300, 150, 30);
        m_tf.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    java.awt.TextField field = (TextField) e.getSource();
                    m_sp.add("User Text: ", field.getText(), null);
                }
            });
        m_tf.setFont(new Font("SansSerif", Font.PLAIN, 12));

        m_messageArea.setFont(new Font("SansSerif", Font.PLAIN, 12));
        m_messageArea.setDrawable(
                                  new BevelBox(Color.white, Color.lightGray, Color.darkGray, 2, true));
        BevelBox bb = null; // No state drawing right now
        m_messageArea.setItemUpDrawable(bb);
        m_messageArea.setItemOverDrawable(bb);
        m_messageArea.setItemDownDrawable(bb);
        m_messageArea.setLabelColor(Color.blue);
        m_messageArea.setRolloverLabelColor(Color.blue);
        m_messageArea.setSelectedLabelColor(Color.blue);
        m_messageArea.setTextColor(Color.black);
        m_messageArea.setSelectedTextColor(Color.black);
        m_messageArea.setRolloverTextColor(Color.black);
        m_messageArea.setDoubleBuffered(true);
        m_messageArea.setBounds(15, 195, 200, 175);

        GenericButton add2 = new GenericButton("add2");
        add2.setUpDrawable(buttonUp);
        add2.setOverDrawable(buttonOver);
        add2.setDownDrawable(buttonDown);
        add2.setBounds(400, 60, 100, 30);
        add2.addActionListener(new ActionListener() {
                private int count2 = 0;
                public void actionPerformed(ActionEvent e) {
                    m_messageArea.add("Sample" +
                                      count2 + ":", "Text is " + count2 * 100, null);
                    count2++;
                }
            });

        m_container.setLayout(null);//new BorderLayout());
        m_container.add(m_sp);//, BorderLayout.CENTER);
        m_container.add(iconButton);
        m_container.add(add);//, BorderLayout.SOUTH);
        m_container.add(remove);
        m_container.add(m_statusLabel);
        m_container.add(wrapLabel);
        m_container.add(db);
        m_container.add(multiFontTest);
        m_container.add(testComposite);
        //          // m_container.add(m_messageArea);
        m_container.add(add2);
        // m_container.add(smoothButton);
        // m_container.add(m_smooth);

        //          // m_container.add(m_tf);
        m_container.setDoubleBuffered(m_doubleBuffer);

        multiples.setBounds(0, 0,
                            testComposite.getBounds().width,
                            testComposite.getBounds().height);

        // m_container.add(multiples);
        // multiples.doLayout();


        GenericComponent labelContainer = new GenericComponent();
        labelContainer.setBounds(320, 250, 200, 200);
        labelContainer.setLayout(new FlowLayout(FlowLayout.LEFT, 2, 2));
        labelContainer.add(new GenericLabel("One"));
        labelContainer.add(new GenericLabel("This is longer"));
        labelContainer.add(new GenericLabel("Shortish"));
        m_container.add(labelContainer);
        setLayout(null);
        add(m_container, BorderLayout.CENTER);

        doLayout();
        multiples.setVisible(false);



    }

    public void update(Graphics g) {
        paint(g);
    }

    public static void main(String args[]) {
        Frame f = new Frame();

        if (args.length > 0 && "true".equals(args[0])) {
            GenericComponent.setDebugPaint(true);
        } // end of if ()

        boolean db = false;
        if ( args.length > 1 && args[1].equals("true")) {
            db = true;
        } // end of if ()

        boolean isApplet = false;
        if ( args.length > 2 && args[2].equals("true")) {
            isApplet = true;
        }

        WidgetTestApplet applet = new WidgetTestApplet(db);
        applet.setIsApp(isApplet);

        // Load some images
        Image icon = applet.getToolkit().createImage("./ignore.gif");
        Image mainBackground = applet.getToolkit().createImage("./bg.jpg");
        MediaTracker tracker = new MediaTracker(applet);
        tracker.addImage(icon, 0);
        tracker.addImage(mainBackground, 0);
        try {
            tracker.waitForID(0);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        f.setLayout(new BorderLayout());
        f.add(applet, BorderLayout.CENTER);
        f.setSize(600, 400);

        f.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    System.exit(0);
                }
            });

        applet.setIconImage(icon);
        applet.init();
        applet.setBackgroundImage(mainBackground);

        f.show();
    }

}// WidgetTestApplet
