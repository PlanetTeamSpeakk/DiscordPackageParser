package com.ptsmods.packageparser.gui;

import com.ptsmods.packageparser.discord.misc.Message;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class MainGUI extends JFrame {
    private static final Parser textParser = Parser.builder().build();
    private static final HtmlRenderer renderer = HtmlRenderer.builder().build();
    private final JPanel mainPanel = new JPanel();
    private final JTabbedPane tabs = new JTabbedPane();
    private final JProgressBar splashProgress = new JProgressBar();
    private final JLabel splashLabel = new JLabel("Parsing package.zip...", SwingConstants.CENTER);
    private MessagesPanel messagesPanel;

    public MainGUI() {
        setTitle("Discord Package Parser - by PlanetTeamSpeak");
        mainPanel.setLayout(new GridLayout(0, 1));
        mainPanel.add(createSplashPanel());
        add(mainPanel);
        setSize(720, 480);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        try {
            setIconImage(ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/icon.png"))));
        } catch (IOException e) {
            System.err.println("Could not read icon image.");
            e.printStackTrace();
        }
        setVisible(true);
//        setResizable(false);
        SwingUtilities.updateComponentTreeUI(this);
    }

    public void updateProgress(int progress) {
        splashProgress.setValue(progress);
    }

    public void finish(long start) {
        tabs.addTab("User", new UserPanel());
        tabs.addTab("Messages", messagesPanel = new MessagesPanel());
        tabs.addTab("Search", new SearchPanel());
        mainPanel.removeAll();
        mainPanel.add(tabs);
        SwingUtilities.updateComponentTreeUI(this);
        System.out.println("Finished loading in " + (System.currentTimeMillis() - start) + " milliseconds.");
    }

    public void updateServerTree() {
        if (messagesPanel != null) messagesPanel.updateServerTree();
    }

    public void setFileName(String s) {
        setSplashLabelText("Parsing " + s + "...");
    }

    public void setSplashLabelText(String s) {
        splashLabel.setText(s);
    }

    public void setSplashProgressVisibility(boolean visible) {
        splashProgress.setVisible(visible);
    }

    static String msgsToHtml(List<Message> msgs) {
        return "<html>" + msgs.stream().map(MainGUI::msgToHtml).collect(Collectors.joining("\n")) + "</html>";
    }

    static String msgToHtml(Message msg) {
        return renderer.render(textParser.parse(msg.toString())).replaceAll("((https?)://)[-a-zA-Z0-9@:%._+~#=]{1,256}\\.[a-zA-Z]{2,6}\\b([-a-zA-Z0-9()@:%_+.~#?&/=]*)", "<a href=\"$0\">$0</a>");
    }

    public static HyperlinkListener getDefaultHyperlinkListener() {
        return e -> {
            if (e.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED) && !GraphicsEnvironment.isHeadless() && Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE))
                try {
                    Desktop.getDesktop().browse(e.getURL().toURI());
                } catch (IOException | URISyntaxException ex) {
                    ex.printStackTrace();
                }
        };
    }

    private JPanel createSplashPanel() {
        JPanel splash = new JPanel();
        splash.setLayout(new GridBagLayout());
        splash.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        JPanel child = new JPanel(new GridLayout(2, 1));
        splashLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
        child.add(splashLabel);
        JPanel grandchild = new JPanel(new GridBagLayout());
        grandchild.add(splashProgress);
        splashProgress.setPreferredSize(new Dimension(150, 15));
        child.add(grandchild);
        splash.add(child);
        return splash;
    }
}
