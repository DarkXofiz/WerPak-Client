/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.Formatting
 *  net.minecraft.item.Items
 *  net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket
 *  net.minecraft.network.packet.s2c.play.GameMessageS2CPacket
 *  org.apache.commons.lang3.StringEscapeUtils
 */
package thunder.hack.features.modules.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.util.Formatting;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import org.apache.commons.lang3.StringEscapeUtils;
import thunder.hack.core.Managers;
import thunder.hack.events.impl.PacketEvent;
import thunder.hack.features.modules.Module;
import thunder.hack.setting.Setting;

public class ChatTranslator
extends Module {
    private final Setting<Lang> urLang = new Setting<Lang>("YourLanguage", Lang.ru);
    private final Setting<Lang> outMessages = new Setting<Lang>("OutMessages", Lang.uk);
    private List<String> exceptions = Arrays.asList("was killed by", "was destroyed by", "has joined a game", "has left the game");
    private String skip;

    public ChatTranslator() {
        super("ChatTranslator", Module.Category.CLIENT);
    }

    @EventHandler
    public void onPacketReceive(PacketEvent.Receive e) {
        if (ChatTranslator.fullNullCheck()) {
            return;
        }
        Object t = e.getPacket();
        if (t instanceof GameMessageS2CPacket) {
            GameMessageS2CPacket mPacket = (GameMessageS2CPacket)t;
            String message = mPacket.content().getString();
            for (String s : this.exceptions) {
                if (!message.contains(s)) continue;
                return;
            }
            Managers.ASYNC.run(() -> {
                try {
                    this.sendMessage(String.valueOf(Formatting.WHITE) + this.translate(message, this.urLang.getValue().name()));
                }
                catch (Exception exc) {
                    exc.printStackTrace();
                }
            });
        }
    }

    @EventHandler
    public void onPacketSend(PacketEvent.Send e) {
        if (ChatTranslator.fullNullCheck()) {
            return;
        }
        Object t = e.getPacket();
        if (t instanceof ChatMessageC2SPacket) {
            ChatMessageC2SPacket pac = (ChatMessageC2SPacket)t;
            if (Objects.equals(pac.chatMessage(), this.skip)) {
                return;
            }
            if (ChatTranslator.mc.player.getMainHandStack().getItem() == Items.FILLED_MAP || ChatTranslator.mc.player.getOffHandStack().getItem() == Items.FILLED_MAP) {
                return;
            }
            if (pac.chatMessage().startsWith("/") || pac.chatMessage().startsWith(Managers.COMMAND.getPrefix())) {
                return;
            }
            Managers.ASYNC.run(() -> {
                try {
                    String outMessage = this.translate(pac.chatMessage(), this.outMessages.getValue().name());
                    if (Objects.equals(pac.chatMessage(), outMessage)) {
                        this.skip = pac.chatMessage();
                        ChatTranslator.mc.player.networkHandler.sendChatMessage(pac.chatMessage());
                    } else {
                        this.skip = outMessage;
                        ChatTranslator.mc.player.networkHandler.sendChatMessage(outMessage);
                    }
                }
                catch (Exception exc) {
                    exc.printStackTrace();
                }
            });
            e.cancel();
        }
    }

    public String translate(String text, String to) throws UnsupportedEncodingException, MalformedURLException {
        StringBuilder response = new StringBuilder();
        URL url = new URL(String.format("https://translate.google.com/m?hl=en&sl=auto&tl=%s&ie=UTF-8&prev=_m&q=%s", to, URLEncoder.encode(text.trim(), StandardCharsets.UTF_8)));
        try (BufferedReader br2 = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8));){
            String line;
            while ((line = br2.readLine()) != null) {
                response.append(line + "\n");
            }
        }
        catch (IOException br2) {
            // empty catch block
        }
        Matcher matcher = Pattern.compile("class=\"result-container\">([^<]*)<\\/div>", 8).matcher(response);
        matcher.find();
        String match = matcher.group(1);
        if (match == null || match.isEmpty()) {
            return "translation failed";
        }
        return StringEscapeUtils.unescapeHtml4((String)match);
    }

    public static enum Lang {
        am,
        ar,
        eu,
        bn,
        bg,
        ca,
        chr,
        hr,
        cs,
        da,
        nl,
        en,
        et,
        fil,
        fi,
        fr,
        de,
        el,
        gu,
        iw,
        hi,
        hu,
        is,
        id,
        it,
        ja,
        kn,
        ko,
        lv,
        lt,
        ms,
        ml,
        mr,
        no,
        pl,
        ro,
        ru,
        sr,
        sk,
        sl,
        es,
        sw,
        sv,
        ta,
        te,
        th,
        tr,
        ur,
        uk,
        vi,
        cy,
        cn;

    }
}

