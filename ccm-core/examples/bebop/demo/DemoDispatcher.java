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
package com.arsdigita.bebop.demo;


import com.arsdigita.bebop.ActionLink;
import com.arsdigita.bebop.BlockStylable;
import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Column;
import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.Container;
import com.arsdigita.bebop.ControlLink;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.FormSection;
import com.arsdigita.bebop.GridPanel;
import com.arsdigita.bebop.Image;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Link;
import com.arsdigita.bebop.Multiple;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.SplitPanel;
import com.arsdigita.bebop.TabbedPane;
import com.arsdigita.bebop.ToggleLink;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormValidationListener;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.form.CheckboxGroup;
import com.arsdigita.bebop.form.FormErrorDisplay;
import com.arsdigita.bebop.form.MultipleSelect;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.OptionGroup;
import com.arsdigita.bebop.form.RadioGroup;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.DateParameter;
import com.arsdigita.bebop.parameters.EmailParameter;
import com.arsdigita.bebop.parameters.IntegerParameter;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.bebop.util.BebopConstants;
import com.arsdigita.bebop.util.GlobalizationUtil;
import static com.arsdigita.bebop.util.GlobalizationUtil.globalize;
import com.arsdigita.bebop.util.Size;
import com.arsdigita.dispatcher.Dispatcher;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.toolbox.ui.QueryRowsBuilder;
import com.arsdigita.util.UncheckedWrapperException;
import java.util.Enumeration;
import java.util.TooManyListenersException;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;


// for buildPageControlLink so it uses less convoluted and possibly
// illegal java that chokes the latest jikes compiler

class Foo extends Label implements PrintListener {
    public static final String aValue = "Kiwi";
    StringParameter m_foo = new StringParameter("foo");

    Foo () {
        super ("static label");
        addPrintListener(this);
    }

    public void prepare(PrintEvent e) {
        Label label = (Label)e.getTarget();
        PageState s = e.getPageState();
        String value = (String)s.getValue(m_foo);
        if (value == null) {
            label.setLabel( (String) GlobalizationUtil.globalize("bebop.demo.foo_is_null").localize());
        } else {
            label.setLabel("foo has the value \"" + value
                           + "\" and length " + value.length() + ".");
        }
        s.setValue(m_foo, aValue);
    }

    public void register(Page p) {
        super.register(p);
        p.addComponentStateParam(this, m_foo);
    }
}


/**
 * Demonstration dispatcher class.  Shows how you would build pages to
 * display dynamic data from the database using Bebop components.
 *
 * @version $Id: DemoDispatcher.java 2089 2010-04-17 07:55:43Z pboy $
 */
public class DemoDispatcher extends AutoDispatcher implements BebopConstants {


    private static final Logger s_log =
        Logger.getLogger(DemoDispatcher.class.getName());

    public DemoDispatcher() {
        s_log.debug("Instantiating a new demo dispatcher in the constructor..."
                    );
        setNotFoundDispatcher(new OtherDispatcher());
    }

    /**
     * returns a new instance of DemoDispatcher.
     */
    public static Dispatcher newInstance() {
        s_log.debug("Instantiating a new demo dispatcher...");
        return new DemoDispatcher();
    }

    public static Page buildPageMySite_1() {
        Page p = new MySitePage();
        p.add(new Label(GlobalizationUtil.globalize("bebop.demo.this_is_the_main_content_area")));
        p.lock();
        return p;
    }

    public static Page buildPageMySite_2() {
        Page p = new MySitePage();
        p.add(new Label(GlobalizationUtil.globalize("bebop.demo.four_score_and_seven_years_ago_blah_blah")));
        p.lock();
        return p;
    }

    /**
     * @return a Bebop page that shows the current time selected from the
     * database.
     */
    public static Page buildPagedate() {
        Page p = new Page("Current Date Page");
        p.add(new HelloDate());
        p.lock();
        return p;
    }

    /**
     * @return a Bebop page that shows a list of users, their emails,
     * and first/last names
     */
    public static Page buildPageusers() {
        Page p = new Page("User Page");
        p.add(new UserList());
        p.lock();
        return p;
    }

    /**
     * @return a Bebop page that shows a list of users, their emails,
     * and first/last names; *also* with a message about the current
     * date.  <b>note</b> the reuse of HelloDate component!
     */
    public static Page buildPageuser_and_date() {
        Page p = new Page("User and date page");
        p.add(new HelloDate());
        p.add(new UserList());
        p.lock();
        return p;
    }

    public static Page buildPageWorkflowAdmin() {
        Page result = new WorkflowAdminPage();
        result.lock();
        return result;
    }

