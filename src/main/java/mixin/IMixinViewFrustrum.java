package mixin;

import net.minecraft.client.renderer.ViewFrustum;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ViewFrustum.class)
public interface IMixinViewFrustrum {
	@Invoker
	void callGetRenderChunk(BlockPos pos);
}
