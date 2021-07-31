package com.ptsmods.packageparser.gui;

import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class ResizableGridPanel extends JPanel implements Scrollable {
    private final int anchor, fill;
    private final List<Component> gridHeaderComponents = new ArrayList<>(), gridComponents = new ArrayList<>(), emptyComponents = new ArrayList<>();
    private int currentWidth;

    public ResizableGridPanel(int defaultWidth, int anchor, int fill) {
        currentWidth = defaultWidth;
        this.anchor = anchor;
        this.fill = fill;
        GridBagLayout layout = new GridBagLayout();
        setLayout(layout);
        AtomicInteger columnWidth = new AtomicInteger(-1);
        AtomicReference<Dimension> lastResize = new AtomicReference<>(new Dimension(0, 0));
        AtomicBoolean shown = new AtomicBoolean(false);
        AtomicBoolean lastShown = new AtomicBoolean(false);
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                // If-statement to make resizing not freeze the application.
                if (Math.abs(getSize().width - lastResize.get().width) > 10 || shown.get() != lastShown.get()) {
                    currentWidth = getSize().width / columnWidth.get();
                    for (Component emptyComponent : emptyComponents) remove(emptyComponent);
                    int emptyComponentCount = (int) (Math.ceil(gridHeaderComponents.size() / (float) currentWidth)) * currentWidth - gridHeaderComponents.size();
                    for (int x = 0; x < emptyComponentCount / 2; x++) {
                        Component emptyComponent = createEmptyComponent();
                        add(emptyComponent, getConstraints(x < emptyComponentCount / 2 ? x : x + gridHeaderComponents.size(), null));
                        emptyComponents.add(emptyComponent);
                    }
                    for (int x = 0; x < gridComponents.size(); x++) {
                        Component component = gridComponents.get(x);
                        layout.setConstraints(component, getConstraints((gridHeaderComponents.contains(component) ? emptyComponentCount / 2 : emptyComponentCount) + x, layout.getConstraints(component).insets));
                    }
                    SwingUtilities.updateComponentTreeUI(ResizableGridPanel.this);
                    lastResize.set(getSize());
                    lastShown.set(shown.get());
                }
            }

            @Override
            public void componentShown(ComponentEvent e) {
                if (columnWidth.get() < 0) {
                    columnWidth.set((getSize().width - (getParent() instanceof JViewport && getParent().getParent() instanceof JScrollPane ? ((JScrollPane) getParent().getParent()).getVerticalScrollBar().getWidth() : 0)) / defaultWidth);
                    shown.set(true);
                    componentResized(e); // Component gets resized before it gets shown which causes the layout to be ordered with a column width of -1 which we fix here.
                }
            }
        });
        addHierarchyListener(event -> {
            // Delegating events
            if (event.getChangedParent() != null && event.getChangedParent().getParent() instanceof JScrollPane)
                event.getChangedParent().getParent().addComponentListener(new ComponentListener() {
                    @Override
                    public void componentResized(ComponentEvent e) {
                        for (ComponentListener listener : getComponentListeners())
                            listener.componentResized(e);
                    }

                    @Override
                    public void componentMoved(ComponentEvent e) {
                        for (ComponentListener listener : getComponentListeners())
                            listener.componentMoved(e);
                    }

                    @Override
                    public void componentShown(ComponentEvent e) {
                        for (ComponentListener listener : getComponentListeners())
                            listener.componentShown(e);
                    }

                    @Override
                    public void componentHidden(ComponentEvent e) {
                        for (ComponentListener listener : getComponentListeners())
                            listener.componentHidden(e);
                    }
                });
        });
    }

    public void addGridComponent(Component component, @Nullable Insets insets) {
        add(component, getConstraints(gridComponents.size(), insets));
        gridComponents.add(component);
    }

    public void addGridHeaderComponent(Component component, @Nullable Insets insets) {
        if (gridHeaderComponents.size() != gridComponents.size()) throw new IllegalArgumentException("Can't add header components after adding normal components.");
        addGridComponent(component, insets);
        gridHeaderComponents.add(component);
    }

    private Component createEmptyComponent() {
        return new JLabel(" ");
    }

    private GridBagConstraints getConstraints(int index, @Nullable Insets insets) {
        return new GridBagConstraints(index % currentWidth, index / currentWidth, 1, 1, 1, 1, anchor, fill, insets == null ? new Insets( 0, 0, 0, 0) : insets, 0, 0);
    }

    @Override
    public Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
    }

    @Override
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 10;
    }

    @Override
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        return ((orientation == SwingConstants.VERTICAL) ? visibleRect.height : visibleRect.width) - 10;
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        return true;
    }

    @Override
    public boolean getScrollableTracksViewportHeight() {
        return false;
    }
}