    public static Page buildPageTabbedPane() {
        Page p = new Page("Tabbed Pane page");
        TabbedPane tp = new TabbedPane();
        Label tabA = new Label(GlobalizationUtil.globalize("bebop.demo.tab_a"));
        Label tabB = new Label(GlobalizationUtil.globalize("bebop.demo.tab_b"));

        tp.add(tabA);

        tp.addTab("second tab",tabB);

        p.add(tp);

        p.lock();
        return p;

    }

    static Page buildPageBoxPanel() {
        Page p = new Page("BoxPanel");

        BoxPanel x_panel = new BoxPanel(BoxPanel.HORIZONTAL, true);
        x_panel.setWidth("80%");
        x_panel.setBorder(1);

        x_panel.add(new Label(GlobalizationUtil.globalize("bebop.demo.this_outer_panel_runs_across")));
        BoxPanel y_panel = new BoxPanel();
        x_panel.add(y_panel);
        x_panel.add(new Label(GlobalizationUtil.globalize("bebop.demo.this_is_in_the_outer_panel_again")));
        y_panel.add(new Label(GlobalizationUtil.globalize("bebop.demo.this_inner_panel_runs_downwards")));
        y_panel.add(new Label(GlobalizationUtil.globalize("bebop.demo.its_axis_defaults_to_vertical")));
        Label l = new Label(GlobalizationUtil.globalize("bebop.demo.centering_to_false"));
        l.setClassAttr("medium");
        y_panel.add(l);
        y_panel.add(new Label(GlobalizationUtil.globalize("bebop.demo.border_to_0_false")));
        y_panel.add(new Label(GlobalizationUtil.globalize("bebop.demo.and_its_width_is_unconstrained")));
        p.add(x_panel);
        p.add(new Label("This Label is outside the outer explicit panel.  "
                        + "Page by default uses a BoxPanel to contain its "
                        + "children."));
        p.lock();
        return p;
    }

    /**
     * @return a page demonstrating a static Label
     */
    static Page buildPageLabel() {
        Page p = new Page("Static Label");
        p.add(new Label(GlobalizationUtil.globalize("bebop.demo.hello_world_")));
        p.lock();
        return p;
    }

    /**
     * Link example from Quickstart.
     * @return a page demonstrating a Link with an URL variable
     */
    static Page buildPageLink() {
        Page p = new Page("Link Example");

        Link link = new Link( new Label(GlobalizationUtil.globalize("bebop.demo.link_text")),  "foo/");
        link.setVar("bar", "1");                // HTML -> href="foo/?bar=1"
        link.setVar("uuml", "\u00FC");                // HTML -> href="foo/?bar=1"
        p.add(link);
        p.lock();
        return p;
    }

    /**
     * @return a page demonstrating a Dynamic Label
     */
    static Page buildPageLabel_dyn() {
        Page p = new Page("Dynamic Label");
        p.add(new Label(GlobalizationUtil.globalize("bebop.demo.you_are_connecting_from")));
        p.add(new Label(GlobalizationUtil.globalize("bebop.demo.dont_care")) {
                public String getLabel(PageState ps) {
                    return ps.getRequest().getRemoteHost();
                }
            }
              );
        p.add(new Label(GlobalizationUtil.globalize("bebop.demo.your_request_header_specifies")));
        p.add(new Label(GlobalizationUtil.globalize("bebop.demo.dont_care"),  false) {
                public String getLabel(PageState ps) {
                    HttpServletRequest req = ps.getRequest();
                    Enumeration names = req.getHeaderNames();
                    StringBuffer buffer = new StringBuffer();
                    buffer.append("<table>\n<tr><th>name</th><th>value</th></tr>\n");
                    while (names.hasMoreElements()) {
                        String name = (String)names.nextElement();
                        buffer.append("<tr><td>").append(name).append("</td><td>\"")
                            .append(req.getHeader(name)).append("\"</td></tr>\n");
                    }
                    buffer.append("</table>\n");
                    return buffer.toString();
                }
            }
              );

        p.lock();
        return p;
    }

    static Page buildPageColumnPanel() {
        Page p = new Page("ColumnPanel");

        ColumnPanel panel = new ColumnPanel(3);

        panel.setColumnWidth(1, "25%");
        panel.setBorder(false);
        panel.setPadColor("#ccffcc");
        panel.setPadBorder(true);

        panel.add(new Label(GlobalizationUtil.globalize("bebop.demo.this_should_be_full_width_aligned_right")),
                  ColumnPanel.FULL_WIDTH | ColumnPanel.RIGHT);
        panel.add(new Label(GlobalizationUtil.globalize("bebop.demo.this_should_be_full_width_and_centered")),
                  ColumnPanel.FULL_WIDTH | ColumnPanel.CENTER);

        panel.add(new Label(GlobalizationUtil.globalize("bebop.demo.this_should_not_be_full_width")));
        panel.add(new Label(GlobalizationUtil.globalize("bebop.demo.yet_another_single_column")));
        panel.add(new Label(GlobalizationUtil.globalize("bebop.demo.third_1_column_cell")));
        // panel.add(new Label(GlobalizationUtil.globalize("bebop.demo.lone_cell_makes_table_look_bad")));

        p.add(panel);
        p.lock();
        return p;
    }

