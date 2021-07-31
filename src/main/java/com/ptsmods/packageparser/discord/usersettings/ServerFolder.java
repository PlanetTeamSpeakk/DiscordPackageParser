package com.ptsmods.packageparser.discord.usersettings;

import com.ptsmods.packageparser.discord.server.Server;
import com.ptsmods.packageparser.PackageParser;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ServerFolder {
    private final List<String> serverIds;
    private final List<Server> servers = new ArrayList<>();
    private final long id;
    private final String name;
    private final Color color;
    private final DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer() {
        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
            setText(ServerFolder.this.getName());
            return this;
        }
    };

    public ServerFolder(List<String> serverIds, long id, String name, Color color) {
        this.serverIds = serverIds;
        this.id = id;
        this.name = name;
        this.color = color;
        renderer.setOpenIcon(new ImageIcon(createIcon(color)));
        renderer.setClosedIcon(new ImageIcon(createIcon(new Color(
                Math.min((int) (color.getRed() + 255 * 0.25f), 255),
                Math.min((int) (color.getGreen() + 255 * 0.25f), 255),
                Math.min((int) (color.getBlue() + 255 * 0.25f), 255)
        ))));
        renderer.setLeafIcon(new ImageIcon(PackageParser.channelIcon));
    }

    private static BufferedImage createIcon(Color c) {
        BufferedImage icon = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = icon.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setColor(c);
        Ellipse2D.Double circle = new Ellipse2D.Double(0, 0, 16, 16);
        g.fill(circle);
        g.dispose();
        return icon;
    }

    public List<Server> getServers() {
        return servers;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Color getColor() {
        return color;
    }

    public TreeCellRenderer getRenderer() {
        return renderer;
    }

    public void fill(List<Server> servers) {
        this.servers.clear();
        this.servers.addAll(serverIds.stream().map(id -> servers.stream().filter(s -> s.getId() == Long.parseLong(id)).findFirst().orElse(null)).filter(Objects::nonNull).collect(Collectors.toList()));
    }

    @Override
    public String toString() {
        return "ServerFolder{" +
                "servers=" + servers +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", color=" + color +
                '}';
    }
}
