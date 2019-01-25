package com.github.lunatrius.schematica.util;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.util.math.BlockPos;

public interface RenderChunkAccessor {
    public void preRenderBlocks(final BufferBuilder buffer, final BlockPos pos);
}
