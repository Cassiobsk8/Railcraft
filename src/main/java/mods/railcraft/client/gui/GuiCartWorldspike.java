/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.client.gui;

import mods.railcraft.common.carts.EntityCartWorldspike;
import mods.railcraft.common.core.RailcraftConstants;
import mods.railcraft.common.gui.containers.ContainerWorldspike;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.text.translation.I18n;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class GuiCartWorldspike extends EntityGui {
    private static final DecimalFormat timeFormatter = (DecimalFormat) NumberFormat.getInstance(Locale.ENGLISH);
    private final EntityCartWorldspike cartWorldspike;
    private final ContainerWorldspike container;

    static {
        timeFormatter.applyPattern("#,##0.00");
    }

    public GuiCartWorldspike(InventoryPlayer playerInv, EntityCartWorldspike worldspike) {
        super(worldspike, new ContainerWorldspike(playerInv, worldspike), RailcraftConstants.GUI_TEXTURE_FOLDER + "gui_single_slot.png");
        xSize = 176;
        ySize = 140;
        cartWorldspike = worldspike;
        container = (ContainerWorldspike) inventorySlots;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        String label = cartWorldspike.getName();
        int sWidth = fontRenderer.getStringWidth(label);
        int sPos = xSize / 2 - sWidth / 2;
        fontRenderer.drawString(label, sPos, 6, 0x404040);
        fontRenderer.drawString(LocalizationPlugin.translate("gui.railcraft.worldspike.fuel"), 85, 24, 0x404040);
        fontRenderer.drawString(LocalizationPlugin.translate("gui.railcraft.worldspike.fuel.remaining", timeFormatter.format((double) container.minutesRemaining / 60.0)), 85, 35, 0x404040);
        fontRenderer.drawString(I18n.translateToLocal("container.inventory"), 8, (ySize - 96) + 2, 0x404040);
    }

}
