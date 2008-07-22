/*
 * FilterPanel.java
 * Copyright 2008 Connor Petty <cpmeister@users.sourceforge.net>
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * Created on Jun 17, 2008, 11:46:31 PM
 */
package pcgen.gui.filter;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.undo.StateEditable;
import pcgen.gui.PCGenUIManager;
import pcgen.gui.facade.CharacterFacade;
import pcgen.gui.util.GenericListModel;
import pcgen.gui.util.GenericListModelWrapper;
import pcgen.gui.util.SimpleTextIcon;
import pcgen.gui.util.event.ListDataAdapter;
import pcgen.util.PropertyFactory;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class FilterPanel extends JPanel implements StateEditable
{

    private static final String filterString = PropertyFactory.getString("in_filter") +
            ":";
    private static final String clearString = PropertyFactory.getString("in_clear");
    private static final String advancedString = PropertyFactory.getString("in_demAdv");
    private final List<JToggleButton> filterbuttons;
    private final JTextField textfield;
    private FilterPanelListener panelListener = null;
    private ListDataListener listListener;
    private List<Filter> selectedFilters;
    private Class<?> filterClass;

    public FilterPanel()
    {
        this.filterbuttons = new LinkedList<JToggleButton>();
        this.textfield = new JTextField();
        initComponents();
    }

    private void initComponents()
    {
        setLayout(new FilterLayout());

        JToolBar toolbar = new JToolBar();
        toolbar.setRollover(true);
        toolbar.setFloatable(false);

        JButton button = new JButton();
        button.setFocusable(false);

        Icon icon = new SimpleTextIcon(button, filterString);
        button.setIcon(icon);

        icon = new SimpleTextIcon(button, clearString);
        button.setRolloverIcon(icon);
        button.setPressedIcon(icon);

        button.addActionListener(new ActionListener()
                         {

                             public void actionPerformed(ActionEvent e)
                             {
                                 textfield.setText("");
                             }

                         });
        toolbar.add(button);

        textfield.getDocument().addDocumentListener(
                new DocumentListener()
                {

                    public void insertUpdate(DocumentEvent e)
                    {
                        fireApplyFilter();
                    }

                    public void removeUpdate(DocumentEvent e)
                    {
                        fireApplyFilter();
                    }

                    public void changedUpdate(DocumentEvent e)
                    {
                        fireApplyFilter();
                    }

                });

        toolbar.add(textfield);

        button = new JButton(new AdvancedAction());
        button.setFocusable(false);
        toolbar.add(button);

        toolbar.addSeparator();

        add(toolbar);
    }

    private <T> void setFilterClass(Class<T> filterClass)
    {
        if (this.filterClass != null)
        {
            PCGenUIManager.getDisplayedFilters(this.filterClass).removeListDataListener(listListener);
        }
        this.filterClass = filterClass;
        GenericListModel<NamedFilter<? super T>> filterModel = PCGenUIManager.getDisplayedFilters(filterClass);
        final List<NamedFilter<? super T>> filterList = new GenericListModelWrapper<NamedFilter<? super T>>(filterModel);
        ListDataListener listener = new ListDataAdapter()
        {

            @Override
            public void listDataChanged(ListDataEvent e)
            {
                setFilterButtons(filterList);
            }

        };
        filterModel.addListDataListener(listener);
        listListener = listener;
        setFilterButtons(filterList);
    }

    public void setFilterPanelListener(FilterPanelListener listener)
    {
        this.panelListener = listener;
    }

    private <T> void setFilterButtons(List<NamedFilter<? super T>> filters)
    {
        for (JToggleButton button : filterbuttons)
        {
            remove(button);
        }
        filterbuttons.clear();

        boolean updateFilters = selectedFilters.retainAll(filters);

        for (NamedFilter<? super T> filter : filters)
        {
            JToggleButton button = new JToggleButton(new FilterAction(filter));
            button.setSelected(selectedFilters.contains(filter));
            filterbuttons.add(button);
            add(button);
        }

        if (updateFilters)
        {
            fireApplyFilter();
        }
    }

    public Hashtable<Object, Object> createState(Class<?> filterClass)
    {
        Hashtable<Object, Object> state = new Hashtable<Object, Object>();
        state.put("FilterClass", filterClass);
        state.put("SelectedFilters", new ArrayList<Filter>());
        return state;
    }

    public void storeState(Hashtable<Object, Object> state)
    {

    }

    @SuppressWarnings("unchecked")
    public void restoreState(Hashtable<?, ?> state)
    {
        Class<?> c = (Class<?>) state.get("FilterClass");
        selectedFilters = (List<Filter>) state.get("SelectedFilters");
        setFilterClass(c);
    }

    public Filter getFilter()
    {
        String text = textfield.getText();
        boolean qFilter = text != null && !text.equals("");
        return new ObjectFilter(text, qFilter);
    }

    private void fireApplyFilter()
    {
        if (panelListener != null)
        {
            ObjectFilter filter = (ObjectFilter) getFilter();
            panelListener.applyFilter(filter, filter.isQuickSearch());
        }
    }

    private class ObjectFilter implements Filter
    {

        private final boolean qFilter;
        private final String text;

        public ObjectFilter(String text, boolean qFilter)
        {
            this.qFilter = qFilter;
            this.text = text;
        }

        public boolean isQuickSearch()
        {
            return qFilter;
        }

        @SuppressWarnings("unchecked")
        public boolean accept(CharacterFacade character, Object object)
        {
            boolean accept = !qFilter || text.equals(object.toString());
            if (accept)
            {
                for (Filter filter : selectedFilters)
                {
                    if (!filter.accept(character, object))
                    {
                        accept = false;
                        break;
                    }
                }
            }
            return accept;
        }

    }

    private class FilterAction extends AbstractAction
    {

        private NamedFilter<?> filter;

        public FilterAction(NamedFilter<?> filter)
        {
            this.filter = filter;
            putValue(NAME, filter.getName());
            putValue(SHORT_DESCRIPTION, filter.getShortDescription());
            putValue(LONG_DESCRIPTION, filter.getLongDescription());
        }

        public void actionPerformed(ActionEvent e)
        {
            if (selectedFilters.contains(filter))
            {
                selectedFilters.remove(filter);
            }
            else
            {
                selectedFilters.add(filter);
            }
            fireApplyFilter();
        }

    }

    private class AdvancedAction extends AbstractAction
    {

        public AdvancedAction()
        {
            super(advancedString);
        }

        public void actionPerformed(ActionEvent e)
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }

    private static class FilterLayout extends FlowLayout
    {

        public FilterLayout()
        {
            super(FlowLayout.LEFT);
        }

        @Override
        public Dimension preferredLayoutSize(Container target)
        {
            synchronized (target.getTreeLock())
            {
                Dimension dim = new Dimension(0, 0);
                int nmembers = target.getComponentCount();

                Insets insets = target.getInsets();
                int maxwidth = target.getWidth() - (insets.left + insets.right +
                        getHgap() * 2);
                int width = 0;
                int height = 0;
                int component = 0;
                for (int i = 0; i < nmembers; i++, component++)
                {
                    Component m = target.getComponent(i);
                    if (m.isVisible())
                    {
                        Dimension d = m.getPreferredSize();
                        if (component > 0)
                        {
                            if (width + d.width > maxwidth)
                            {
                                dim.width = Math.max(dim.width, width);
                                dim.height += height + getVgap();
                                width = 0;
                                height = 0;
                                component = 0;
                            }
                            width += getHgap();
                        }
                        height = Math.max(height, d.height);
                        width += d.width;
                    }
                }
                dim.width = Math.max(dim.width, width);
                dim.height += height;

                dim.width += insets.left + insets.right + getHgap() * 2;
                dim.height += insets.top + insets.bottom + getVgap() * 2;
                return dim;
            }
        }

        @Override
        public Dimension minimumLayoutSize(Container target)
        {
            synchronized (target.getTreeLock())
            {
                Dimension dim = new Dimension(0, 0);
                int nmembers = target.getComponentCount();

                Insets insets = target.getInsets();
                int maxwidth = target.getWidth() - (insets.left + insets.right +
                        getHgap() * 2);
                int width = 0;
                int height = 0;
                int component = 0;
                for (int i = 0; i < nmembers; i++, component++)
                {
                    Component m = target.getComponent(i);
                    if (m.isVisible())
                    {
                        Dimension d = m.getMinimumSize();
                        if (component > 0)
                        {
                            if (width + d.width > maxwidth)
                            {
                                dim.width = Math.max(dim.width, width);
                                dim.height += height + getVgap();
                                width = 0;
                                height = 0;
                                component = 0;
                            }
                            width += getHgap();
                        }
                        height = Math.max(height, d.height);
                        width += d.width;
                    }
                }
                dim.width = Math.max(dim.width, width);
                dim.height += height;

                dim.width += insets.left + insets.right + getHgap() * 2;
                dim.height += insets.top + insets.bottom + getVgap() * 2;
                return dim;
            }
        }

    }
}
