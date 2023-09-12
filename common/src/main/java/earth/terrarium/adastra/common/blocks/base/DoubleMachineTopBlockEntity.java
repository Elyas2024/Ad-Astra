package earth.terrarium.adastra.common.blocks.base;

import earth.terrarium.adastra.common.blockentities.base.BasicContainer;
import earth.terrarium.adastra.common.blockentities.base.ContainerMachineBlockEntity;
import earth.terrarium.adastra.common.registry.ModBlockEntityTypes;
import earth.terrarium.botarium.common.energy.base.BotariumEnergyBlock;
import earth.terrarium.botarium.common.energy.base.EnergySnapshot;
import earth.terrarium.botarium.common.energy.impl.SimpleEnergyContainer;
import earth.terrarium.botarium.common.energy.impl.WrappedBlockEnergyContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DoubleMachineTopBlockEntity extends BlockEntity implements BotariumEnergyBlock<WrappedBlockEnergyContainer>, BasicContainer, WorldlyContainer {
    private WrappedBlockEnergyContainer energyContainer;
    private WrappedBlockEnergyContainer belowContainer;
    private ContainerMachineBlockEntity belowEntity;

    public DoubleMachineTopBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntityTypes.DOUBLE_MACHINE_TOP.get(), pos, blockState);
    }

    @Override
    public WrappedBlockEnergyContainer getEnergyStorage() {
        if (this.energyContainer != null) return this.energyContainer;
        return this.energyContainer = new WrappedBlockEnergyContainer(this, new SimpleEnergyContainer(0) {
            @Override
            public long insertEnergy(long maxAmount, boolean simulate) {
                return getBelowContainer().insertEnergy(maxAmount, simulate);
            }

            @Override
            public long extractEnergy(long maxAmount, boolean simulate) {
                return getBelowContainer().extractEnergy(maxAmount, simulate);
            }

            @Override
            public long getStoredEnergy() {
                return getBelowContainer().getStoredEnergy();
            }

            @Override
            public long getMaxCapacity() {
                return getBelowContainer().getMaxCapacity();
            }

            @Override
            public long maxInsert() {
                return getBelowContainer().maxInsert();
            }

            @Override
            public long maxExtract() {
                return getBelowContainer().maxExtract();
            }

            @Override
            public boolean allowsInsertion() {
                return getBelowContainer().allowsInsertion();
            }

            @Override
            public boolean allowsExtraction() {
                return getBelowContainer().allowsExtraction();
            }

            @Override
            public EnergySnapshot createSnapshot() {
                return getBelowContainer().createSnapshot();
            }

            @Override
            public void clearContent() {
                getBelowContainer().clearContent();
            }
        });
    }

    public void serverTick(ServerLevel level, long time, BlockState state, BlockPos pos) {
        if (belowEntity == null && level.getBlockEntity(pos.below()) instanceof ContainerMachineBlockEntity entity) {
            belowEntity = entity;
        }
        if (this.energyContainer == null) return;
        if (state.getBlock() instanceof DoubleMachineBlock machine && machine.isGenerator()) {
//            TransferUtils.pushItemsNearby(this, belowEntity, new int[0], belowEntity.getSideConfig().get(0), Predicate.not(Direction.DOWN::equals)); // TODO
//            TransferUtils.pullItemsNearby(this, belowEntity, new int[0], belowEntity.getSideConfig().get(0), Predicate.not(Direction.DOWN::equals)); // TODO
//            TransferUtils.pushEnergyNearby(this, this.getEnergyStorage().maxExtract(), belowEntity.getSideConfig().get(1), Predicate.not(Direction.DOWN::equals));
//            TransferUtils.pullEnergyNearby(this, this.getEnergyStorage().maxInsert(), belowEntity.getSideConfig().get(1), Predicate.not(Direction.DOWN::equals));
        }
    }

    public WrappedBlockEnergyContainer getBelowContainer() {
        if (this.belowContainer != null) return this.belowContainer;
        return this.belowContainer = ((BotariumEnergyBlock<WrappedBlockEnergyContainer>) level.getBlockEntity(this.getBlockPos().below())).getEnergyStorage();
    }

    @Override
    public NonNullList<ItemStack> items() {
        return belowEntity.items();
    }

    @Override
    public void update() {
        belowEntity.update();
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return belowEntity.stillValid(player);
    }

    @Override
    public int @NotNull [] getSlotsForFace(@NotNull Direction side) {
        return belowEntity.getSlotsForFace(side);
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, @NotNull ItemStack itemStack, @Nullable Direction direction) {
        return belowEntity.canPlaceItemThroughFace(index, itemStack, direction);
    }

    @Override
    public boolean canTakeItemThroughFace(int index, @NotNull ItemStack stack, @NotNull Direction direction) {
        return belowEntity.canTakeItemThroughFace(index, stack, direction);
    }
}
