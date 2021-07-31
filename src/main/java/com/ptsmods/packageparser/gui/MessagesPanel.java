package com.ptsmods.packageparser.gui;

import com.ptsmods.packageparser.PackageParser;
import com.ptsmods.packageparser.discord.server.Channel;
import com.ptsmods.packageparser.discord.server.Server;
import com.ptsmods.packageparser.discord.usersettings.ServerFolder;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.MutableTreeNode;
import java.awt.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class MessagesPanel extends JPanel {
    private final JTree serverTree;
    private final JScrollPane serverTreeScroll;

    MessagesPanel() {
        setLayout(new GridBagLayout());
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");
        DefaultMutableTreeNode serversNode = new DefaultMutableTreeNode("Servers");
        DefaultMutableTreeNode dmsNode = new DefaultMutableTreeNode("DMs");
        root.add(serversNode);
        root.add(dmsNode);
        java.util.List<Server> servers = PackageParser.getServers();
        List<ServerFolder> serverFolders = PackageParser.getUser().getSettings().getGuildFolders();
        for (int i = 0; i < servers.size(); i++)
            if (serverFolders.size() > 0 && serverFolders.get(0).getServers().size() > 0 && serverFolders.get(0).getServers().get(0).getId() == servers.get(i).getId()) {
                ServerFolder folder = serverFolders.remove(0);
                serversNode.add(createNode(folder));
                i += folder.getServers().size() - 1;
            } else serversNode.add(createNode(servers.get(i)));
        for (Channel channel : PackageParser.getPrivateChannels()) dmsNode.add(new DefaultMutableTreeNode(channel));
        serverTree = new JTree(root);
        ImageIcon serverIconSelected = new ImageIcon(PackageParser.serverIconSelected);
        ImageIcon serverIcon = new ImageIcon(PackageParser.serverIcon);
        ImageIcon channelIconSelected = new ImageIcon(PackageParser.channelIconSelected);
        ImageIcon channelIcon = new ImageIcon(PackageParser.channelIcon);
        serverTree.setCellRenderer(new DefaultTreeCellRenderer() {
            @Override
            public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                Object v = ((DefaultMutableTreeNode) value).getUserObject();
                if (v instanceof ServerFolder) return ((ServerFolder) v).getRenderer().getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
                super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
                setText(
                        v instanceof Server ? ((Server) v).getName() :
                        v instanceof Channel ? ((Channel) v).getName() :
                        String.valueOf(v)
                );
                setIcon(
                        v instanceof Channel ? expanded ? channelIconSelected : channelIcon :
                        expanded ? serverIconSelected : serverIcon
                );
                return this;
            }
        });
        serverTree.setRootVisible(false);
        serverTree.setShowsRootHandles(true);
        add(serverTreeScroll = new JScrollPane(serverTree), new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.PAGE_END, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        MessagesPane messagesPane = new MessagesPane(false, null);
        add(new JScrollPane(messagesPane), new GridBagConstraints(1, 0, 2, 1, 3, 1, GridBagConstraints.PAGE_END, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        serverTree.addTreeSelectionListener(event -> {
            Object comp = serverTree.getLastSelectedPathComponent();
            if (comp != null && (comp = ((DefaultMutableTreeNode) comp).getUserObject()) instanceof Channel) {
                Channel channel = (Channel) comp;
                messagesPane.setText("<html>" + MainGUI.msgsToHtml(channel.getMessages()) + "</html>");
            }
        });
    }

    private MutableTreeNode createNode(ServerFolder folder) {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(folder);
        for (Server server : folder.getServers()) node.add(createNode(server));
        return node;
    }

    private MutableTreeNode createNode(Server server) {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(server);
        for (Channel channel : server.getChannels()) node.add(new DefaultMutableTreeNode(channel));
        return node;
    }

    public void updateServerTree() {
        int verScroll = serverTreeScroll.getVerticalScrollBar().getValue();
        int horScroll = serverTreeScroll.getHorizontalScrollBar().getValue();
        SwingUtilities.updateComponentTreeUI(serverTree);
        serverTreeScroll.getVerticalScrollBar().setValue(verScroll);
        serverTreeScroll.getHorizontalScrollBar().setValue(horScroll);
    }
}
