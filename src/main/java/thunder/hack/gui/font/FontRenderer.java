/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  it.unimi.dsi.fastutil.chars.Char2IntArrayMap
 *  it.unimi.dsi.fastutil.chars.Char2ObjectArrayMap
 *  it.unimi.dsi.fastutil.objects.Object2ObjectMap
 *  it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  it.unimi.dsi.fastutil.objects.ObjectList
 *  net.minecraft.client.render.BufferBuilder
 *  net.minecraft.client.render.Tessellator
 *  net.minecraft.client.render.VertexFormats
 *  net.minecraft.client.render.VertexFormat.DrawMode
 *  net.minecraft.util.Identifier
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.client.render.GameRenderer
 *  org.joml.Matrix4f
 *  org.lwjgl.opengl.GL11
 */
package thunder.hack.gui.font;

import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.chars.Char2IntArrayMap;
import it.unimi.dsi.fastutil.chars.Char2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import java.awt.Color;
import java.awt.Font;
import java.io.Closeable;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.util.Identifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.GameRenderer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import thunder.hack.core.manager.IManager;
import thunder.hack.features.modules.client.HudEditor;
import thunder.hack.gui.font.Glyph;
import thunder.hack.gui.font.GlyphMap;
import thunder.hack.utility.math.MathUtility;
import thunder.hack.utility.render.Render2DEngine;

