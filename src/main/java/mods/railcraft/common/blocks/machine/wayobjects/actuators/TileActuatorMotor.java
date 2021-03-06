/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.machine.wayobjects.actuators;

import mods.railcraft.api.signals.IReceiverTile;
import mods.railcraft.api.signals.SignalAspect;
import mods.railcraft.api.signals.SignalController;
import mods.railcraft.api.signals.SimpleSignalReceiver;
import mods.railcraft.common.blocks.machine.IEnumMachine;
import mods.railcraft.common.blocks.machine.interfaces.ITileAspectResponder;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.gui.GuiHandler;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.network.IGuiReturnHandler;
import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.network.RailcraftOutputStream;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;

public class TileActuatorMotor extends TileActuatorSecured implements ITileAspectResponder, IGuiReturnHandler, IReceiverTile {

    private final SimpleSignalReceiver receiver = new SimpleSignalReceiver(getLocalizationTag(), this);
    private boolean[] switchOnAspects = new boolean[SignalAspect.values().length];
    private boolean switchAspect;
    private boolean switchOnRedstone = true;

    public TileActuatorMotor() {
        switchOnAspects[SignalAspect.RED.ordinal()] = true;
    }

    @Nonnull
    @Override
    public IEnumMachine<?> getMachineType() {
        return ActuatorVariant.MOTOR;
    }

    @Override
    public boolean openGui(EntityPlayer player) {
        GuiHandler.openGui(EnumGui.SWITCH_MOTOR, player, world, getPos());
        return true;
    }

    @Override
    public void update() {
        super.update();
        if (Game.isClient(world)) {
            receiver.tickClient();
            return;
        }
        receiver.tickServer();
        boolean active = isSwitchAspect();
        if (switchAspect != active) {
            switchAspect = active;
        }
    }

    @Override
    public void onControllerAspectChange(SignalController con, SignalAspect aspect) {
    }

    @Override
    public void onNeighborBlockChange(@Nonnull IBlockState state, @Nonnull Block neighborBlock, BlockPos pos) {
        super.onNeighborBlockChange(state, neighborBlock, pos);
        boolean power = isBeingPoweredByRedstone();
        if (isPowered() != power) {
            setPowered(power);
        }
    }

    @Override
    public boolean canConnectRedstone(EnumFacing dir) {
        return true;
    }

    private boolean isSwitchAspect() {
        return switchOnAspects[receiver.getAspect().ordinal()];
    }

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound data) {
        super.writeToNBT(data);

        byte[] array = new byte[switchOnAspects.length];
        for (int i = 0; i < switchOnAspects.length; i++) {
            array[i] = (byte) (switchOnAspects[i] ? 1 : 0);
        }
        data.setByteArray("PowerOnAspect", array);

        data.setBoolean("switchAspect", switchAspect);

        data.setBoolean("switchOnRedstone", switchOnRedstone);

        receiver.writeToNBT(data);
        return data;
    }

    @Override
    public void readFromNBT(@Nonnull NBTTagCompound data) {
        super.readFromNBT(data);

        if (data.hasKey("PowerOnAspect")) {
            byte[] array = data.getByteArray("PowerOnAspect");
            for (int i = 0; i < switchOnAspects.length; i++) {
                switchOnAspects[i] = array[i] == 1;
            }
        }

        switchAspect = data.getBoolean("switchAspect");

        if (data.hasKey("switchOnRedstone"))
            switchOnRedstone = data.getBoolean("switchOnRedstone");

        receiver.readFromNBT(data);
    }

    @Override
    public void writePacketData(@Nonnull RailcraftOutputStream data) throws IOException {
        super.writePacketData(data);
        receiver.writePacketData(data);

        writeGuiData(data);
    }

    @Override
    public void readPacketData(@Nonnull RailcraftInputStream data) throws IOException {
        super.readPacketData(data);
        receiver.readPacketData(data);

        readGuiData(data, null);

        markBlockForUpdate();
    }

    @Override
    public void writeGuiData(@Nonnull RailcraftOutputStream data) throws IOException {
        super.writeGuiData(data);
        byte bits = 0;
        for (int i = 0; i < switchOnAspects.length; i++) {
            bits |= (switchOnAspects[i] ? 1 : 0) << i;
        }
        data.writeByte(bits);
        data.writeBoolean(switchOnRedstone);
    }

    @Override
    public void readGuiData(@Nonnull RailcraftInputStream data, EntityPlayer sender) throws IOException {
        super.readGuiData(data, sender);
        byte bits = data.readByte();
        for (int bit = 0; bit < switchOnAspects.length; bit++) {
            switchOnAspects[bit] = ((bits >> bit) & 1) == 1;
        }
        switchOnRedstone = data.readBoolean();
    }

    @Override
    public boolean doesActionOnAspect(SignalAspect aspect) {
        return switchOnAspects[aspect.ordinal()];
    }

    @Override
    public void doActionOnAspect(SignalAspect aspect, boolean trigger) {
        switchOnAspects[aspect.ordinal()] = trigger;
    }

    @Override
    public SimpleSignalReceiver getReceiver() {
        return receiver;
    }

    @Override
    public boolean shouldSwitch(@Nullable EntityMinecart cart) {
        return switchAspect || (shouldSwitchOnRedstone() && isPowered());
    }

    public boolean shouldSwitchOnRedstone() {
        return switchOnRedstone;
    }

    public void setSwitchOnRedstone(boolean switchOnRedstone) {
        this.switchOnRedstone = switchOnRedstone;
    }
}