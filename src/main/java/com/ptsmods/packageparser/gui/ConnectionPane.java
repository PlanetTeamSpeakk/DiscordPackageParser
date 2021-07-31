package com.ptsmods.packageparser.gui;

import com.ptsmods.packageparser.discord.usersettings.Connection;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Function;

public class ConnectionPane extends Component {
    private final Connection connection;
    private final ConnectionType type;
    private final String value;

    public ConnectionPane(Connection connection) {
        this.connection = connection;
        type = ConnectionType.fromCodeName(connection.getType());
        value = connection.getName();
        setPreferredSize(new Dimension(100, 100));
        String url = type.getUrl(connection);
        if (url != null) {
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    try {
                        Desktop.getDesktop().browse(new URL(url).toURI());
                    } catch (IOException | URISyntaxException ex) {
                        ex.printStackTrace();
                    }
                }
            });
        }
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        Dimension size = getSize();
        Color c = type.getColor();
        Color cDark = new Color(c.getRed() * 0.65f / 255, c.getGreen() * 0.65f / 255, c.getBlue() * 0.65f / 255);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        g2d.setPaint(new RadialGradientPaint(size.width / 2f, size.height / 2f, (float) Math.sqrt((size.width / 2f) * (size.width / 2f) + (size.height / 2f) * (size.height / 2f)), new float[] {0f, 0.4f, 1f}, new Color[] {c, c, cDark}));
        g2d.fillRoundRect(0, 0, size.width, size.height, 50, 50);
        g2d.setPaint(null);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
        g2d.drawImage(type.getImage(), size.width / 2 - type.getImage().getWidth() / 2, size.height / 2 - type.getImage().getHeight() / 2, null);

        g2d.setColor(Color.WHITE);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.85f));
        g2d.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        g2d.drawString(type == ConnectionType.UNKNOWN ? connection.getType() : type.getDisplayName(), size.width / 2f - g2d.getFontMetrics().stringWidth(type == ConnectionType.UNKNOWN ? connection.getType() : type.getDisplayName()) / 2f, size.height / 2f - 8);

        g2d.setPaintMode();
        g2d.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        g2d.drawString(value, size.width / 2f - g2d.getFontMetrics().stringWidth(value) / 2f, size.height / 2f + 10);
    }

    public enum ConnectionType {
        // Colours taken from ClearVision
        // https://github.com/ClearVision/ClearVision-v6/blob/master/src/settings/connections.scss
        TWITCH(new Color(0x593695), "Twitch", "twitch", con -> "https://www.twitch.tv/" + con.getName()),
        YOUTUBE(new Color(0xFC171C), "YouTube", "youtube", con -> "https://www.youtube.com/channel/" + con.getId()),
        BATTLE_NET(new Color(0x0566B0), "Battle.net", "battlenet", con -> null),
        STEAM(new Color(0x212121), "Steam", "steam", con -> "https://steamcommunity.com/id/" + con.getName()),
        REDDIT(new Color(0xFF5900), "Reddit", "reddit", con -> "https://reddit.com/u/" + con.getName()),
        FACEBOOK(new Color(0x355089), "Facebook", "facebook", con -> "https://www.facebook.com/" + con.getName().toLowerCase(Locale.ROOT).replace(' ', '.')),
        TWITTER(new Color(0x33AEFA), "Twitter", "twitter", con -> "https://twitter.com/" + con.getName()),
        SPOTIFY(new Color(0x1DB954), "Spotify", "spotify", con -> "https://open.spotify.com/user/" + con.getName()),
        XBOX_LIVE(new Color(0x107C10), "Xbox Live", "xbox", con -> "https://xboxgamertag.com/search/" + con.getName()),
        GITHUB(new Color(0x333333), "GitHub", "github", con -> "https://github.com/" + con.getName()),
        SKYPE(new Color(0x02B4EB), "Skype", "skype", con -> null),
        UNKNOWN(new Color(0xF500F7), "Unknown", null, con -> null);

        private final Color color;
        private final String displayName, codeName;
        private final Function<Connection, String> urlFunction;
        private final BufferedImage image;

        ConnectionType(Color color, String displayName, String codeName, Function<Connection, String> urlFunction) {
            this.color = color;
            this.displayName = displayName;
            this.codeName = codeName;
            this.urlFunction = urlFunction;
            BufferedImage image = null;
            try {
                image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/connectionIcons/" + name() + ".png")));
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.image = image;
        }

        public Color getColor() {
            return color;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getCodeName() {
            return codeName;
        }

        public String getUrl(Connection connection) {
            return urlFunction.apply(connection);
        }

        public BufferedImage getImage() {
            return image;
        }

        public static ConnectionType fromCodeName(String codeName) {
            return Arrays.stream(values()).filter(type -> Objects.equals(type.getCodeName(), codeName)).findFirst().orElse(UNKNOWN);
        }
    }
}
