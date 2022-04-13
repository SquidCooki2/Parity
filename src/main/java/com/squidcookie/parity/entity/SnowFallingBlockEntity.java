package com.squidcookie.parity.entity;

import com.mojang.logging.LogUtils;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import org.slf4j.Logger;

public class SnowFallingBlockEntity
extends Entity {
    private static final Logger LOGGER = LogUtils.getLogger();
    public int timeFalling;
    private int layers;
    public boolean dropItem = true;
    public NbtCompound blockEntityData;
    private BlockState blockState = Blocks.SNOW.getDefaultState();
    protected static final TrackedData<BlockPos> BLOCK_POS = DataTracker.registerData(SnowFallingBlockEntity.class, TrackedDataHandlerRegistry.BLOCK_POS);

    public SnowFallingBlockEntity(EntityType<? extends SnowFallingBlockEntity> entityType, World world) {
        super(entityType, world);
    }

    public SnowFallingBlockEntity(World world, double x, double y, double z, BlockState state) {
        this(ModEntityType.SNOW_FALLING_BLOCK, world);
        this.blockState = state;
        this.layers = this.blockState.get(SnowBlock.LAYERS);
        this.intersectionChecked = true;
        this.setPosition(x, y + (double)((1.0f - this.getHeight()) / 2.0f), z);
        this.setVelocity(Vec3d.ZERO);
        this.prevX = x;
        this.prevY = y;
        this.prevZ = z;
        this.setFallingBlockPos(this.getBlockPos());
    }

    public static void spawnFromBlock(World world, BlockPos pos, BlockState state) {
        SnowFallingBlockEntity snowFallingBlockEntity = new SnowFallingBlockEntity(world, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, world.getBlockState(pos));
        world.setBlockState(pos, state.getFluidState().getBlockState());
        world.spawnEntity(snowFallingBlockEntity);
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
        Block block = this.blockState.getBlock();
        if (block != Blocks.SNOW) {
            LOGGER.error("Cannot load block other than snow");
            this.discard();
        } else {
            int layers = this.layers;
            ItemStack snowballItemStack = new ItemStack(Items.SNOWBALL, layers);
            this.timeFalling++;
            if (!this.hasNoGravity()) {
                this.setVelocity(this.getVelocity().add(0.0, -0.04, 0.0));
            }
            this.move(MovementType.SELF, this.getVelocity());
            if (!this.world.isClient) {
                BlockPos blockPos = this.getBlockPos();
                boolean isInFluid = this.world.getFluidState(blockPos).isIn(FluidTags.WATER) || this.world.getFluidState(blockPos).isIn(FluidTags.LAVA);
                if (this.onGround || isInFluid) {
                    BlockState blockHitResult = this.world.getBlockState(blockPos);
                    boolean isEightSnowLayers = this.world.getBlockState(blockPos).isOf(Blocks.SNOW) && this.world.getBlockState(blockPos).get(SnowBlock.LAYERS) == 8;
                    boolean isOneSnowLayerOnEightSnowLayers = isEightSnowLayers && this.world.getBlockState(blockPos.up()).isOf(Blocks.SNOW) && this.world.getBlockState(blockPos.up()).get(SnowBlock.LAYERS) == 1;
                    if (isOneSnowLayerOnEightSnowLayers) {
                        blockHitResult = this.world.getBlockState(blockPos.up());
                        blockPos = blockPos.up();
                    }
                    this.setVelocity(this.getVelocity().multiply(0.7, -0.5, 0.7));
                    if (!blockHitResult.isOf(Blocks.MOVING_PISTON)) {
                        if (!isInFluid) {
                            if (!blockHitResult.isOf(Blocks.SNOW) || isEightSnowLayers && !isOneSnowLayerOnEightSnowLayers) {
                                if (blockHitResult.isOf(Blocks.SNOW)) {
                                    blockPos = blockPos.up();
                                }
                                boolean bl = blockHitResult.canReplace(new AutomaticItemPlacementContext(this.world, blockPos, Direction.DOWN, ItemStack.EMPTY, Direction.UP)) || blockHitResult.isOf(Blocks.SNOW);
                                boolean bl2 = FallingBlock.canFallThrough(this.world.getBlockState(blockPos.down())) && !blockHitResult.isOf(Blocks.SNOW);
                                boolean bl3 = this.blockState.canPlaceAt(this.world, blockPos) && !bl2;
                                if (bl && bl3) {
                                    if (this.world.setBlockState(blockPos, this.blockState)) {
                                        blockPos = this.getBlockPos();
                                        ((ServerWorld) this.world).getChunkManager().threadedAnvilChunkStorage.sendToOtherNearbyPlayers(this, new BlockUpdateS2CPacket(blockPos, this.world.getBlockState(blockPos)));
                                        this.discard();
                                        this.onLanding(block, blockPos, blockHitResult);
                                        if (this.blockEntityData != null && this.blockState.hasBlockEntity()) {
                                            BlockEntity blockEntity = this.world.getBlockEntity(blockPos);
                                            if (blockEntity != null) {
                                                NbtCompound nbtCompound = blockEntity.createNbt();
                                                for (String string : this.blockEntityData.getKeys()) {
                                                    nbtCompound.put(string, this.blockEntityData.get(string).copy());
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
                                    this.world.setBlockState(blockPos, this.blockState.with(SnowBlock.LAYERS, layers));
                                } else {
                                    layers %= 8;
                                    this.world.setBlockState(blockPos, this.blockState.with(SnowBlock.LAYERS, 8));
                                    this.world.setBlockState(blockPos.up(), this.blockState.with(SnowBlock.LAYERS, layers));
                                }
                                this.onLanding(block, blockPos, blockHitResult);
                            }
                        } else {
                            this.discard();
                            if (this.world.getFluidState(blockPos).isIn(FluidTags.WATER)) {
                                this.world.setBlockState(blockPos, Blocks.ICE.getDefaultState());
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
    }

    public void onLanding(Block block, BlockPos pos, BlockState state) {
        if (block instanceof SnowLandingBlock) {
            ((SnowLandingBlock)block).onLanding(this.world, pos, this.blockState, state, this);
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
        this.blockState = this.blockState.with(SnowBlock.LAYERS, this.layers);
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

    public BlockState getBlockState() {
        return this.blockState;
    }

    @Override
    public boolean entityDataRequiresOperator() {
        return true;
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this, Block.getRawIdFromState(this.blockState));
    }

    @Override
    public void onSpawnPacket(EntitySpawnS2CPacket packet) {
        super.onSpawnPacket(packet);
        this.blockState = Block.getStateFromRawId(packet.getEntityData());
        this.intersectionChecked = true;
        double d = packet.getX();
        double e = packet.getY();
        double f = packet.getZ();
        this.setPosition(d, e + (double)((1.0f - this.getHeight()) / 2.0f), f);
        this.setFallingBlockPos(this.getBlockPos());
    }
}