public class FontRenderer
implements Closeable {
    private static final Char2IntArrayMap colorCodes = new Char2IntArrayMap(){
        {
            this.put('0', 0);
            this.put('1', 170);
            this.put('2', 43520);
            this.put('3', 43690);
            this.put('4', 0xAA0000);
            this.put('5', 0xAA00AA);
            this.put('6', 0xFFAA00);
            this.put('7', 0xAAAAAA);
            this.put('8', 0x555555);
            this.put('9', 0x5555FF);
            this.put('A', 0x55FF55);
            this.put('B', 0x55FFFF);
            this.put('C', 0xFF5555);
            this.put('D', 0xFF55FF);
            this.put('E', 0xFFFF55);
            this.put('F', 0xFFFFFF);
        }
    };
    private static final ExecutorService ASYNC_WORKER = Executors.newCachedThreadPool();
    private final Object2ObjectMap<Identifier, ObjectList<DrawEntry>> GLYPH_PAGE_CACHE = new Object2ObjectOpenHashMap();
    private final float originalSize;
    private final ObjectList<GlyphMap> maps = new ObjectArrayList();
    private final Char2ObjectArrayMap<Glyph> allGlyphs = new Char2ObjectArrayMap();
    private final int charsPerPage;
    private final int padding;
    private final String prebakeGlyphs;
    private int scaleMul = 0;
    private Font font;
    private int previousGameScale = -1;
    private Future<Void> prebakeGlyphsFuture;
    private boolean initialized;

    public FontRenderer(Font font, float sizePx, int charactersPerPage, int paddingBetweenCharacters, @Nullable String prebakeCharacters) {
        this.originalSize = sizePx;
        this.charsPerPage = charactersPerPage;
        this.padding = paddingBetweenCharacters;
        this.prebakeGlyphs = prebakeCharacters;
        this.init(font, sizePx);
    }

    public FontRenderer(Font font, float sizePx) {
        this(font, sizePx, 256, 5, null);
    }

    private static int floorNearestMulN(int x, int n) {
        return n * (int)Math.floor((double)x / (double)n);
    }

    public static String stripControlCodes(String text) {
        char[] chars = text.toCharArray();
        StringBuilder f = new StringBuilder();
        for (int i = 0; i < chars.length; ++i) {
            char c = chars[i];
            if (c == '\u00a7') {
                ++i;
                continue;
            }
            f.append(c);
        }
        return f.toString();
    }

    private void sizeCheck() {
        int gs = (int)IManager.mc.getWindow().getScaleFactor();
        if (gs != this.previousGameScale) {
            this.close();
            this.init(this.font, this.originalSize);
        }
    }

    private void init(Font font, float sizePx) {
        if (this.initialized) {
            throw new IllegalStateException("Double call to init()");
        }
        this.initialized = true;
        this.scaleMul = this.previousGameScale = (int)IManager.mc.getWindow().getScaleFactor();
        this.font = font.deriveFont(sizePx * (float)this.scaleMul);
        if (this.prebakeGlyphs != null && !this.prebakeGlyphs.isEmpty()) {
            this.prebakeGlyphsFuture = this.prebake();
        }
    }

    private Future<Void> prebake() {
        return ASYNC_WORKER.submit(() -> {
            for (char c : this.prebakeGlyphs.toCharArray()) {
                if (Thread.interrupted()) break;
                this.locateGlyph1(c);
            }
            return null;
        });
    }

    private GlyphMap generateMap(char from, char to) {
        GlyphMap gm = new GlyphMap(from, to, this.font, FontRenderer.randomIdentifier(), this.padding);
        this.maps.add((Object)gm);
        return gm;
    }

    private Glyph locateGlyph0(char glyph) {
        for (GlyphMap map : this.maps) {
            if (!map.contains(glyph)) continue;
            return map.getGlyph(glyph);
        }
        int base = FontRenderer.floorNearestMulN(glyph, this.charsPerPage);
        GlyphMap glyphMap = this.generateMap((char)base, (char)(base + this.charsPerPage));
        return glyphMap.getGlyph(glyph);
    }

    @Nullable
    private Glyph locateGlyph1(char glyph) {
        return (Glyph)this.allGlyphs.computeIfAbsent(glyph, this::locateGlyph0);
    }

    public void drawString(MatrixStack stack, String s, double x, double y, int color) {
        float r = (float)(color >> 16 & 0xFF) / 255.0f;
        float g = (float)(color >> 8 & 0xFF) / 255.0f;
        float b = (float)(color & 0xFF) / 255.0f;
        float a = (float)(color >> 24 & 0xFF) / 255.0f;
        this.drawString(stack, s, (float)x, (float)y, r, g, b, a);
    }

    public void drawString(MatrixStack stack, String s, double x, double y, Color color) {
        this.drawString(stack, s, (float)x, (float)y, (float)color.getRed() / 255.0f, (float)color.getGreen() / 255.0f, (float)color.getBlue() / 255.0f, color.getAlpha());
    }

    public void drawString(MatrixStack stack, String s, float x, float y, float r, float g, float b, float a) {
        this.drawString(stack, s, x, y, r, g, b, a, false, 0);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void drawString(MatrixStack stack, String s, float x, float y, float r, float g, float b, float a, boolean gradient, int offset) {
        if (this.prebakeGlyphsFuture != null && !this.prebakeGlyphsFuture.isDone()) {
            try {
                this.prebakeGlyphsFuture.get();
            }
            catch (InterruptedException | ExecutionException exception) {
                // empty catch block
            }
        }
        this.sizeCheck();
        float r2 = r;
        float g2 = g;
        float b2 = b;
        stack.push();
        stack.translate(MathUtility.roundToDecimal(x, 1), MathUtility.roundToDecimal(y -= 3.0f, 1), 0.0);
        stack.scale(1.0f / (float)this.scaleMul, 1.0f / (float)this.scaleMul, 1.0f);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        GL11.glTexParameteri((int)3553, (int)10241, (int)9729);
        GL11.glTexParameteri((int)3553, (int)10240, (int)9729);
        RenderSystem.setShader(GameRenderer::getPositionTexColorProgram);
        Matrix4f mat = stack.peek().getPositionMatrix();
        char[] chars = s.toCharArray();
        float xOffset = 0.0f;
        float yOffset = 0.0f;
        boolean inSel = false;
        int lineStart = 0;
        Object2ObjectMap<Identifier, ObjectList<DrawEntry>> object2ObjectMap = this.GLYPH_PAGE_CACHE;
        synchronized (object2ObjectMap) {
            for (int i = 0; i < chars.length; ++i) {
                char c = chars[i];
                if (inSel) {
                    inSel = false;
                    char c1 = Character.toUpperCase(c);
                    if (colorCodes.containsKey(c1)) {
                        int ii = colorCodes.get(c1);
                        int[] col = FontRenderer.RGBIntToRGB(ii);
                        r2 = (float)col[0] / 255.0f;
                        g2 = (float)col[1] / 255.0f;
                        b2 = (float)col[2] / 255.0f;
                        continue;
                    }
                    if (c1 != 'R') continue;
                    r2 = r;
                    g2 = g;
                    b2 = b;
                    continue;
                }
                if (gradient) {
                    Color color = HudEditor.getColor(i * offset);
                    r2 = (float)color.getRed() / 255.0f;
                    g2 = (float)color.getGreen() / 255.0f;
                    b2 = (float)color.getBlue() / 255.0f;
                    a = (float)color.getAlpha() / 255.0f;
                }
                if (c == '\u00a7') {
                    inSel = true;
                    continue;
                }
                if (c == '\n') {
                    yOffset += this.getStringHeight(s.substring(lineStart, i)) * (float)this.scaleMul;
                    xOffset = 0.0f;
                    lineStart = i + 1;
                    continue;
                }
                Glyph glyph = this.locateGlyph1(c);
                if (glyph == null) continue;
                if (glyph.value() != ' ') {
                    Identifier i1 = glyph.owner().bindToTexture;
                    DrawEntry entry = new DrawEntry(xOffset, yOffset, r2, g2, b2, glyph);
                    ((ObjectList)this.GLYPH_PAGE_CACHE.computeIfAbsent((Object)i1, integer -> new ObjectArrayList())).add((Object)entry);
                }
                xOffset += (float)glyph.width();
            }
            for (Identifier identifier : this.GLYPH_PAGE_CACHE.keySet()) {
                RenderSystem.setShaderTexture((int)0, (Identifier)identifier);
                List objects = (List)this.GLYPH_PAGE_CACHE.get((Object)identifier);
                BufferBuilder bb = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
                for (DrawEntry object : objects) {
                    float xo = object.atX;
                    float yo = object.atY;
                    float cr = object.r;
                    float cg = object.g;
                    float cb = object.b;
                    Glyph glyph = object.toDraw;
                    GlyphMap owner = glyph.owner();
                    float w = glyph.width();
                    float h = glyph.height();
                    float u1 = (float)glyph.u() / (float)owner.width;
                    float v1 = (float)glyph.v() / (float)owner.height;
                    float u2 = (float)(glyph.u() + glyph.width()) / (float)owner.width;
                    float v2 = (float)(glyph.v() + glyph.height()) / (float)owner.height;
                    bb.vertex(mat, xo + 0.0f, yo + h, 0.0f).texture(u1, v2).color(cr, cg, cb, a);
                    bb.vertex(mat, xo + w, yo + h, 0.0f).texture(u2, v2).color(cr, cg, cb, a);
                    bb.vertex(mat, xo + w, yo + 0.0f, 0.0f).texture(u2, v1).color(cr, cg, cb, a);
                    bb.vertex(mat, xo + 0.0f, yo + 0.0f, 0.0f).texture(u1, v1).color(cr, cg, cb, a);
                }
                Render2DEngine.endBuilding(bb);
            }
            this.GLYPH_PAGE_CACHE.clear();
        }
        stack.pop();
    }

    public void drawCenteredString(MatrixStack stack, String s, double x, double y, int color) {
        float r = (float)(color >> 16 & 0xFF) / 255.0f;
        float g = (float)(color >> 8 & 0xFF) / 255.0f;
        float b = (float)(color & 0xFF) / 255.0f;
        float a = (float)(color >> 24 & 0xFF) / 255.0f;
        this.drawString(stack, s, (float)(x - (double)(this.getStringWidth(s) / 2.0f)), (float)y, r, g, b, a);
    }

    public void drawCenteredString(MatrixStack stack, String s, double x, double y, Color color) {
        this.drawString(stack, s, (float)(x - (double)(this.getStringWidth(s) / 2.0f)), (float)y, (float)color.getRed() / 255.0f, (float)color.getGreen() / 255.0f, (float)color.getBlue() / 255.0f, (float)color.getAlpha() / 255.0f);
    }

    public void drawCenteredString(MatrixStack stack, String s, float x, float y, float r, float g, float b, float a) {
        this.drawString(stack, s, x - this.getStringWidth(s) / 2.0f, y, r, g, b, a);
    }

    public float getStringWidth(String text) {
        char[] c = FontRenderer.stripControlCodes(text).toCharArray();
        float currentLine = 0.0f;
        float maxPreviousLines = 0.0f;
        for (char c1 : c) {
            if (c1 == '\n') {
                maxPreviousLines = Math.max(currentLine, maxPreviousLines);
                currentLine = 0.0f;
                continue;
            }
            Glyph glyph = this.locateGlyph1(c1);
            currentLine += glyph == null ? 0.0f : (float)glyph.width() / (float)this.scaleMul;
        }
        return Math.max(currentLine, maxPreviousLines);
    }

    public float getStringHeight(String text) {
        char[] c = FontRenderer.stripControlCodes(text).toCharArray();
        if (c.length == 0) {
            c = new char[]{' '};
        }
        float currentLine = 0.0f;
        float previous = 0.0f;
        for (char c1 : c) {
            if (c1 == '\n') {
                if (currentLine == 0.0f) {
                    currentLine = this.locateGlyph1(' ') == null ? 0.0f : (float)Objects.requireNonNull(this.locateGlyph1(' ')).height() / (float)this.scaleMul;
                }
                previous += currentLine;
                currentLine = 0.0f;
                continue;
            }
            Glyph glyph = this.locateGlyph1(c1);
            currentLine = Math.max(glyph == null ? 0.0f : (float)glyph.height() / (float)this.scaleMul, currentLine);
        }
        return currentLine + previous;
    }

    @Override
    public void close() {
        try {
            if (this.prebakeGlyphsFuture != null && !this.prebakeGlyphsFuture.isDone() && !this.prebakeGlyphsFuture.isCancelled()) {
                this.prebakeGlyphsFuture.cancel(true);
                this.prebakeGlyphsFuture.get();
                this.prebakeGlyphsFuture = null;
            }
            for (GlyphMap map : this.maps) {
                map.destroy();
            }
            this.maps.clear();
            this.allGlyphs.clear();
            this.initialized = false;
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    @Contract(value="-> new", pure=true)
    @NotNull
    public static Identifier randomIdentifier() {
        return Identifier.of((String)"thunderhack", (String)("temp/" + FontRenderer.randomString()));
    }

    private static String randomString() {
        return IntStream.range(0, 32).mapToObj(operand -> String.valueOf((char)new Random().nextInt(97, 123))).collect(Collectors.joining());
    }

    @Contract(value="_ -> new", pure=true)
    public static int @NotNull [] RGBIntToRGB(int in) {
        int red = in >> 16 & 0xFF;
        int green = in >> 8 & 0xFF;
        int blue = in & 0xFF;
        return new int[]{red, green, blue};
    }

    public float getFontHeight(String str) {
        return this.getStringHeight(str);
    }

    public void drawGradientString(MatrixStack stack, String s, float x, float y, int offset) {
        this.drawString(stack, s, x, y, 255.0f, 255.0f, 255.0f, 255.0f, true, offset);
    }

    public void drawGradientCenteredString(MatrixStack matrices, String s, float x, float y, int i) {
        this.drawGradientString(matrices, s, x - this.getStringWidth(s) / 2.0f, y, i);
    }

    record DrawEntry(float atX, float atY, float r, float g, float b, Glyph toDraw) {
    }
}