    static Page buildPageGridPanel() {
        Page p = new Page("GridPanel");

        GridPanel panel = new GridPanel(3);
        panel.setBorder(new Size(2));

        panel.add(new Label(GlobalizationUtil.globalize("bebop.demo.centered_across_the_full_width")),
                  GridPanel.FULL_WIDTH | GridPanel.CENTER);
        panel.add(new Label(GlobalizationUtil.globalize("bebop.demo.only_one_column")));
        panel.add(new Label(GlobalizationUtil.globalize("bebop.demo.rightaligned_single_column")),
                  BlockStylable.RIGHT);
        panel.add(new Label(GlobalizationUtil.globalize("bebop.demo.third_1_column_cell")));
        panel.add(new Label(GlobalizationUtil.globalize("bebop.demo.full_width_and_aligned_right")),
                  GridPanel.FULL_WIDTH | GridPanel.RIGHT);
        panel.add(new Label(GlobalizationUtil.globalize("bebop.demo.lonely_cell_on_last_row")));

        p.add(panel);
        p.lock();
        return p;
    }

    static Page buildPageActionLink() {
        Page p = new Page("ActionLink");

        final Label blurb = new Label(GlobalizationUtil.globalize("bebop.demo.this_is_the_blurb"));

        p.add(new ActionLink( (String) GlobalizationUtil.globalize("bebop.demo.this_link_showshides_the_blurb").localize()) {
                public void respond(PageState s) {
                    blurb.setVisible(s, ! blurb.isVisible(s));
                }
            });
        p.add(blurb);
        p.add(new Label(GlobalizationUtil.globalize("bebop.demo.a_static_label")));

        p.lock();
        return p;
    }

    static Page buildPageToggleLink() {
        Page p = new Page("ToggleLink");

        p.add(new ToggleLink("take green"));
        p.add(new ToggleLink("choose red"));
        p.add(new ToggleLink("no! yellow"));
        p.add(new Label(GlobalizationUtil.globalize("bebop.demo.a_static_label")));

        p.lock();
        return p;
    }

    static Page buildPageForm0() {
        // GMD -- there was no reason to define a new class here,
        // and jikes wouldn't compile it, either
        Form testForm = new Form("testform");
        ColumnPanel panel = (ColumnPanel) testForm.getPanel();
        panel.setColumnWidth(1, "25%");
        panel.setBorder(false);
        panel.setPadColor("white");
        panel.setPadBorder(true);

        Label l1 = new Label("This should be full width, aligned right "
                             + "and to prove it's working with a long "
                             + "text, here is an example");
        testForm.add(l1, ColumnPanel.FULL_WIDTH | ColumnPanel.RIGHT);

        Label l2 = new Label(GlobalizationUtil.globalize("bebop.demo.this_should_be_full_width_too"));
        testForm.add(l2, ColumnPanel.FULL_WIDTH | ColumnPanel.RIGHT);

        Label l3 = new Label(GlobalizationUtil.globalize("bebop.demo.this_should_not_be_full_width"));
        testForm.add(l3);

        Label l4 = new Label(GlobalizationUtil.globalize("bebop.demo.this_should_not_be_full_width"));
        testForm.add(l4);

        Page p = new Page("Form, primitive (visually a ColumnPanel)");
        p.add(testForm);
        p.lock();
        return p;
    }

    static Page buildPageWorkflow() {
        Page p = new WorkflowAdminPage();
        p.lock();
        return p;
    }

    static Page buildPageSplitPanel() {
        Page p = new Page("SplitPanel");

        p.add(new SplitPanel(new Label(GlobalizationUtil.globalize("bebop.demo.header")),
                             new Label(GlobalizationUtil.globalize("bebop.demo.left_component")),
                             new Label(GlobalizationUtil.globalize("bebop.demo.right_component"))));
        p.lock();
        return p;
    }

    static Page buildPageImage() {
        Image image = new Image("http://www.impaqt.net/abbi3.jpg", "River");
        image.setWidth("337");
        image.setHeight("310");

        Page p = new Page("Image");
        p.add(image);
        p.lock();
        return p;
    }

