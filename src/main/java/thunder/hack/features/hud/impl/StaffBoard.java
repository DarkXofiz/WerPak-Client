/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  net.minecraft.util.Formatting
 *  net.minecraft.world.GameMode
 *  net.minecraft.scoreboard.Team
 *  net.minecraft.util.Identifier
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.network.PlayerListEntry
 */
package thunder.hack.features.hud.impl;

import com.mojang.authlib.GameProfile;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import net.minecraft.util.Formatting;
import net.minecraft.world.GameMode;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.Identifier;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.PlayerListEntry;
import thunder.hack.features.cmd.impl.StaffCommand;
import thunder.hack.features.hud.HudElement;
import thunder.hack.features.modules.client.HudEditor;
import thunder.hack.gui.font.FontRenderers;
import thunder.hack.utility.render.Render2DEngine;
import thunder.hack.utility.render.animation.AnimationUtility;

public class StaffBoard
extends HudElement {
    private static final Pattern validUserPattern = Pattern.compile("^\\w{3,16}$");
    private List<String> players = new ArrayList<String>();
    private List<String> notSpec = new ArrayList<String>();
    private Map<String, Identifier> skinMap = new HashMap<String, Identifier>();
    private float vAnimation;
    private float hAnimation;

    public StaffBoard() {
        super("StaffBoard", 50, 50);
    }

    public static List<String> getOnlinePlayer() {
        return StaffBoard.mc.player.networkHandler.getPlayerList().stream().map(PlayerListEntry::getProfile).map(GameProfile::getName).filter(profileName -> validUserPattern.matcher((CharSequence)profileName).matches()).collect(Collectors.toList());
    }

    public static List<String> getOnlinePlayerD() {
        ArrayList<String> S = new ArrayList<String>();
        for (PlayerListEntry player : StaffBoard.mc.player.networkHandler.getPlayerList()) {
            if (mc.isInSingleplayer() || player.getScoreboardTeam() == null) break;
            String prefix = player.getScoreboardTeam().getPrefix().getString();
            if (!StaffBoard.check(Formatting.strip((String)prefix).toLowerCase()) && !StaffCommand.staffNames.toString().toLowerCase().contains(player.getProfile().getName().toLowerCase()) && !player.getProfile().getName().toLowerCase().contains("1danil_mansoru1") && !player.getProfile().getName().toLowerCase().contains("barslan_") && !player.getProfile().getName().toLowerCase().contains("timmings") && !player.getProfile().getName().toLowerCase().contains("timings") && !player.getProfile().getName().toLowerCase().contains("ruthless") && !player.getScoreboardTeam().getPrefix().getString().contains("YT") && (!player.getScoreboardTeam().getPrefix().getString().contains("Y") || !player.getScoreboardTeam().getPrefix().getString().contains("T"))) continue;
            String name = Arrays.asList(player.getScoreboardTeam().getPlayerList().toArray()).toString().replace("[", "").replace("]", "");
            if (player.getGameMode() == GameMode.SPECTATOR) {
                S.add(player.getScoreboardTeam().getPrefix().getString() + name + ":gm3");
                continue;
            }
            S.add(player.getScoreboardTeam().getPrefix().getString() + name + ":active");
        }
        return S;
    }

    public List<String> getVanish() {
        ArrayList<String> list = new ArrayList<String>();
        for (Team s : StaffBoard.mc.world.getScoreboard().getTeams()) {
            if (s.getPrefix().getString().isEmpty() || mc.isInSingleplayer()) continue;
            String name = Arrays.asList(s.getPlayerList().toArray()).toString().replace("[", "").replace("]", "");
            if (StaffBoard.getOnlinePlayer().contains(name) || name.isEmpty() || (!StaffCommand.staffNames.toString().toLowerCase().contains(name.toLowerCase()) || !StaffBoard.check(s.getPrefix().getString().toLowerCase())) && !StaffBoard.check(s.getPrefix().getString().toLowerCase()) && !name.toLowerCase().contains("1danil_mansoru1") && !name.toLowerCase().contains("barslan_") && !name.toLowerCase().contains("timmings") && !name.toLowerCase().contains("timings") && !name.toLowerCase().contains("ruthless") && !s.getPrefix().getString().contains("YT") && (!s.getPrefix().getString().contains("Y") || !s.getPrefix().getString().contains("T"))) continue;
            list.add(s.getPrefix().getString() + name + ":vanish");
        }
        return list;
    }

    public static boolean check(String name) {
        if (mc.getCurrentServerEntry() != null && StaffBoard.mc.getCurrentServerEntry().address.contains("mcfunny")) {
            return name.contains("helper") || name.contains("moder") || name.contains("\u043c\u043e\u0434\u0435\u0440") || name.contains("\u0445\u0435\u043b\u043f\u0435\u0440");
        }
        return name.contains("helper") || name.contains("moder") || name.contains("admin") || name.contains("owner") || name.contains("curator") || name.contains("\u043a\u0443\u0440\u0430\u0442\u043e\u0440") || name.contains("\u043c\u043e\u0434\u0435\u0440") || name.contains("\u0430\u0434\u043c\u0438\u043d") || name.contains("\u0445\u0435\u043b\u043f\u0435\u0440") || name.contains("\u043f\u043e\u0434\u0434\u0435\u0440\u0436\u043a\u0430") || name.contains("\u0441\u043e\u0442\u0440\u0443\u0434\u043d\u0438\u043a") || name.contains("\u0437\u0430\u043c") || name.contains("\u0441\u0442\u0430\u0436\u0451\u0440");
    }

    @Override
    public void onRender2D(DrawContext context) {
        super.onRender2D(context);
        ArrayList<String> all = new ArrayList<String>();
        all.addAll(this.players);
        all.addAll(this.notSpec);
        int y_offset1 = 0;
        float max_width = 50.0f;
        float pointerX = 0.0f;
        for (String player : all) {
            float timeWidth;
            if (y_offset1 == 0) {
                y_offset1 += 4;
            }
            y_offset1 += 9;
            float nameWidth = FontRenderers.sf_bold_mini.getStringWidth(player.split(":")[0]);
            float width = (nameWidth + (timeWidth = FontRenderers.sf_bold_mini.getStringWidth(player.split(":")[1].equalsIgnoreCase("vanish") ? String.valueOf(Formatting.RED) + "V" : (player.split(":")[1].equalsIgnoreCase("gm3") ? String.valueOf(Formatting.RED) + "V " + String.valueOf(Formatting.YELLOW) + "(GM3)" : String.valueOf(Formatting.GREEN) + "Z")))) * 1.4f;
            if (width > max_width) {
                max_width = width;
            }
            if (!(timeWidth > pointerX)) continue;
            pointerX = timeWidth;
        }
        this.vAnimation = AnimationUtility.fast(this.vAnimation, 14 + y_offset1, 15.0f);
        this.hAnimation = AnimationUtility.fast(this.hAnimation, max_width, 15.0f);
        Render2DEngine.drawHudBase(context.getMatrices(), this.getPosX(), this.getPosY(), this.hAnimation, this.vAnimation, HudEditor.hudRound.getValue().floatValue());
        if (HudEditor.hudStyle.is(HudEditor.HudStyle.Glowing)) {
            FontRenderers.sf_bold.drawCenteredString(context.getMatrices(), "Staff", (double)(this.getPosX() + this.hAnimation / 2.0f), (double)(this.getPosY() + 4.0f), HudEditor.textColor.getValue().getColorObject());
        } else {
            FontRenderers.sf_bold.drawGradientCenteredString(context.getMatrices(), "Staff", this.getPosX() + this.hAnimation / 2.0f, this.getPosY() + 4.0f, 10);
        }
        if (y_offset1 > 0) {
            if (HudEditor.hudStyle.is(HudEditor.HudStyle.Blurry)) {
                Render2DEngine.drawRectDumbWay(context.getMatrices(), this.getPosX() + 4.0f, this.getPosY() + 13.0f, this.getPosX() + this.getWidth() - 8.0f, this.getPosY() + 14.0f, new Color(0x54FFFFFF, true));
            } else {
                Render2DEngine.horizontalGradient(context.getMatrices(), this.getPosX() + 2.0f, this.getPosY() + 13.7f, this.getPosX() + 2.0f + this.hAnimation / 2.0f - 2.0f, this.getPosY() + 13.5f, Render2DEngine.injectAlpha(HudEditor.textColor.getValue().getColorObject(), 0), HudEditor.textColor.getValue().getColorObject());
                Render2DEngine.horizontalGradient(context.getMatrices(), this.getPosX() + 2.0f + this.hAnimation / 2.0f - 2.0f, this.getPosY() + 13.7f, this.getPosX() + 2.0f + this.hAnimation - 4.0f, this.getPosY() + 14.0f, HudEditor.textColor.getValue().getColorObject(), Render2DEngine.injectAlpha(HudEditor.textColor.getValue().getColorObject(), 0));
            }
        }
        Render2DEngine.addWindow(context.getMatrices(), this.getPosX(), this.getPosY(), this.getPosX() + this.hAnimation, this.getPosY() + this.vAnimation, 1.0);
        int y_offset = 0;
        for (String player : all) {
            float px = this.getPosX() + (max_width - pointerX - 10.0f);
            Identifier tex = this.getTexture(player);
            if (tex != null) {
                context.drawTexture(tex, (int)(this.getPosX() + 3.0f), (int)(this.getPosY() + 16.0f + (float)y_offset), 8, 8, 8.0f, 8.0f, 8, 8, 64, 64);
                context.drawTexture(tex, (int)(this.getPosX() + 3.0f), (int)(this.getPosY() + 16.0f + (float)y_offset), 8, 8, 40.0f, 8.0f, 8, 8, 64, 64);
            }
            FontRenderers.sf_bold_mini.drawString(context.getMatrices(), player.split(":")[0], (double)(this.getPosX() + 13.0f), (double)(this.getPosY() + 19.0f + (float)y_offset), HudEditor.textColor.getValue().getColor());
            FontRenderers.sf_bold_mini.drawCenteredString(context.getMatrices(), player.split(":")[1].equalsIgnoreCase("vanish") ? String.valueOf(Formatting.RED) + "O" : (player.split(":")[1].equalsIgnoreCase("gm3") ? String.valueOf(Formatting.YELLOW) + "O" : String.valueOf(Formatting.GREEN) + "O"), (double)(px + (this.getPosX() + max_width - px) / 2.0f), (double)(this.getPosY() + 19.0f + (float)y_offset), HudEditor.textColor.getValue().getColor());
            Render2DEngine.drawRect(context.getMatrices(), px, this.getPosY() + 17.0f + (float)y_offset, 0.5f, 8.0f, new Color(0x44FFFFFF, true));
            y_offset += 9;
        }
        Render2DEngine.popWindow();
        this.setBounds(this.getPosX(), this.getPosY(), this.hAnimation, this.vAnimation);
    }

    @Override
    public void onUpdate() {
        if (StaffBoard.mc.player != null && StaffBoard.mc.player.age % 10 == 0) {
            this.players = this.getVanish();
            this.notSpec = StaffBoard.getOnlinePlayerD();
            this.players.sort(String::compareTo);
            this.notSpec.sort(String::compareTo);
        }
    }

    private Identifier getTexture(String n) {
        Identifier id = null;
        if (this.skinMap.containsKey(n)) {
            id = this.skinMap.get(n);
        }
        for (PlayerListEntry ple : mc.getNetworkHandler().getPlayerList()) {
            if (!n.contains(ple.getProfile().getName())) continue;
            id = ple.getSkinTextures().texture();
            if (this.skinMap.containsKey(n)) break;
            this.skinMap.put(n, id);
            break;
        }
        return id;
    }
}

