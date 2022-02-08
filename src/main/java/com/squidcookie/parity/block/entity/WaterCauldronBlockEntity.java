package com.squidcookie.parity.block.entity;

import com.squidcookie.parity.util.Math;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class WaterCauldronBlockEntity
extends BlockEntity {
    /**
     * Hex color code (stored as a base ten integer)
     */
    private int color = -1;
    NbtCompound potion;

    public WaterCauldronBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityType.WATER_CAULDRON, pos, state);
    }

    /**
     * Alternative name used by Yarn in {@link net.minecraft.item.DyeableItem}: "blendAndSetColor"
     */
    public void addColor(DyeItem dyeItem) {
        float[] components = dyeItem.getColor().getColorComponents();
        int[] color = {(this.color & 0xFF0000) >> 16, (this.color & 0xFF00) >> 8, this.color & 0xFF};
        int[] newColor = new int[3];

        newColor[0] = (int)(components[0] * 255.0F);
        newColor[1] = (int)(components[1] * 255.0F);
        newColor[2] = (int)(components[2] * 255.0F);
        if (hasColor()) {
            int[] avgColor = new int[3];
            avgColor[0] = (color[0] + newColor[0]) / 2;
            avgColor[1] = (color[1] + newColor[1]) / 2;
            avgColor[2] = (color[2] + newColor[2]) / 2;
            float avgMax = (Math.max(color) + Math.max(newColor)) / 2.0F;
            float maxOfAvg = (float)Math.max(avgColor);
            float gainFactor = avgMax / maxOfAvg;
            color[0] = (int)(avgColor[0] * gainFactor);
            color[1] = (int)(avgColor[1] * gainFactor);
            color[2] = (int)(avgColor[2] * gainFactor);
        } else {
            color = newColor;
        }
        this.color = (color[0] << 16) + (color[1] << 8) + color[2];
        this.markDirty();
    }

    public boolean hasColor() {
        return this.color != -1;
    }

    public int getColor() {
        return this.color;
    }

    public void removeColor() {
        this.color = -1;
        this.markDirty();
    }

    public void setPotion(ItemStack stack) {
        this.potion = stack.getNbt();
        this.color = PotionUtil.getColor(stack);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putInt("Color", this.color);
        if (this.potion != null) {
            nbt.put("Potion", this.potion);
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.color = nbt.getInt("Color");
        if (nbt.contains("Potion", 10)) {
            this.potion = nbt.getCompound("Potion");
        }
        this.markDirty();
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return this.createNbt();
    }

    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public void markDirty() {
        if (this.world != null) {
            if (this.world.isClient) {
                MinecraftClient.getInstance().worldRenderer.scheduleBlockRenders(this.pos.getX(), this.pos.getY(), this.pos.getZ(), this.pos.getX(), this.pos.getY(), this.pos.getZ());
            } else {
                ((ServerWorld)this.world).getChunkManager().markForUpdate(this.pos);
            }
            super.markDirty();
        }
    }
}