    static Page buildPagePage() {
        Page page = new Page("Hello, World !");
        page.lock();
        return page;
    }

    /** Changes the page title dynamically */
    static Page buildPagePage_dyn() {
        Page page = new Page(new Label(GlobalizationUtil.globalize("bebop.demo.ignored")) {
                public GlobalizedMessage getGlobalizedMessage() {
                    return new GlobalizedMessage ("Dynamic " + Math.random() + " page");
                } } );
        page.lock();
        return page;
    }

    static Page buildPageTabbedPane_1() {
        Page p = new Page("TabbedPane");

        TabbedPane pane = new TabbedPane();
        pane.setKey("edel");
        p.add(pane);

        pane.addTab(new Image
                    ("http://images.google.com/images?q=tbn:QNyNG6NTnd4:x.y.", "iris"),
                    new Label(GlobalizationUtil.globalize("bebop.demo.this_pane_talks_about_monocotyledons")));
        pane.add(new Label(GlobalizationUtil.globalize("bebop.demo.this_pane_talks_about_dicotyledons")));
        p.lock();
        return p;
    }

    static Page buildPageCheckboxGroup() {
        Page          p     = new Page("CheckboxGroup, Option, Submit");
        Form          form  = new Form("boxform");
        CheckboxGroup group = new CheckboxGroup("boxes");
        p.add(form);
        form.add(group);
        group.addOption(new Option("father", "Adam"));
        group.addOption(new Option("mother", "Eve" ));
        group.addOption(new Option("son0"  , "Cain"));
        group.addOption(new Option("son1"  , "Abel"));
        group.setOptionSelected("mother");
        group.setClassAttr("vertical");
        form.add(new Submit("the submit button"));
        p.lock();
        return p;
    }

    static Page buildPageRadioGroup() {
        Page       p     = new Page("RadioGroup");
        Form       form  = new Form("radioform");
        RadioGroup group = new RadioGroup("boxes");
        p.add(form);
        form.add(group);
        group.addOption(new Option("a", "aleph"));
        group.addOption(new Option("b", "beth" ));
        group.addOption(new Option("c", "gimel"));
        group.addOption(new Option("d", "dalet"));
        group.setOptionSelected("c");
        form.add(new Submit("the submit button"));
        p.lock();
        return p;
    }

    static Page buildPageTextField() {
        Page      p     = new Page("Text Field Widget");
        Form      form  = new Form("address");
        p.add(form);
        form.add(new Label(GlobalizationUtil.globalize("bebop.demo.street")));
        form.add(new TextField("street"));
        form.add(new Label("City (mandatory)"  ));
        TextField city = new TextField("city");
        city.addValidationListener(new NotNullValidationListener());
        form.add(city);
        form.add(new Label(GlobalizationUtil.globalize("bebop.demo.country")));
        form.add(new TextField("country"));
        form.add(new Submit("Done"));
        p.lock();
        return p;
    }

    static Page buildPageForm_Simple() {

        // Set up the page
        Page page = new Page("A Simple Form");
        page.add(new Label(GlobalizationUtil.globalize("bebop.demo.subscribe_to_our_mailing_list")));

        // Create the form
        Form form = new Form("subscribe");

        form.add(new Label(GlobalizationUtil.globalize("bebop.demo.name")));
        form.add(new TextField(new StringParameter("name")));
        form.add(new Label(GlobalizationUtil.globalize("bebop.demo.age")));
        form.add(new TextField(new IntegerParameter("age")));

        form.add(new Label(GlobalizationUtil.globalize("bebop.demo.email_required")));
        TextField email = new TextField(new EmailParameter("email"));
        email.addValidationListener(new NotNullValidationListener());
        form.add(email);

        form.add(new Label(GlobalizationUtil.globalize("bebop.demo.why_would_you_ever_want_to_subscribe_to_spam_")));

        TextArea reason = new TextArea(new StringParameter("reason"));
        reason.setWrap(TextArea.SOFT);
        reason.setRows(4);
        reason.setCols(40);
        form.add(reason);

        form.add(new Submit("Subscribe"),
                 ColumnPanel.RIGHT | ColumnPanel.FULL_WIDTH);

        page.add(form);

        // Finish the page
        page.lock();
        return page;
    }

