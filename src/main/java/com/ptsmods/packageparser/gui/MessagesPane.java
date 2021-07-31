package com.ptsmods.packageparser.gui;

import com.ptsmods.packageparser.discord.misc.Message;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.event.MouseEvent;
import java.util.function.Function;

public class MessagesPane extends JTextPane {
    private final boolean showTooltips;
    private final Function<Integer, Message> messageGetter;

    MessagesPane(boolean showTooltips, @Nullable Function<Integer, Message> messageGetter) {
        this.showTooltips = showTooltips;
        this.messageGetter = messageGetter;
        if (showTooltips) ToolTipManager.sharedInstance().registerComponent(this);
        setContentType("text/html;charset=UTF-8");
        setEditorKit(new HTMLEditorKit());
        ((HTMLDocument) getStyledDocument()).getStyleSheet().addRule("p {font-family: sans-serif; margin: 0}");
        setEditable(false);
        addHyperlinkListener(MainGUI.getDefaultHyperlinkListener());
    }

    @Override
    public String getToolTipText(MouseEvent event) {
        if (showTooltips) {
            HTMLDocument doc = (HTMLDocument) getStyledDocument();
            String s = "";
            try {
                s = doc.getText(0, viewToModel(event.getPoint()));
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
            Message msg = messageGetter == null ? null : messageGetter.apply((int) (s.chars().filter(i -> i == '\n').count() - 1));
            return msg != null ? msg.getChannel().getServer() != null ? "<html><b>Server:</b> " + msg.getChannel().getServer().getName() + "<br><b>Channel:</b> #" + msg.getChannel().getName() + "</html>" : "<html>" + msg.getChannel().getName() + "</html>" : null;
        } else return null;
    }
}
