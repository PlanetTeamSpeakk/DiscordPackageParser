package com.ptsmods.packageparser.gui;

import com.ptsmods.packageparser.PackageParser;
import com.ptsmods.packageparser.discord.misc.Message;
import com.ptsmods.packageparser.discord.server.Server;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class SearchPanel extends JPanel {
    SearchPanel() {
        setLayout(new GridBagLayout());
        List<Message> results = new ArrayList<>(PackageParser.getMessages());
        MessagesPane resultsPane = new MessagesPane(true, i -> i < results.size() ? results.get(i) : null);
        JScrollPane resultsScroll = new JScrollPane(resultsPane);
        JLabel resultsCountLabel = new JLabel("");
        JTextField searchField = new JTextField();
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                update();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                update();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                update();
            }

            private void update() {
                results.clear();
                results.addAll(getResults(searchField.getText()));
                insertResults(results, resultsPane);
                updateResultsCount(resultsCountLabel, results.size());
            }
        });
        add(searchField, new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));
        resultsCountLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));
        updateResultsCount(resultsCountLabel, results.size());
        add(resultsCountLabel, new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(-10, 4, 0, 0), 0, 0));
        JComboBox<String> sortDDM = new JComboBox<>(new String[] {"Timestamp - ascending", "Timestamp - descending", "Server (id)", "Server (name)"});
        sortDDM.addActionListener(e -> {
            int i = sortDDM.getSelectedIndex();
            switch (i) {
                case 0:
                    results.sort(Comparator.comparingLong(msg -> msg.getTimestamp().getTime()));
                    break;
                case 1:
                    results.sort(Comparator.<Message>comparingLong(msg -> msg.getTimestamp().getTime()).reversed());
                    break;
                case 2:
                    results.sort(Comparator.comparingLong(msg -> msg.getChannel().getServer() == null ? 0 : msg.getChannel().getServer().getId()));
                    break;
                case 3:
                    results.sort(Comparator.comparing(msg -> Optional.ofNullable(msg.getChannel().getServer()).map(Server::getName).orElse("")));
                    break;
            }
            insertResults(results, resultsPane);
        });
        sortDDM.setToolTipText("Sort by");
        add(sortDDM, new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 4, 0, 0), 0, 0));
        insertResults(results, resultsPane);
        add(resultsScroll, new GridBagConstraints(0, 1, 3, 1, 1, 1, GridBagConstraints.PAGE_END, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
    }

    private void updateResultsCount(JLabel label, int count) {
        label.setText("Results: " + count);
    }

    private List<Message> getResults(String input) {
        String in = input.toLowerCase(Locale.ROOT);
        return PackageParser.getMessages().stream().filter(msg -> msg.getContentLower().contains(in)).collect(Collectors.toList());
    }

    private void insertResults(List<Message> results, JTextPane resultsPane) {
        resultsPane.setText(MainGUI.msgsToHtml(results.subList(0, Math.min(results.size(), 2000))));
    }
}