    /** Sample page from the "Option Groups" tutorial,
     *  infrastructure/presentation/bebop/doc/demo/form-option-groups.xml
     */
    static Page buildPageOptionGroup() {

        // Set up the page
        Page page = new Page("McStas's Sandwich Joint (basic: OptionGroup)");
        page.add(new Label(GlobalizationUtil.globalize("bebop.demo.customize_your_sandwich")));

        // Create the form
        Form form = new Form("sandwich");

        form.add(new Label(GlobalizationUtil.globalize("bebop.demo.protein")));
        RadioGroup proteinWidget = new RadioGroup(new StringParameter("protein"));
        proteinWidget.addOption(new Option("beef", "Beef"));
        proteinWidget.addOption(new Option("chicken", "Chicken"));
        proteinWidget.addOption(new Option("rat", "Rodent Surprise"));
        proteinWidget.setOptionSelected("rat");
        proteinWidget.addValidationListener(new NotNullValidationListener());
        form.add(proteinWidget);

        form.add(new Label(GlobalizationUtil.globalize("bebop.demo.vitamins")));
        CheckboxGroup vitaminsWidget = new CheckboxGroup("vitamins");
        vitaminsWidget.addOption(new Option("lettuce", "Lettuce"));
        vitaminsWidget.addOption(new Option("tomato", "Tomato"));
        vitaminsWidget.addOption(new Option("onion", "Onions"));
        vitaminsWidget.addOption(new Option("pickle", "Pickles"));
        vitaminsWidget.setOptionSelected("lettuce");
        vitaminsWidget.setOptionSelected("tomato");
        form.add(vitaminsWidget);

        form.add(new Label(GlobalizationUtil.globalize("bebop.demo.sauce")));
        MultipleSelect sauceWidget = new MultipleSelect("sauce");
        sauceWidget.addOption(new Option("mayo", "Mayonanise"));
        sauceWidget.addOption(new Option("ketchup", "Ketchup"));
        sauceWidget.addOption(new Option("fungus", "Secret Sauce"));
        form.add(sauceWidget);

        form.add(new Label(GlobalizationUtil.globalize("bebop.demo.payment_method")));
        SingleSelect paymentWidget
            = new SingleSelect(new StringParameter("payment"));
        paymentWidget.addOption(new Option("cash", "Cash"));
        paymentWidget.addOption(new Option("check", "Check"));
        paymentWidget.addOption(new Option("credit", "Credit"));
        paymentWidget.addValidationListener(new NotNullValidationListener());
        form.add(paymentWidget);

        // Use an anonymous inner class as a process listener for the form
        form.addProcessListener(
                                new FormProcessListener() {
                                    public void process(FormSectionEvent e) {
                                        FormData data = e.getFormData();

                                        String protein, payment;
                                        String[] vitamins, sauce;
                                        int i;

                                        protein = (String)data.get("protein");
                                        vitamins = (String[])data.get("vitamins");
                                        sauce = (String[])data.get("sauce");
                                        payment = (String)data.get("payment");

                                        System.out.println("Pretend this is a database transaction:");

                                        System.out.println("Protein: " + protein);

                                        if (vitamins != null) {
                                            System.out.print("Vitamins:");
                                            for (i=0; i < vitamins.length; i++) {
                                                System.out.print(" " + vitamins[i]);
                                            }
                                            System.out.println();
                                        }

                                        if (sauce != null) {
                                            System.out.print("Sauce:");
                                            for (i=0; i < sauce.length; i++) {
                                                System.out.print(" " + sauce[i]);
                                            }
                                            System.out.println();
                                        }

                                        System.out.println("Payment: " + payment);

                                        System.out.println("Thank you for shopping at McStas's.");

                                    }
                                }
                                );

        form.add(new Submit("Purchase"),
                 ColumnPanel.RIGHT | ColumnPanel.FULL_WIDTH);

        page.add(form);

        // Finish the page
        page.lock();

        return page;
    }

