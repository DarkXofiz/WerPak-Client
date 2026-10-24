/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Box
 *  net.minecraft.client.util.math.MatrixStack
 */
package thunder.hack.utility.render;

import java.awt.Color;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.client.util.math.MatrixStack;
import thunder.hack.utility.render.Render2DEngine;
import thunder.hack.utility.render.Render3DEngine;

public final class BlockAnimationUtility {
    private static final Map<BlockRenderData, Long> blocks = new ConcurrentHashMap<BlockRenderData, Long>();

    public static void onRender(MatrixStack matrixStack) {
        blocks.forEach((animation, time) -> {
            if ((float)(System.currentTimeMillis() - time) > 300.0f) {
                blocks.remove(animation);
            } else {
                animation.renderWithTime(System.currentTimeMillis() - time, matrixStack);
            }
        });
    }

    public static void renderBlock(BlockPos pos, Color lineColor, int lineWidth, Color fillColor, BlockAnimationMode animationMode, BlockRenderMode renderMode) {
        if (renderMode == BlockRenderMode.None) {
            return;
        }
        blocks.put(new BlockRenderData(pos, lineColor, lineWidth, fillColor, animationMode, renderMode), System.currentTimeMillis());
    }

    public static boolean isRendering(BlockPos pos) {
        return blocks.keySet().stream().anyMatch(blockRenderData -> blockRenderData.pos().equals((Object)pos));
    }

    public static enum BlockRenderMode {
        Fill,
        Line,
        All,
        None;

    }

