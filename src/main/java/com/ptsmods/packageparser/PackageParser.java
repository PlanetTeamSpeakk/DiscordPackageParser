package com.ptsmods.packageparser;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
//import com.opencsv.CSVReader;
//import com.opencsv.exceptions.CsvException;
import com.opencsv.CSVReader;
import com.ptsmods.packageparser.discord.misc.*;
import com.ptsmods.packageparser.discord.server.Channel;
import com.ptsmods.packageparser.discord.server.Server;
import com.ptsmods.packageparser.discord.user.Relationship;
import com.ptsmods.packageparser.discord.user.User;
import com.ptsmods.packageparser.discord.usersettings.*;
import com.ptsmods.packageparser.gui.MainGUI;
//import com.ptsmods.packageparser.Pair;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.text.html.HTMLDocument;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class PackageParser {
    public static final BufferedImage channelIcon, channelIconSelected, serverIcon, serverIconSelected;
    private static final Executor executor = Executors.newCachedThreadPool(r -> {
        Thread t = new Thread(r);
        t.setUncaughtExceptionHandler((thread, throwable) -> showError(throwable));
        return t;
    });
    private static File pckg = new File("package.zip");
    private static ZipFile zip;
    private static final Map<DataType, List<ZipEntry>> entries = new HashMap<>();
    private static final List<Server> servers = new ArrayList<>();
    private static final Map<Long, Server> serversMap = new HashMap<>();
    private static final Map<Long, Channel> channels = new HashMap<>();
    private static final List<Channel> privateChannels = new ArrayList<>();
    private static final List<Message> messages = new ArrayList<>();
    private static User user;
    private static final MainGUI gui = new MainGUI();
    private static final AtomicInteger progress = new AtomicInteger();
    private static int dataFileCount = 0;
    private static BufferedImage avatar = null;

    static {
        ClassLoader cl = PackageParser.class.getClassLoader();
        BufferedImage channelIcon0 = null, channelIconSelected0 = null, serverIcon0 = null, serverIconSelected0 = null;
        try {
            channelIcon0 = ImageIO.read(Objects.requireNonNull(cl.getResourceAsStream("channel_icon.png")));
            channelIconSelected0 = ImageIO.read(Objects.requireNonNull(cl.getResourceAsStream("channel_icon_selected.png")));
            serverIcon0 = ImageIO.read(Objects.requireNonNull(cl.getResourceAsStream("server_icon.png")));
            serverIconSelected0 = ImageIO.read(Objects.requireNonNull(cl.getResourceAsStream("server_icon_selected.png")));
        } catch (IOException e) {
            System.err.println("Couldn't read images.");
        }
        channelIcon = getScaledInstance(Objects.requireNonNull(channelIcon0), 16, 16);
        channelIconSelected = getScaledInstance(Objects.requireNonNull(channelIconSelected0), 16, 16);
        serverIcon = getScaledInstance(Objects.requireNonNull(serverIcon0), 16, 16);
        serverIconSelected = getScaledInstance(Objects.requireNonNull(serverIconSelected0), 16, 16);
    }

    public static void main(String[] args) {
        try {
            run();
        } catch (Throwable t) {
            showError(t);
        }
    }

    private static void showError(Throwable error) {
        System.err.println("Got error: " + error);
        gui.dispose();
        JFrame frame = new JFrame();
        frame.setSize(480, 288);
        frame.setTitle("Uh oh! An error occurred!");
        frame.setLocationRelativeTo(null);
        try {
            frame.setIconImage(ImageIO.read(Objects.requireNonNull(PackageParser.class.getResourceAsStream("/icon.png"))));
        } catch (IOException e) {
            System.err.println("Could not read icon image.");
            e.printStackTrace();
        }

        JPanel content = new JPanel();
        content.setLayout(new GridBagLayout());

        JLabel label = new JLabel("Uh oh! An error occurred!", SwingConstants.CENTER);
        label.setFont(label.getFont().deriveFont(Font.BOLD, 24));
        content.add(label, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.SOUTH, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

        JPanel line2 = new JPanel();
        line2.setLayout(new GridBagLayout());
        JPanel line2Content = new JPanel();
        line2Content.setLayout(new GridBagLayout());
        line2Content.add(new JLabel("But not to worry, you can easily report it with a couple of clicks and", SwingConstants.CENTER), new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.SOUTH, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        line2Content.add(new JLabel("have it be solved in a matter of days!", SwingConstants.CENTER), new GridBagConstraints(0, 1, 1, 1, 1, 1, GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        line2.add(line2Content);
        content.add(line2, new GridBagConstraints(0, 1, 1, 1, 0, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(20, 20, 20, 20), 0, 0));

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (PrintWriter writer = new PrintWriter(out)) {
            error.printStackTrace(writer);
            writer.flush();
        }
        String stacktrace = out.toString();

        JButton button = new JButton("Click here to copy the stacktrace and report it!");
        button.addActionListener(event -> {
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection("I have encountered an error while using Discord Package Parser.\nHere's the stacktrace:\n\n" + stacktrace), null);
            try {
                Desktop.getDesktop().browse(new URL("https://github.com/PlanetTeamSpeakk/DiscordPackageParser/issues/new").toURI());
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            }
        });
        content.add(button, new GridBagConstraints(0, 2, 1, 1, 0, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 100, 0, 100), 0, 0));

        frame.add(content);
        frame.setVisible(true);
    }

    @SuppressWarnings("unchecked")
    private static void run() {
        try {
            JFrame.setDefaultLookAndFeelDecorated(true);
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            SwingUtilities.updateComponentTreeUI(gui);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            System.out.println("Could not find system and/or set look and feel class.");
            e.printStackTrace();
        }
        gui.setVisible(true);
        if (Integer.parseInt(System.getProperty("java.version").substring(0, System.getProperty("java.version").indexOf('.'))) < 11) {
            JEditorPane pane = new JEditorPane("text/html", "<html>It is recommended to use at least Java 11 to use this program as it may otherwise not be able to parse all messages and look downright terrible.<br>Please upgrade <a href=\"https://adoptopenjdk.net\">here</a>.</html>");
            pane.addHyperlinkListener(MainGUI.getDefaultHyperlinkListener());
            pane.setEditable(false);
            pane.setBackground(new JLabel().getBackground());
            HTMLDocument doc = (HTMLDocument) pane.getDocument();
            doc.getStyleSheet().addRule("html {text-align: center; font-family: sans-serif}");
            JOptionPane.showMessageDialog(null, pane, "Please upgrade your Java version", JOptionPane.PLAIN_MESSAGE);
        }
        if (!pckg.exists()) {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Select package zip to parse...");
            chooser.setCurrentDirectory(new File("./"));
            if (chooser.showOpenDialog(gui) != JFileChooser.APPROVE_OPTION) exit();
            else {
                pckg = chooser.getSelectedFile();
                gui.setFileName(pckg.getName());
            }
        }
        try {
            zip = new ZipFile(pckg);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(gui, "The selected package zip could not be read. Are you sure it is a zip file?", "Unreadable file", JOptionPane.ERROR_MESSAGE);
            exit();
        }
        long start = System.currentTimeMillis();
        System.out.println("Indexing " + pckg.getName() + "...");
        for (DataType type : DataType.values())
            entries.put(type, new ArrayList<>());
        Enumeration<? extends ZipEntry> entryEnumeration = zip.entries();
        while (entryEnumeration.hasMoreElements()) {
            ZipEntry entry = entryEnumeration.nextElement();
            DataType type = DataType.fromEntry(entry);
            if (type == null && !entry.isDirectory() && !"README.txt".equalsIgnoreCase(entry.getName())) System.out.println("Not sure what to do with entry " + entry.getName() + ".");
            else if (type != null) entries.get(type).add(entry);
        }
        int files = entries.values().stream().mapToInt(List::size).sum();
        dataFileCount = (int) entries.entrySet().stream()
                .filter(entry -> entry.getKey().shouldParse)
                .flatMap(entry -> entry.getValue().stream())
                .filter(entry -> (entry.getName().toLowerCase(Locale.ROOT).endsWith(".json") || entry.getName().toLowerCase(Locale.ROOT).endsWith(".csv")) && !entry.getName().endsWith("audit-log.json"))
                .count();
        System.out.println("Found " + files + " files in total of which " + dataFileCount + " are data files.");

        Gson gson = new Gson();
        CompletableFuture.allOf(
            CompletableFuture.runAsync(() -> {
                System.out.println("Parsing servers...");
                Type type = new TypeToken<Map<String, Object>>() {}.getType();
                Queue<Server> serverQueue = new ConcurrentLinkedQueue<>();
                entries.get(DataType.SERVERS).parallelStream()
                        .filter(entry -> entry.getName().endsWith("guild.json"))
                        .map(entry -> gson.<Map<String, Object>>fromJson(new BufferedReader(new InputStreamReader(Objects.requireNonNull(getStream(entry)))), type))
                        .map(map -> new Server(Long.parseLong((String) map.get("id")), (String) map.get("name")))
                        .forEach(server -> {
                            serverQueue.add(server);
                            updateProgress();
                        });
                System.out.println("Found " + serverQueue.size() + " servers");
                System.out.println("Parsing messages and channels...");
                Map<Long, Server> concServerMap = new ConcurrentHashMap<>(serverQueue.stream().collect(Collectors.toMap(Server::getId, server -> server)));
                Map<Long, Channel> concChannels = new ConcurrentHashMap<>();
                AtomicInteger parseFail = new AtomicInteger();
                ConcurrentLinkedQueue<Message> messageQueue = new ConcurrentLinkedQueue<>();
                entries.get(DataType.MESSAGES).parallelStream()
                        .filter(entry -> entry.getName().endsWith("channel.json"))
                        .forEach(entry -> {
                            try {
                                Map<String, Object> channelMap = gson.fromJson(new BufferedReader(new InputStreamReader(Objects.requireNonNull(getStream(entry)))), type);
                                long id = Long.parseLong((String) channelMap.get("id"));
                                Long serverId = channelMap.get("guild") == null ? null : Long.parseLong(((Map<String, String>) channelMap.get("guild")).get("id"));
                                Server server = serverId == null ? null : concServerMap.containsKey(serverId) ? concServerMap.get(serverId) : new Server(serverId, ((Map<String, String>) channelMap.get("guild")).get("name"));
                                Channel channel = concChannels.containsKey(id) ? concChannels.get(id) : new Channel(id, Channel.Type.values()[((Double) channelMap.get("type")).intValue()], (String) channelMap.get("name"), server, channelMap.containsKey("recipients") && ((List<String>) channelMap.get("recipients")).size() == 2 ? Long.parseLong(((List<String>) channelMap.get("recipients")).get(1)) : null, () -> SwingUtilities.invokeLater(gui::updateServerTree));
                                if (!concChannels.containsKey(channel.getId())) {
                                    concChannels.put(channel.getId(), channel);
                                    if (server != null) server.addChannel(channel);
                                }
                                updateProgress();
                                if (server != null && !serverQueue.contains(server)) serverQueue.add(server);
                                try {
                                    List<Message> messages = new CSVReader(new BufferedReader(new InputStreamReader(Objects.requireNonNull(getStream(zip.getEntry(entry.getName().substring(0, entry.getName().length() - 12) + "messages.csv"))), StandardCharsets.UTF_8)))
                                            .readAll().stream().skip(1).map(a -> new Message(Long.parseLong(a[0]), a[2], channel)).collect(Collectors.toList());
                                    channel.addMessages(messages);
                                    messageQueue.addAll(messages);
                                } catch (IOException | NumberFormatException e) {
                                    parseFail.incrementAndGet();
                                }
                                updateProgress();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                messages.addAll(messageQueue);
                servers.addAll(serverQueue);
                serversMap.putAll(servers.stream().collect(Collectors.toMap(Server::getId, server -> server)));
                channels.putAll(concChannels);
                messages.sort(Comparator.comparingLong(msg -> msg.getTimestamp().getTime()));
                System.out.println("Found " + messages.size() + " messages and " + channels.size() + " channels. " + parseFail.get() + " message" + (parseFail.get() == 1 ? "" : "s") + " couldn't be parsed.");
            }, executor), CompletableFuture.runAsync(() -> {
                System.out.println("Parsing account...");
                InputStream userStream = entries.get(DataType.ACCOUNT).stream().filter(entry -> "account/user.json".equalsIgnoreCase(entry.getName())).findFirst().map(PackageParser::getStream).orElse(null);
                if (userStream == null) System.err.println("Could not get user json file.");
                else {
                    Map<String, Object> userData = gson.fromJson(new InputStreamReader(userStream), new TypeToken<Map<String, Object>>() {}.getType());
                    Map<String, Object> settingsData = (Map<String, Object>) userData.get("settings");
                    user = new User(
                            Long.parseLong((String) userData.get("id")),
                            (String) userData.get("username"),
                            ((Double) userData.get("discriminator")).intValue(),
                            (String) userData.get("email"),
                            (Boolean) userData.get("verified"),
                            (String) userData.get("avatar_hash"),
                            (Boolean) userData.get("has_mobile"),
                            (Boolean) userData.get("needs_email_verification"),
                            parse((String) userData.get("premium_until")),
                            ((Double) userData.get("flags")).intValue(),
                            (String) userData.get("phone"),
                            parse((String) userData.get("temp_banned_until")),
                            (String) userData.get("ip"),
                            new User.Settings(
                                    (String) settingsData.get("locale"),
                                    (Boolean) settingsData.get("show_current_game"),
                                    ((List<String>) settingsData.get("restricted_guilds")).stream().map(Long::parseLong).collect(Collectors.toList()),
                                    (Boolean) settingsData.get("default_guilds_restricted"),
                                    (Boolean) settingsData.get("inline_attachment_media"),
                                    (Boolean) settingsData.get("inline_embed_media"),
                                    (Boolean) settingsData.get("gif_auto_play"),
                                    (Boolean) settingsData.get("render_embeds"),
                                    (Boolean) settingsData.get("render_reactions"),
                                    (Boolean) settingsData.get("animate_emoji"),
                                    (Boolean) settingsData.get("enable_tts_command"),
                                    (Boolean) settingsData.get("message_display_compact"),
                                    (Boolean) settingsData.get("convert_emoticons"),
                                    ContentFilter.values()[((Double) settingsData.get("explicit_content_filter")).intValue()],
                                    (Boolean) settingsData.get("disable_games_tab"),
                                    "light".equalsIgnoreCase((String) settingsData.get("theme")) ? Theme.LIGHT : "dark".equalsIgnoreCase((String) settingsData.get("theme")) ? Theme.DARK : Theme.DARK_LIGHT,
                                    (Boolean) settingsData.get("developer_mode"),
                                    ((List<String>) settingsData.get("guild_positions")).stream().map(Long::parseLong).collect(Collectors.toList()),
                                    (Boolean) settingsData.get("detect_platform_accounts"),
                                    Status.valueOf(((String) settingsData.get("status")).toUpperCase(Locale.ROOT)),
                                    ((Double) settingsData.get("afk_timeout")).intValue(),
                                    ((Double) settingsData.get("timezone_offset")).intValue(),
                                    (Boolean) settingsData.get("stream_notifications_enabled"),
                                    (Boolean) settingsData.get("allow_accessibility_detection"),
                                    (Boolean) settingsData.get("contact_sync_enabled"),
                                    (Boolean) settingsData.get("native_phone_integration_enabled"),
                                    ((Double) settingsData.get("animate_stickers")).intValue(),
                                    ((Double) settingsData.get("friend_discovery_flags")).intValue(),
                                    ((List<Map<String, Object>>) settingsData.get("guild_folders")).stream().map(folder -> new ServerFolder(((List<String>) folder.get("guild_ids")), ((Double) folder.get("id")).intValue(), (String) folder.get("name"), new Color(((Double) folder.get("color")).intValue()))).collect(Collectors.toList()),
                                    Optional.ofNullable((Map<String, Object>) settingsData.get("custom_status")).map(map -> new CustomStatus((String) map.get("text"), map.get("expires_at") == null ? null : parse((String) map.get("expires_at")), map.get("emoji_id") == null ? null : Long.parseLong((String) map.get("emoji_id")), (String) map.get("emoji_name"))).orElse(null)
                            ),
                            ((List<Map<String, Object>>) userData.get("connections")).stream().map(map -> new Connection((String) map.get("type"), (String) map.get("id"), (String) map.get("name"), (Boolean) map.get("revoked"), ((Double) map.get("visibility")).intValue(), (Boolean) map.get("friend_sync"), (Boolean) map.get("show_activity"), (Boolean) map.get("verified"))).collect(Collectors.toList()),
                            ((List<Map<String, Object>>) userData.get("mfa_sessions")).stream().map(map -> new MfaSession(Long.parseLong((String) map.get("user_id")), new Date(((Double) map.get("started")).intValue() * 1000L), (String) map.get("ip"), new Date(Integer.parseInt((String) map.get("e")) * 1000L), (String) map.get("id"))).collect(Collectors.toList()),
                            ((List<Map<String, Object>>) userData.get("relationships")).stream().map(map -> new Relationship(Long.parseLong((String) map.get("id")), Relationship.Type.values()[((Double) map.get("type")).intValue() - 1], (String) map.get("nickname"), (String) ((Map<String, Object>) map.get("user")).get("username"), (String) ((Map<String, Object>) map.get("user")).get("avatar"), Integer.parseInt((String) ((Map<String, Object>) map.get("user")).get("discriminator")), ((Double) ((Map<String, Object>) map.get("user")).get("public_flags")).intValue())).collect(Collectors.toList()),
                            ((List<Map<String, Object>>) userData.get("payments")).stream().map(map -> new Payment(Long.parseLong((String) map.get("id")), parse((String) map.get("created_at")), (String) map.get("currency"), ((Double) map.get("tax")).intValue(), (Boolean) map.get("tax_inclusive"), ((Double) map.get("amount")).intValue(), ((Double) map.get("amount_refunded")).intValue(), ((Double) map.get("status")).intValue(), (String) map.get("description"), ((Double) map.get("flags")).intValue(),
                                    Optional.ofNullable((Map<String, Object>) map.get("subscription")).map(map0 -> new Subscription(Long.parseLong((String) map0.get("id")), ((Double) map0.get("type")).intValue(), parse((String) map0.get("current_period_start")), parse((String) map0.get("current_period_end")), map0.get("payment_gateway") == null ? null : ((Double) map0.get("payment_gateway")).intValue(), (String) map0.get("payment_gateway_plan_id"), (String) map0.get("currency"), Long.parseLong((String) map0.get("plan_id")), ((List<Map<String, Object>>) map0.get("items")).stream().map(map1 -> new Subscription.Item(Long.parseLong((String) map1.get("id")), Long.parseLong((String) map1.get("plan_id")), ((Double) map1.get("quantity")).intValue())).collect(Collectors.toList()))).orElse(null),
                                    Optional.ofNullable((Map<String, Object>) map.get("payment_source")).map(mapToPaymentSource()).orElse(null),
                                    Long.parseLong((String) map.get("sku_id")), ((Double) map.get("sku_price")).intValue(), Long.parseLong((String) map.get("sku_subscription_plan_id")))).collect(Collectors.toList()),
                            ((List<Map<String, Object>>) userData.get("payment_sources")).stream().map(mapToPaymentSource()).collect(Collectors.toList()),
                            ((List<Map<String, Object>>) userData.get("guild_settings")).stream().map(map -> new ServerSettings(map.get("guild_id") == null ? null : Long.parseLong((String) map.get("guild_id")), (Boolean) map.get("suppress_everyone"), (Boolean) map.get("suppress_roles"), ((Double) map.get("message_notifications")).intValue(), (Boolean) map.get("mobile_push"), (Boolean) map.get("muted"), map.get("mute_config") != null && ((Map<?, ?>) map.get("mute_config")).containsKey("end_time") ? parse((String) ((Map<String, Object>) map.get("mute_config")).get("end_time")) : null, (Boolean) map.get("hide_muted_channels"),
                                    ((List<Map<String, Object>>) map.get("channel_overrides")).stream().map(map0 -> new ServerSettings.ChannelOverride(Long.parseLong((String) map0.get("channel_id")), ((Double) map0.get("message_notifications")).intValue(), (Boolean) map0.get("muted"), map0.get("mute_config") != null && ((Map<?, ?>) map0.get("mute_config")).containsKey("end_time") ? parse((String) ((Map<String, Object>) map0.get("mute_config")).get("end_time")) : null, (Boolean) map0.get("collapsed"))).collect(Collectors.toList()), ((Double) map.get("version")).intValue())).collect(Collectors.toList()),
                            ((List<Map<String, Map<String, Object>>>) userData.get("library_applications")).stream().map(map -> new LibraryApplication(Long.parseLong((String) map.get("application").get("id")), (String) map.get("application").get("name"), (String) map.get("application").get("icon"), (String) map.get("application").get("description"), (String) map.get("application").get("summary"), (String) map.get("application").get("splash"), (String) map.get("application").get("slug"), ((List<Map<String, Object>>) map.get("application").get("publishers")).stream().map(map0 -> Pair.of((String) map0.get("id"), (String) map0.get("name"))).collect(Collectors.toList()), map.get("application").containsKey("developers") ? ((List<Map<String, Object>>) map.get("application").get("developers")).stream().map(map0 -> Pair.of((String) map0.get("id"), (String) map0.get("name"))).collect(Collectors.toList()) : Collections.emptyList())).collect(Collectors.toList()),
                            ((Map<String, String>) userData.get("notes")).entrySet().stream().collect(Collectors.<Map.Entry<String, String>, Long, String>toMap(entry -> Long.parseLong(entry.getKey()), entry -> entry.getValue() == null ? "" : entry.getValue()))
                    );
                    if (entries.get(DataType.ACCOUNT).stream().noneMatch(entry -> entry.getName().startsWith("account/avatar."))) {
                        try {
                            avatar = ImageIO.read(new URL(user.getAvatarURL()));
                        } catch (IOException e) {
                            System.err.println("Could not read default avatar image.");
                            e.printStackTrace();
                        }
                    }
                }
                updateProgress();
        }, executor), CompletableFuture.runAsync(() -> {
            InputStream avatarStream = entries.get(DataType.ACCOUNT).stream().filter(entry -> entry.getName().startsWith("account/avatar.")).findFirst().map(PackageParser::getStream).orElse(null);
            if (avatarStream != null)
                try {
                    avatar = ImageIO.read(avatarStream);
                } catch (IOException e) {
                    System.err.println("Could not read avatar image from package.");
                    e.printStackTrace();
                }
            updateProgress();
        }, executor)).thenAccept(v -> {
            if (user == null) {
                JOptionPane.showMessageDialog(gui, "The selected package zip does not seem to be a data package from Discord.", "Unknown file selected", JOptionPane.ERROR_MESSAGE);
                exit();
                return;
            }
            SwingUtilities.invokeLater(() -> {
                gui.setSplashLabelText("Loading...");
                gui.setSplashProgressVisibility(false);
            });
            System.out.println("Parsed " + pckg.getName() + " in " + (System.currentTimeMillis() - start) + " milliseconds.");
            long start0 = System.currentTimeMillis();
            List<Server> sortedServers = user.getSettings().getGuildPositions().stream().map(serversMap::get).filter(Objects::nonNull).collect(Collectors.toList());
            for (ServerFolder folder : user.getSettings().getGuildFolders()) folder.fill(servers);
            servers.clear();
            servers.addAll(sortedServers);
            privateChannels.addAll(channels.values().stream().filter(channel -> channel.getType() != Channel.Type.GUILD).collect(Collectors.toList()));
            SwingUtilities.invokeLater(() -> gui.finish(start0));
        });
    }

    public static User getUser() {
        return user;
    }

    public static BufferedImage getAvatar() {
        return avatar;
    }

    public static List<Server> getServers() {
        return Collections.unmodifiableList(servers);
    }

    public static List<Channel> getPrivateChannels() {
        return Collections.unmodifiableList(privateChannels);
    }

    public static List<Message> getMessages() {
        return Collections.unmodifiableList(messages);
    }

    // From https://stackoverflow.com/a/7951324
    public static BufferedImage getScaledInstance(BufferedImage img, int targetWidth, int targetHeight) {
        int type = img.getTransparency() == Transparency.OPAQUE ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
        int w = img.getWidth();
        int h = img.getHeight();
        do {
            if (w > targetWidth) {
                w /= 2;
                if (w < targetWidth) w = targetWidth;
            }
            if (h > targetHeight) {
                h /= 2;
                if (h < targetHeight) h = targetHeight;
            }
            BufferedImage tmp = new BufferedImage(w, h, type);
            Graphics2D g2 = tmp.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2.drawImage(img, 0, 0, w, h, null);
            g2.dispose();
            img = tmp;
        } while (w != targetWidth || h != targetHeight);
        return img;
    }

    public static File resolvePath(String path) {
        return pckg.getAbsoluteFile().getParentFile().toPath().resolve(path).toFile();
    }

    private static Date parse(String s) {
        try {
            return s == null ? null : new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'.'SSSXXX").parse(s);
        } catch (ParseException e) {
            try {
                return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").parse(s);
            } catch (ParseException e0) {
                System.err.println("Error parsing " + s);
                e0.printStackTrace();
                return new Date();
            }
        }
    }

    private static InputStream getStream(ZipEntry entry) {
        try {
            return zip.getInputStream(entry);
        } catch (IOException e) {
            System.err.println("Could not get stream for entry " + entry.getName());
            e.printStackTrace();
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private static Function<Map<String, Object>, Payment.PaymentSource> mapToPaymentSource() {
        return map0 -> new Payment.PaymentSource(
                Long.parseLong((String) map0.get("id")),
                ((Double) map0.get("type")).intValue(),
                (Boolean) map0.get("invalid"),
                (String) map0.get("email"),
                new Address(
                        ((Map<String, String>) map0.get("billing_address")).get("name"),
                        ((Map<String, String>) map0.get("billing_address")).get("line_1"),
                        ((Map<String, String>) map0.get("billing_address")).get("line_2"),
                        ((Map<String, String>) map0.get("billing_address")).get("city"),
                        ((Map<String, String>) map0.get("billing_address")).get("state"),
                        ((Map<String, String>) map0.get("billing_address")).get("country"),
                        ((Map<String, String>) map0.get("billing_address")).get("postal_code")),
                (String) map0.get("country"));
    }

    private static void updateProgress() {
        SwingUtilities.invokeLater(() -> gui.updateProgress((int) ((double) progress.incrementAndGet() / dataFileCount * 100)));
    }

    private static void exit() {
        gui.dispose();
        System.exit(0);
    }

    public enum DataType {
        ACCOUNT(true),
        ACCOUNT_APPLICATIONS(false),
        ACTIVITY_ANALYTICS(false),
        ACTIVITY_MODELING(false), // All of these contain rather useless information.
        ACTIVITY_REPORTING(false),
        ACTIVITY_TNS(false),
        MESSAGES(true),
        SERVERS(true);

        public final boolean shouldParse;

        DataType(boolean shouldParse) {
            this.shouldParse = shouldParse;
        }

        public static DataType fromEntry(ZipEntry entry) {
            if (entry.isDirectory()) return null;
            String name = entry.getName().toUpperCase(Locale.ROOT);
            for (DataType type : values())
                if (name.startsWith(type.name().replace('_', '/'))) return type;
            return null;
        }
    }
}