    /** Sample page from the tutorial "Widget Print Listenters" in
     *  infrastructure/presentation/bebop/doc/demo/form-print-listeners.xml
     */
    static Page buildPageFormPrintListener() {

        // Set up the page
        Page page = new Page("McStas's Sandwich Joint (with PrintListenter)");
        page.add(new Label(GlobalizationUtil.globalize("bebop.demo.customize_your_sandwich")));
        page.add(new Label(GlobalizationUtil.globalize("bebop.demo.now_with_100_more_tofu_")));

        // Create the form
        Form form = new Form("sandwich");

        form.add(new Label(GlobalizationUtil.globalize("bebop.demo.protein")));
        RadioGroup proteinWidget
            = new RadioGroup(new StringParameter("protein"));

        try {
            proteinWidget.addPrintListener(
                                           new PrintListener() {
                                               public void prepare(PrintEvent e) {
                                                   OptionGroup o = (OptionGroup)e.getTarget();

                                                   // Retrieve the FormData object so that we can set the
                                                   // default value in it
                                                   FormData data = o.getForm().getFormData(e.getPageState());

                                                   // Randomly determine whether the user is vegetarian
                                                   if (Math.random() >= 0.5) {
                                                       o.addOption(new Option("beef", "Beef"));
                                                       o.addOption(new Option("chicken", "Chicken"));
                                                       o.addOption(new Option("rat", "Rodent Surprise"));

                                                       // Pre-fill the value for carnivores
                                                       data.put("protein", "rat");
                                                   } else {
                                                       o.addOption(new Option("fried_tofu", "Fried Tofu"));
                                                       o.addOption(new Option("steamed_tofu", "Steamed Tofu"));
                                                       o.addOption(new Option("rat", "Tofu Surprise"));

                                                       // Pre-fill the value for vegetarians
                                                       data.put("protein", "fried_tofu");
                                                   }
                                               }
                                           }
                                           );
        } catch (TooManyListenersException e) {
            throw new UncheckedWrapperException("This cannot happen", e);
        }

        proteinWidget.addValidationListener(new NotNullValidationListener());
        form.add(proteinWidget);

        form.add(new Label(GlobalizationUtil.globalize("bebop.demo.vitamins")));
        CheckboxGroup vitaminsWidget = new CheckboxGroup("vitamins");
        vitaminsWidget.addOption(new Option("lettuce", "Lettuce"));
        vitaminsWidget.addOption(new Option("tomato", "Tomato"));
        vitaminsWidget.addOption(new Option("onion", "Onions"));
        vitaminsWidget.addOption(new Option("pickle", "Pickles"));
        vitaminsWidget.setOptionSelected("lettuce");
        vitaminsWidget.setOptionSelected("tomato");
        form.add(vitaminsWidget);

        form.add(new Label(GlobalizationUtil.globalize("bebop.demo.sauce")));
        MultipleSelect sauceWidget = new MultipleSelect("sauce");
        sauceWidget.addOption(new Option("mayo", "Mayonanise"));
        sauceWidget.addOption(new Option("ketchup", "Ketchup"));
        sauceWidget.addOption(new Option("fungus", "Secret Sauce"));
        form.add(sauceWidget);

        form.add(new Label(GlobalizationUtil.globalize("bebop.demo.payment_method")));
        SingleSelect paymentWidget
            = new SingleSelect(new StringParameter("payment"));
        paymentWidget.addOption(new Option("cash", "Cash"));
        paymentWidget.addOption(new Option("check", "Check"));
        paymentWidget.addOption(new Option("credit", "Credit"));
        paymentWidget.addValidationListener(new NotNullValidationListener());
        form.add(paymentWidget);

        // Use an anonymous inner class as a process listener for the form
        form.addProcessListener(
                                new FormProcessListener() {
                                    public void process(FormSectionEvent e) {
                                        FormData data = e.getFormData();

                                        String protein, payment;
                                        String[] vitamins, sauce;
                                        int i;

                                        protein = (String)data.get("protein");
                                        vitamins = (String[])data.get("vitamins");
                                        sauce = (String[])data.get("sauce");
                                        payment = (String)data.get("payment");

                                        System.out.println("Pretend this is a database transaction:");

                                        System.out.println("Protein: " + protein);

                                        if (vitamins != null) {
                                            System.out.print("Vitamins:");
                                            for (i=0; i < vitamins.length; i++) {
                                                System.out.print(" " + vitamins[i]);
                                            }
                                            System.out.println();
                                        }

                                        if (sauce != null) {
                                            System.out.print("Sauce:");
                                            for (i=0; i < sauce.length; i++) {
                                                System.out.print(" " + sauce[i]);
                                            }
                                            System.out.println();
                                        }

                                        System.out.println("Payment: " + payment);

                                        System.out.println("Thank you for shopping at McStas's.");

                                    }
                                }
                                );

        form.add(new Submit("Purchase"),
                 ColumnPanel.RIGHT | ColumnPanel.FULL_WIDTH);

        page.add(form);

        // Finish the page
        page.lock();
        return page;
    }

    /**
     * Define a form section which can be reused in multiple forms
     * From tutorial demo/form-sections.xml
     */
    public static class DeliverySection extends FormSection {

