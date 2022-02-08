package com.squidcookie.parity.entity;

import com.squidcookie.parity.block.SnowLandingBlock;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.item.AutomaticItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;

import java.util.Objects;

public class SnowFallingBlockEntity
extends Entity {
    public int timeFalling;
    private int layers;
    public boolean dropItem = true;
    private long discardTime;
    public NbtCompound blockEntityData;
    private BlockState block = Blocks.SNOW.getDefaultState();
    protected static final TrackedData<BlockPos> BLOCK_POS = DataTracker.registerData(SnowFallingBlockEntity.class, TrackedDataHandlerRegistry.BLOCK_POS);

    public SnowFallingBlockEntity(EntityType<? extends SnowFallingBlockEntity> entityType, World world) {
        super(entityType, world);
    }

    public SnowFallingBlockEntity(World world, double x, double y, double z, BlockState state) {
        this(ModEntityType.SNOW_FALLING_BLOCK, world);
        this.block = state;
        this.layers = this.block.get(SnowBlock.LAYERS);
        this.intersectionChecked = true;
        this.setPosition(x, y + (double)((1.0f - this.getHeight()) / 2.0f), z);
        this.setVelocity(Vec3d.ZERO);
        this.prevX = x;
        this.prevY = y;
        this.prevZ = z;
        this.setFallingBlockPos(this.getBlockPos());
    }

    @Override
    public boolean isAttackable() {
        return false;
    }

    public void setFallingBlockPos(BlockPos pos) {
        this.dataTracker.set(BLOCK_POS, pos);
    }

    public BlockPos getFallingBlockPos() {
        return this.dataTracker.get(BLOCK_POS);
    }

    @Override
    protected Entity.MoveEffect getMoveEffect() {
        return Entity.MoveEffect.NONE;
    }

    @Override
    protected void initDataTracker() {
        this.dataTracker.startTracking(BLOCK_POS, BlockPos.ORIGIN);
    }

    @Override
    public boolean collides() {
        return !this.isRemoved();
    }

    @Override
    public void tick() {
        BlockPos blockPos;
        if (this.world.isClient && this.discardTime > 0L) {
            if (System.currentTimeMillis() >= this.discardTime) {
                super.setRemoved(Entity.RemovalReason.DISCARDED);
            }
            return;
        }
        Block block = this.block.getBlock();
        if (block != Blocks.SNOW) {
            LOGGER.error("Cannot load block other than snow");
            this.discard();
            return;
        }
        int layers = this.layers;
        ItemStack snowballItemStack = new ItemStack(Items.SNOWBALL, layers);
        if (this.timeFalling++ == 0) {
            blockPos = this.getBlockPos();
            if (this.world.getBlockState(blockPos).isOf(block)) {
                this.world.removeBlock(blockPos, false);
            } else if (!this.world.isClient) {
                this.discard();
                return;
            }
        }
        if (!this.hasNoGravity()) {
            this.setVelocity(this.getVelocity().add(0.0D, -0.04D, 0.0D));
        }
        this.move(MovementType.SELF, this.getVelocity());
        if (!this.world.isClient) {
            blockPos = this.getBlockPos();
            boolean isInFluid = this.world.getFluidState(blockPos).isIn(FluidTags.WATER) || this.world.getFluidState(blockPos).isIn(FluidTags.LAVA);
            if (this.onGround || isInFluid) {
                BlockState blockHitResult = this.world.getBlockState(blockPos);
                boolean isEightSnowLayers = this.world.getBlockState(blockPos).isOf(Blocks.SNOW) && this.world.getBlockState(blockPos).get(SnowBlock.LAYERS) == 8;
                boolean isOneSnowLayerOnEightSnowLayers = isEightSnowLayers && this.world.getBlockState(blockPos.up()).isOf(Blocks.SNOW) && this.world.getBlockState(blockPos.up()).get(SnowBlock.LAYERS) == 1;
                if (isOneSnowLayerOnEightSnowLayers) {
                    blockHitResult = this.world.getBlockState(blockPos.up());
                    blockPos = blockPos.up();
                }
                this.setVelocity(this.getVelocity().multiply(0.7D, -0.5D, 0.7D));
                if (!blockHitResult.isOf(Blocks.MOVING_PISTON)) {
                    if (!isInFluid) {
                        if (!blockHitResult.isOf(Blocks.SNOW) || isEightSnowLayers && !isOneSnowLayerOnEightSnowLayers) {
                            if (blockHitResult.isOf(Blocks.SNOW)){
                                blockPos = blockPos.up();
                            }
                            boolean bool = blockHitResult.canReplace(new AutomaticItemPlacementContext(this.world, blockPos, Direction.DOWN, ItemStack.EMPTY, Direction.UP)) || blockHitResult.isOf(Blocks.SNOW);
                            boolean bool2 = FallingBlock.canFallThrough(this.world.getBlockState(blockPos.down())) && !blockHitResult.isOf(Blocks.SNOW);
                            boolean bool3 = this.block.canPlaceAt(this.world, blockPos) && !bool2;
                            if (bool && bool3) {
                                if (this.world.setBlockState(blockPos, this.block, Block.NOTIFY_ALL)) {
                                    blockPos = this.getBlockPos();
                                    ((ServerWorld)this.world).getChunkManager().threadedAnvilChunkStorage.sendToOtherNearbyPlayers(this, new BlockUpdateS2CPacket(blockPos, this.world.getBlockState(blockPos)));
                                    this.discard();
                                    this.onLanding(block, blockPos, blockHitResult);
                                    if (this.blockEntityData != null && this.block.hasBlockEntity()) {
                                        BlockEntity blockEntity = this.world.getBlockEntity(blockPos);
                                        if (blockEntity != null) {
                                            NbtCompound nbtCompound = blockEntity.createNbt();
                                            for (String string : this.blockEntityData.getKeys()) {
                                                nbtCompound.put(string, Objects.requireNonNull(this.blockEntityData.get(string)).copy());
                                            }
                                            try {
                                                blockEntity.readNbt(nbtCompound);
                                            } catch (Exception exception) {
                                                LOGGER.error("Failed to load block entity from falling block", exception);
                                            }
                                            blockEntity.markDirty();
                                        }
                                    }
                                } else if (this.dropItem && this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
                                    this.discard();
                                    this.onDestroyedOnLanding(block, blockPos);
                                    this.dropStack(snowballItemStack);
                                }
                            } else {
                                this.discard();
                                if (this.dropItem && this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
                                    this.onDestroyedOnLanding(block, blockPos);
                                    this.dropStack(snowballItemStack);
                                }
                            }
                        } else {
                            this.discard();
                            layers = layers + blockHitResult.get(SnowBlock.LAYERS);
                            if (layers <= 8) {
                                this.world.setBlockState(blockPos, this.block.with(SnowBlock.LAYERS, layers));
                            } else {
                                layers %= 8;
                                this.world.setBlockState(blockPos, this.block.with(SnowBlock.LAYERS, 8));
                                this.world.setBlockState(blockPos.up(), this.block.with(SnowBlock.LAYERS, layers));
                            }
                            this.onLanding(block, blockPos, blockHitResult);
                        }
                    } else {
                        this.discard();
                        if (this.world.getFluidState(blockPos).isIn(FluidTags.WATER)) {
                            this.world.setBlockState(blockPos, Blocks.ICE.getDefaultState(), Block.NOTIFY_ALL);
                        } else if (this.world.getFluidState(blockPos).isIn(FluidTags.LAVA)) {
                            this.world.syncWorldEvent(WorldEvents.LAVA_EXTINGUISHED, blockPos, 0);
                        }
                        this.onDestroyedOnLanding(block, blockPos);
                    }
                }
            } else if (!(this.world.isClient || (this.timeFalling <= 100 || blockPos.getY() > this.world.getBottomY() && blockPos.getY() <= this.world.getTopY()) && this.timeFalling <= 600)) {
                this.discard();
                if (this.dropItem && this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
                    this.dropStack(snowballItemStack);
                }
            }
        }
        this.setVelocity(this.getVelocity().multiply(0.98));
    }

    @Override
    public void setRemoved(Entity.RemovalReason reason) {
        if (this.world.shouldRemoveEntityLater(reason)) {
            this.discardTime = System.currentTimeMillis() + 50L;
            return;
        }
        super.setRemoved(reason);
    }

    public void onLanding(Block block, BlockPos pos, BlockState state) {
        if (block instanceof SnowLandingBlock) {
            ((SnowLandingBlock)block).onLanding(this.world, pos, this.block, state, this);
        }
    }

    public void onDestroyedOnLanding(Block block, BlockPos pos) {
        if (block instanceof SnowLandingBlock) {
            ((SnowLandingBlock)block).onDestroyedOnLanding(this.world, pos, this);
        }
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putInt("Time", this.timeFalling);
        nbt.putInt("Layers", this.layers);
        nbt.putBoolean("DropItem", this.dropItem);
        if (this.blockEntityData != null) {
            nbt.put("TileEntityData", this.blockEntityData);
        }
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        this.timeFalling = nbt.getInt("Time");
        this.layers = nbt.getInt("Layers");
        if (this.layers == 0) {
            this.layers = 1;
        }
        this.block = this.block.with(SnowBlock.LAYERS, this.layers);
        if (nbt.contains("DropItem", 99)) {
            this.dropItem = nbt.getBoolean("DropItem");
        }
        if (nbt.contains("TileEntityData", 10)) {
            this.blockEntityData = nbt.getCompound("TileEntityData");
        }
    }

    @Override
    public boolean doesRenderOnFire() {
        return false;
    }

    @Override
    public void populateCrashReport(CrashReportSection section) {
        super.populateCrashReport(section);
        section.add("Imitating BlockState", this.block.toString());
    }

    public BlockState getBlockState() {
        return this.block;
    }

    @Override
    public boolean entityDataRequiresOperator() {
        return true;
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this, Block.getRawIdFromState(this.getBlockState()));
    }

    @Override
    public void onSpawnPacket(EntitySpawnS2CPacket packet) {
        super.onSpawnPacket(packet);
        this.block = Block.getStateFromRawId(packet.getEntityData());
        this.intersectionChecked = true;
        double d = packet.getX();
        double e = packet.getY();
        double f = packet.getZ();
        this.setPosition(d, e + (double)((1.0f - this.getHeight()) / 2.0f), f);
        this.setFallingBlockPos(this.getBlockPos());
    }
}
