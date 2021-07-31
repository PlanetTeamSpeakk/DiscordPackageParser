package com.ptsmods.packageparser.gui;

import com.ptsmods.packageparser.PackageParser;
import com.ptsmods.packageparser.discord.misc.Payment;
import com.ptsmods.packageparser.discord.user.Relationship;
import com.ptsmods.packageparser.discord.user.User;
import com.ptsmods.packageparser.discord.usersettings.Connection;
import com.ptsmods.packageparser.discord.usersettings.CustomStatus;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class UserPanel extends JPanel {
    public GeneralPanel generalPanel;

    UserPanel() {
        setLayout(new GridLayout(0, 1));
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("General", generalPanel = new GeneralPanel());
        tabs.addTab("Settings", new SettingsPanel());
        tabs.addTab("Payments", getVerticallyScrollableScrollPane(new PaymentsPanel()));
        tabs.addTab("Relationships", getVerticallyScrollableScrollPane(new RelationshipsPanel()));
        tabs.addTab("Connections", getVerticallyScrollableScrollPane(new ConnectionsPanel()));
        add(tabs);
    }

    private JScrollPane getVerticallyScrollableScrollPane(JComponent component) {
        JScrollPane scrollPane = new JScrollPane(component);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        // Delegating events, required for ResizableGridPanel to work.
        return scrollPane;
    }

    public static class GeneralPanel extends JPanel {
        private GeneralPanel() {
            setLayout(new GridBagLayout());
            JPanel content = new JPanel();
            content.setLayout(new GridBagLayout());
            User user = PackageParser.getUser();

            content.add(createField("Tag", user.getTag()), createBasicConstraints(0));
            content.add(createField("ID", String.valueOf(user.getId())), createBasicConstraints(1));
            content.add(createField("E-mail address", user.getEmail()), createBasicConstraints(2));
            content.add(createField("IP", user.getIp()), createBasicConstraints(3));
            content.add(createField("Created at", new SimpleDateFormat("MMMM d yyyy HH:mm:ss").format(user.getCreatedAt())), createBasicConstraints(4));
            content.add(new JLabel(new ImageIcon(PackageParser.getScaledInstance(PackageParser.getAvatar(), 256, 256))),
                        new GridBagConstraints(3, 0, 5, 5, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
            content.setPreferredSize(new Dimension(696, 387)); // Default size of the parent layout.
            add(content);
        }

        private GridBagConstraints createBasicConstraints(int y) {
            return new GridBagConstraints(0, y, 1, 1, 1, 1, GridBagConstraints.NORTHEAST, GridBagConstraints.BOTH, new Insets(10, 10, 0, 0), 0, 0);
        }

        private JPanel createField(String title, String content) {
            title += " "; // Letters tend to get cut off when aligned to the right, especially when the text is bold.
            content += " "; // Preventing content from being misaligned from the title
            JPanel panel = new JPanel();
            panel.setLayout(new GridBagLayout());
            JLabel titleLabel = new JLabel(title);
            titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 14));
            panel.add(titleLabel, new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
            JLabel contentLabel = new JLabel(content);
            contentLabel.setFont(contentLabel.getFont().deriveFont(14f));
            panel.add(contentLabel, new GridBagConstraints(0, 1, 1, 1, 1, 0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
            return panel;
        }
    }

    private static class SettingsPanel extends ResizableGridPanel {
        private SettingsPanel() {
            super(5, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL);
            User.Settings settings = PackageParser.getUser().getSettings();
            int fieldCount = 0;
            for (Method method : Arrays.stream(User.Settings.class.getDeclaredMethods()).sorted(Comparator.comparing(m -> cleanName(m.getName()))).toArray(Method[]::new)) {
                try {
                    if ("toString".equals(method.getName())) continue;
                    Object rawValue = method.invoke(settings);
                    Class<?> c = method.getReturnType();
                    String s = null;
                    if (c == boolean.class) s = (boolean) rawValue ? "Yes" : "No";
                    else if (c == String.class) s = (String) rawValue;
                    else if (Enum.class.isAssignableFrom(c)) s = ((Enum<?>) rawValue).name().replace('_', ' ');
                    else if (c == CustomStatus.class) s = ((CustomStatus) rawValue).getText();
                    if (s != null) {
                        addGridComponent(new JLabel("<html><b>" + cleanName(method.getName()) + "</b><br>" + s + "</html>"), new Insets(fieldCount < 5 ? 10 : 0, 5, 0, 0));
                        fieldCount++;
                    }
                } catch (IllegalAccessException | InvocationTargetException ignored) {}
            }
        }

        private String cleanName(String name) {
            for (int i = 0; i < name.length(); i++)
                if (Character.isUpperCase(name.charAt(i))) {
                    name = name.substring(i);
                    break;
                }
            StringBuilder output = new StringBuilder();
            for (char ch : name.toCharArray())
                output.append(Character.isUpperCase(ch) ? " " : "").append(ch);
            return output.substring(1);
        }
    }

    private static class PaymentsPanel extends ResizableGridPanel {
        private PaymentsPanel() {
            super(4, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL);
            List<Payment> payments = PackageParser.getUser().getPayments();
            if (payments.isEmpty()) {
                JLabel label = new JLabel("You have not made any purchases on Discord yet.");
                label.setFont(label.getFont().deriveFont(Font.BOLD, 24));
                add(label);
            } else {
                DecimalFormat decimalFormat = new DecimalFormat("#.00");
                int total = payments.stream().mapToInt(Payment::getAmount).sum();
                int gifted = payments.stream().filter(payment -> payment.getDescription().toLowerCase(Locale.ROOT).contains("gift")).mapToInt(Payment::getAmount).sum();

                addGridHeaderComponent(new JLabel("<html><b>Total made:</b> " + payments.size() + "</html>"), new Insets(10, 10, 25, 0));
                addGridHeaderComponent(new JLabel("<html><b>Total spent:</b> " + decimalFormat.format(total / 100D) + "</html>"), new Insets(10, 10, 25, 0));
                addGridHeaderComponent(new JLabel("<html><b>Total gifted:</b> " + decimalFormat.format(gifted / 100D) + "</html>"), new Insets(10, 10, 25, 0));
                addGridHeaderComponent(new JLabel("<html><b>% gifted:</b> " + decimalFormat.format(gifted / (double) total * 100) + "</html>"), new Insets(10, 10, 25, 0));

                int fieldCount = 0;
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d yyyy HH:mm");
                for (Payment payment : payments) {
                    addGridComponent(new JLabel("<html><b>" + payment.getDescription() + "</b><br>" + decimalFormat.format(payment.getAmount() / 100D) + " " + payment.getCurrency().toUpperCase(Locale.ROOT) + "<br>Made at " + dateFormat.format(payment.getCreatedAt()) + "</html>"), new Insets(fieldCount < 5 ? 10 : 5, 10, 5, 0));
                    fieldCount++;
                }
            }
        }
    }

    private static class RelationshipsPanel extends ResizableGridPanel {
        private RelationshipsPanel() {
            super(4, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL);
            List<Relationship> relationships = PackageParser.getUser().getRelationships();
            relationships.sort(Comparator.comparing(Relationship::getTag));
            if (relationships.isEmpty()) {
                JLabel label = new JLabel("You do not have any relationships on Discord yet.");
                label.setFont(label.getFont().deriveFont(Font.BOLD, 24));
                add(label);
            } else {
                addGridHeaderComponent(new JLabel("<html><b>Total friends:</b> " + relationships.stream().filter(relationship -> relationship.getType() == Relationship.Type.FRIEND).count() + "</html>"), new Insets(10, 10, 25, 0));
                addGridHeaderComponent(new JLabel("<html><b>Total blocked:</b> " + relationships.stream().filter(relationship -> relationship.getType() == Relationship.Type.BLOCKED).count() + "</html>"), new Insets(10, 10, 25, 0));
                addGridHeaderComponent(new JLabel("<html><b>Total incoming:</b> " + relationships.stream().filter(relationship -> relationship.getType() == Relationship.Type.PENDING_INCOMING).count() + "</html>"), new Insets(10, 10, 25, 0));
                addGridHeaderComponent(new JLabel("<html><b>Total outgoing:</b> " + relationships.stream().filter(relationship -> relationship.getType() == Relationship.Type.PENDING_OUTGOING).count() + "</html>"), new Insets(10, 10, 25, 0));

                int fieldCount = 0;
                for (Relationship relationship : relationships) {
                    addGridComponent(new JLabel("<html><b>" + relationship.getTag() + "</b><br>" + relationship.getType().name().replace('_', ' ') + "</html>"), new Insets(fieldCount < 5 ? 10 : 5, 10, 5, 0));
                    fieldCount++;
                }
            }
        }
    }

    private static class ConnectionsPanel extends ResizableGridPanel {
        private ConnectionsPanel() {
            super(4, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL);
            setLayout(new GridBagLayout());
            List<Connection> connections = PackageParser.getUser().getConnections();
            connections.sort(Comparator.comparing(Connection::getType));
            if (connections.isEmpty()) {
                JLabel label = new JLabel("You do not have any connections on Discord yet.");
                label.setFont(label.getFont().deriveFont(Font.BOLD, 24));
                add(label);
            } else {
                int fieldCount = 0;
                for (Connection connection : connections) {
                    addGridComponent(new ConnectionPane(connection), new Insets(fieldCount < 5 ? 10 : 5, 10, 5, 0));
                    fieldCount++;
                }
            }
        }
    }
}