        // Construct the section
        public DeliverySection() {
            super();

            Label sectionHeader = new Label(GlobalizationUtil.globalize("bebop.demo.delivery"));
            sectionHeader.setFontWeight(Label.BOLD);
            add(sectionHeader, BlockStylable.FULL_WIDTH | BlockStylable.LEFT);

            add(new Label(GlobalizationUtil.globalize("bebop.demo.payment_method")));
            SingleSelect paymentWidget
                = new SingleSelect(new StringParameter("payment"));
            paymentWidget.addOption(new Option("cash", "Cash"));
            paymentWidget.addOption(new Option("check", "Check"));
            paymentWidget.addOption(new Option("credit", "Credit"));
            paymentWidget.addValidationListener(new NotNullValidationListener());
            add(paymentWidget);

            add(new Label(GlobalizationUtil.globalize("bebop.demo.shipment_method")));
            SingleSelect shipWidget
                = new SingleSelect(new StringParameter("shipment"));
            shipWidget.addOption(new Option("ups", "UPS Ground (add $1.00)"));
            shipWidget.addOption(new Option("fedex",
                                            "FedEx Next Day Air (add $5.00)"));
            shipWidget.addOption(new Option("cannon", "Burger Cannon (free)"));
            shipWidget.addValidationListener(new NotNullValidationListener());
            add(shipWidget);

            add(new Label("Note: Due to some regretful " +
                          "inicidents, you must pay Cash should you choose to ship by " +
                          "Burger Cannon"), BlockStylable.FULL_WIDTH | BlockStylable.CENTER);

            addValidationListener(
                                  new FormValidationListener() {
                                      static final String CASH = "cash";
                                      static final String CANNON = "cannon";

                                      @Override
                                      public void validate(FormSectionEvent e)
                                          throws FormProcessException {
                                          FormData data = e.getFormData();
                                          String payment = (String)data.get("payment");
                                          String shipment = (String)data.get("shipment");

                                          if ( ! CASH.equals(payment) && CANNON.equals(shipment)) {
                                              throw new FormProcessException(
                                                      "Must pay cash when shipping via Burger Cannon",
                                                      globalize("(key missing)") );
                                          }
                                      }
                                  }
                                  );

            addProcessListener(
                               new FormProcessListener() {
                                   public void process(FormSectionEvent e) {
                                       FormData data = e.getFormData();

                                       System.out.println("Here is where credit card authentication " +
                                                          "would be performed.");

                                       System.out.println("Payment: " + data.get("payment"));
                                       System.out.println("Shipment: " + data.get("shipment"));
                                   }
                               }
                               );
        }
    }

    /**
     * Construct and initialize the page
     */
    static Page buildPageFormSection() {

        // Set up the page
        Page page = new Page("McStas's Sandwich Joint (with FormSection)");
        page.add(new Label(GlobalizationUtil.globalize("bebop.demo.customize_your_sandwich")));

        // Create the form
        Form form = new Form("sandwich");

        Label mainHeader = new Label(GlobalizationUtil.globalize("bebop.demo.purchase"));
        mainHeader.setFontWeight(Label.BOLD);
        form.add(mainHeader, BlockStylable.FULL_WIDTH | BlockStylable.LEFT);

        form.add(new Label(GlobalizationUtil.globalize("bebop.demo.protein")));
        RadioGroup proteinWidget = new RadioGroup(new StringParameter("protein"));
        proteinWidget.addOption(new Option("beef", "Beef"));
        proteinWidget.addOption(new Option("chicken", "Chicken"));
        proteinWidget.addOption(new Option("rat", "Rodent Surprise"));
        proteinWidget.setOptionSelected("rat");
        proteinWidget.addValidationListener(new NotNullValidationListener());
        form.add(proteinWidget);

        form.add(new Label(GlobalizationUtil.globalize("bebop.demo.vitamins")));
        CheckboxGroup vitaminsWidget = new CheckboxGroup("vitamins");
        vitaminsWidget.addOption(new Option("lettuce", "Lettuce"));
        vitaminsWidget.addOption(new Option("tomato", "Tomato"));
        vitaminsWidget.addOption(new Option("onion", "Onions"));
        vitaminsWidget.addOption(new Option("pickle", "Pickles"));
        vitaminsWidget.setOptionSelected("lettuce");
        vitaminsWidget.setOptionSelected("tomato");
        form.add(vitaminsWidget);

        form.add(new Label(GlobalizationUtil.globalize("bebop.demo.sauce")));
        MultipleSelect sauceWidget = new MultipleSelect("sauce");
        sauceWidget.addOption(new Option("mayo", "Mayonanise"));
        sauceWidget.addOption(new Option("ketchup", "Ketchup"));
        sauceWidget.addOption(new Option("fungus", "Secret Sauce"));
        form.add(sauceWidget);

        form.add(new DeliverySection());

        form.add(new FormErrorDisplay(form), BlockStylable.FULL_WIDTH);

        // Use an anonymous inner class as a process listener for the form
        form.addProcessListener(
                                new FormProcessListener() {
                                    public void process(FormSectionEvent e) {
                                        FormData data = e.getFormData();

                                        String protein, payment, shipment;
                                        String[] vitamins, sauce;
                                        int i;

                                        protein = (String)data.get("protein");
                                        vitamins = (String[])data.get("vitamins");
                                        sauce = (String[])data.get("sauce");
                                        payment = (String)data.get("payment");
                                        shipment = (String)data.get("shipment");

                                        System.out.println("Pretend this is a database transaction:");

                                        System.out.println("  Protein: " + protein);

                                        if (vitamins != null) {
                                            System.out.print("  Vitamins:");
                                            for (i=0; i < vitamins.length; i++) {
                                                System.out.print(" " + vitamins[i]);
                                            }
                                            System.out.println();
                                        }

                                        if (sauce != null) {
                                            System.out.print("  Sauce:");
                                            for (i=0; i < sauce.length; i++) {
                                                System.out.print(" " + sauce[i]);
                                            }
                                            System.out.println();
                                        }

                                        System.out.println("  Thank you for shopping at McStas's.");

                                    }
                                }
                                );

        form.add(new Submit("Purchase"),
                 BlockStylable.RIGHT | BlockStylable.FULL_WIDTH);

        page.add(form);

        // Finish the page
        page.lock();
        return page;
    }