    private record BlockRenderData(BlockPos pos, Color lineColor, int lineWidth, Color fillColor, BlockAnimationMode animationMode, BlockRenderMode renderMode) {
        void renderWithTime(Long time, MatrixStack stack) {
            switch (this.animationMode.ordinal()) {
                case 3: {
                    if (this.renderMode == BlockRenderMode.All || this.renderMode == BlockRenderMode.Line) {
                        Render3DEngine.drawBoxOutline(new Box(this.pos), this.lineColor, this.lineWidth);
                    }
                    if (this.renderMode != BlockRenderMode.All && this.renderMode != BlockRenderMode.Fill) break;
                    Render3DEngine.drawFilledBox(stack, new Box(this.pos), this.fillColor);
                    break;
                }
                case 2: {
                    float scale = 1.0f - (float)time.longValue() / 300.0f;
                    Box box = new Box((double)this.pos.getX(), (double)this.pos.getY(), (double)this.pos.getZ(), (double)this.pos.getX(), (double)this.pos.getY(), (double)this.pos.getZ());
                    if (this.renderMode == BlockRenderMode.All || this.renderMode == BlockRenderMode.Line) {
                        Render3DEngine.drawBoxOutline(box.shrink((double)scale, (double)scale, (double)scale).offset(0.5 + (double)scale * 0.5, 0.5 + (double)scale * 0.5, 0.5 + (double)scale * 0.5), this.lineColor, this.lineWidth);
                    }
                    if (this.renderMode != BlockRenderMode.All && this.renderMode != BlockRenderMode.Fill) break;
                    Render3DEngine.drawFilledBox(stack, box.shrink((double)scale, (double)scale, (double)scale).offset(0.5 + (double)scale * 0.5, 0.5 + (double)scale * 0.5, 0.5 + (double)scale * 0.5), Render2DEngine.injectAlpha(this.fillColor, (int)((float)this.fillColor.getAlpha() * (1.0f - (float)time.longValue() / 300.0f))));
                    break;
                }
                case 0: {
                    Box box = new Box(this.pos);
                    BlockRenderData.renderBox(time, stack, box, this.renderMode, this.lineColor, this.lineWidth, this.fillColor);
                    break;
                }
                case 6: {
                    float scale = (float)time.longValue() / 300.0f;
                    Box box = new Box((double)this.pos.getX(), (double)this.pos.getY(), (double)this.pos.getZ(), (double)this.pos.getX(), (double)this.pos.getY(), (double)this.pos.getZ());
                    if (this.renderMode == BlockRenderMode.All || this.renderMode == BlockRenderMode.Line) {
                        Render3DEngine.drawBoxOutline(box.shrink((double)scale, (double)scale, (double)scale).offset(0.5 + (double)scale * 0.5, 0.5 + (double)scale * 0.5, 0.5 + (double)scale * 0.5), this.lineColor, this.lineWidth);
                    }
                    if (this.renderMode != BlockRenderMode.All && this.renderMode != BlockRenderMode.Fill) break;
                    Render3DEngine.drawFilledBox(stack, box.shrink((double)scale, (double)scale, (double)scale).offset(0.5 + (double)scale * 0.5, 0.5 + (double)scale * 0.5, 0.5 + (double)scale * 0.5), Render2DEngine.injectAlpha(this.fillColor, (int)((float)this.fillColor.getAlpha() * ((float)time.longValue() / 300.0f))));
                    break;
                }
                case 4: {
                    float scale = time > 100L ? 1.0f - (float)(time - 100L) / 400.0f : (float)time.longValue() / 100.0f;
                    Box box = new Box((double)this.pos.getX(), (double)this.pos.getY(), (double)this.pos.getZ(), (double)this.pos.getX(), (double)this.pos.getY(), (double)this.pos.getZ());
                    if (this.renderMode == BlockRenderMode.All || this.renderMode == BlockRenderMode.Line) {
                        Render3DEngine.drawBoxOutline(box.shrink((double)scale, (double)scale, (double)scale).offset(0.5 + (double)scale * 0.5, 0.5 + (double)scale * 0.5, 0.5 + (double)scale * 0.5), this.lineColor, this.lineWidth);
                    }
                    if (this.renderMode != BlockRenderMode.All && this.renderMode != BlockRenderMode.Fill) break;
                    Render3DEngine.drawFilledBox(stack, box.shrink((double)scale, (double)scale, (double)scale).offset(0.5 + (double)scale * 0.5, 0.5 + (double)scale * 0.5, 0.5 + (double)scale * 0.5), Render2DEngine.injectAlpha(this.fillColor, (int)((float)this.fillColor.getAlpha() * scale)));
                    break;
                }
                case 5: {
                    float scale = (float)time.longValue() / 300.0f;
                    Box box = new Box((double)this.pos.getX(), (double)((float)this.pos.getY() + scale), (double)this.pos.getZ(), (double)(this.pos.getX() + 1), (double)this.pos.getY(), (double)(this.pos.getZ() + 1));
                    if (this.renderMode == BlockRenderMode.All || this.renderMode == BlockRenderMode.Line) {
                        Render3DEngine.drawBoxOutline(box, this.lineColor, this.lineWidth);
                    }
                    if (this.renderMode != BlockRenderMode.All && this.renderMode != BlockRenderMode.Fill) break;
                    Render3DEngine.drawFilledBox(stack, box, Render2DEngine.injectAlpha(this.fillColor, (int)((float)this.fillColor.getAlpha() * ((float)time.longValue() / 300.0f))));
                    break;
                }
                case 7: {
                    float scale = time < 200L ? 1.0f : 1.0f + ((float)time.longValue() - 200.0f) / 400.0f;
                    Box box = new Box((double)this.pos.getX(), (double)this.pos.getY(), (double)this.pos.getZ(), (double)this.pos.getX(), (double)this.pos.getY(), (double)this.pos.getZ());
                    if (this.renderMode == BlockRenderMode.All || this.renderMode == BlockRenderMode.Line) {
                        Render3DEngine.drawBoxOutline(box.shrink((double)scale, (double)scale, (double)scale).offset(0.5 + (double)(scale * 0.5f), 0.5 + (double)scale * 0.5, 0.5 + (double)scale * 0.5), this.lineColor, this.lineWidth);
                    }
                    if (this.renderMode != BlockRenderMode.All && this.renderMode != BlockRenderMode.Fill) break;
                    Render3DEngine.drawFilledBox(stack, box.shrink((double)scale, (double)scale, (double)scale).offset(0.5 + (double)scale * 0.5, 0.5 + (double)scale * 0.5, 0.5 + (double)scale * 0.5), Render2DEngine.injectAlpha(this.fillColor, (int)((float)this.fillColor.getAlpha() * scale)));
                    break;
                }
                case 8: {
                    float scale = time < 200L ? 1.5f - (float)time.longValue() / 200.0f * 0.5f : 1.0f;
                    Box box = new Box((double)this.pos.getX(), (double)this.pos.getY(), (double)this.pos.getZ(), (double)this.pos.getX(), (double)this.pos.getY(), (double)this.pos.getZ());
                    if (this.renderMode == BlockRenderMode.All || this.renderMode == BlockRenderMode.Line) {
                        Render3DEngine.drawBoxOutline(box.shrink((double)scale, (double)scale, (double)scale).offset(0.5 + (double)(scale * 0.5f), 0.5 + (double)scale * 0.5, 0.5 + (double)scale * 0.5), this.lineColor, this.lineWidth);
                    }
                    if (this.renderMode != BlockRenderMode.All && this.renderMode != BlockRenderMode.Fill) break;
                    Render3DEngine.drawFilledBox(stack, box.shrink((double)scale, (double)scale, (double)scale).offset(0.5 + (double)scale * 0.5, 0.5 + (double)scale * 0.5, 0.5 + (double)scale * 0.5), Render2DEngine.injectAlpha(this.fillColor, (int)((float)this.fillColor.getAlpha() * scale)));
                    break;
                }
                case 1: {
                    float scale = 1.0f + (float)time.longValue() / 1500.0f;
                    Box box = new Box((double)this.pos.getX(), (double)this.pos.getY(), (double)this.pos.getZ(), (double)this.pos.getX(), (double)this.pos.getY(), (double)this.pos.getZ());
                    if (this.renderMode == BlockRenderMode.All || this.renderMode == BlockRenderMode.Line) {
                        Render3DEngine.drawBoxOutline(box.shrink((double)scale, (double)scale, (double)scale).offset(0.5 + (double)(scale * 0.5f), 0.5 + (double)scale * 0.5, 0.5 + (double)scale * 0.5), Render2DEngine.injectAlpha(this.lineColor, (int)((float)this.lineColor.getAlpha() * (1.0f - (float)time.longValue() / 300.0f))), this.lineWidth);
                    }
                    if (this.renderMode != BlockRenderMode.All && this.renderMode != BlockRenderMode.Fill) break;
                    Render3DEngine.drawFilledBox(stack, box.shrink((double)scale, (double)scale, (double)scale).offset(0.5 + (double)scale * 0.5, 0.5 + (double)scale * 0.5, 0.5 + (double)scale * 0.5), Render2DEngine.injectAlpha(this.fillColor, (int)((float)this.fillColor.getAlpha() * (1.0f - (float)time.longValue() / 300.0f))));
                }
            }
        }

        private static void renderBox(Long time, MatrixStack stack, Box box, BlockRenderMode renderMode, Color lineColor, int lineWidth, Color fillColor) {
            if (renderMode == BlockRenderMode.All || renderMode == BlockRenderMode.Line) {
                Render3DEngine.drawBoxOutline(box, Render2DEngine.injectAlpha(lineColor, (int)((float)fillColor.getAlpha() * (1.0f - (float)time.longValue() / 300.0f))), lineWidth);
            }
            if (renderMode == BlockRenderMode.All || renderMode == BlockRenderMode.Fill) {
                Render3DEngine.drawFilledBox(stack, box, Render2DEngine.injectAlpha(fillColor, (int)((float)fillColor.getAlpha() * (1.0f - (float)time.longValue() / 300.0f))));
            }
        }
    }

    public static enum BlockAnimationMode {
        Fade,
        Hover,
        Decrease,
        Static,
        Flash,
        Grow,
        Fill,
        TNT,
        Pull;

    }
}