    /**
     * Construct and initialize the page
     */
    static Page buildPageControlPanel_1() {
        Page page = new Page("Animal Kingdom");

        // Create the navbar
        ColumnPanel navBarPanel = new ColumnPanel(1);
        navBarPanel.add(new Label(
                                  "&lt;ul&gt; " +
                                  "  &lt;li&gt;&lt;b&gt;Lambs&lt;/b&gt;&lt;/li&gt;" +
                                  "  &lt;li&gt;&lt;a href=\"wolves\"&gt;Wolves&lt;/a&gt;&lt;/li&gt;" +
                                  "  &lt;li&gt;&lt;a href=\"slugs\"&gt;Slugs&lt;/a&gt;&lt;/li&gt;" +
                                  "&lt;/ul&gt;"
                                  ), ColumnPanel.LEFT);

        // Create the inner panel
        ColumnPanel innerPanel = new ColumnPanel(2);
        innerPanel.setBorder(false);
        innerPanel.setPadColor("#DDDDDD");
        innerPanel.setPadFrameWidth("5");
        innerPanel.add(navBarPanel, ColumnPanel.LEFT);
        innerPanel.add(new Label(
                                 "Mary had a little lamb,&lt;br&gt;" +
                                 "Its fleece was white as snow.&lt;br&gt;" +
                                 "And everywhere that Mary went&lt;br&gt;" +
                                 "The Lamb was sure to go.&lt;br&gt;"
                                 ));

        // Create the outer panel
        ColumnPanel outerPanel = new ColumnPanel(1);
        outerPanel.setBorder(false);
        outerPanel.setPadColor("#FFFFFF");
        outerPanel.add(new Label(GlobalizationUtil.globalize("bebop.demo.animal_kingdom_little_lamb")));
        outerPanel.add(innerPanel);
        outerPanel.add(new Label(
                                 "&lt;hr&gt; " +
                                 "  &lt;font size=-1&gt;All rights reserved. " +
                                 "  Void where prohibited. " +
                                 "  Actual color of animal may vary.&lt;/font&gt;"
                                 ), ColumnPanel.BOTTOM | ColumnPanel.CENTER);

        // Complete the page
        page.add(outerPanel);
        page.lock();
        return page;
    }

    /**
     * @return a page demonstrating Multiple
     */
    static Page buildPageMultiple() {
        Page p = new Page("Multiple");
        GridPanel panel = new GridPanel(2);
        Multiple m = new Multiple
            (new QueryRowsBuilder("com.arsdigita.kernel.UserPrimaryEmail"));
        Container row = new SimpleContainer(BEBOP_PANELROW, m.BEBOP_XML_NS);
        row.add(new Column(m, "userID"));
        row.add(new Column(m, "primaryEmailAddress"));
        m.add(row);
        panel.add(m, BlockStylable.INSERT);
        p.add(panel);
        p.lock();
        return p;
    }

    /**
     * Try out state parameters, to check on SDM bug #197656
     */
    static Page buildPageStateParameter() {
        Page pg = new Page("StateParameter");
        pg.add(new ActionLink( (String) GlobalizationUtil.globalize("bebop.demo.no_action").localize()) {
                public void register(Page p) {
                    super.register(p);
                    p.addGlobalStateParam(new DateParameter("foo"));
                }
            }
               );
        pg.lock();
        return pg;
    }

    static Page buildPageControlLink() {
        Page p = new Page("ContolLink");
        p.add(new Foo());
        p.add(new ControlLink("Set foo to \"" + Foo.aValue + "\""));
        p.lock();
        return p;
    }
}
