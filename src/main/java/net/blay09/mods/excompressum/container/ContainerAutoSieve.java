package net.blay09.mods.excompressum.container;

import net.blay09.mods.excompressum.tile.TileEntityAutoSieve;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerAutoSieve extends Container {

    private final TileEntityAutoSieve tileEntity;

    public ContainerAutoSieve(InventoryPlayer inventoryPlayer, TileEntityAutoSieve tileEntity) {
        this.tileEntity = tileEntity;
        addSlotToContainer(new SlotAutoSieveInput(tileEntity, 0, 8, 35));
        addSlotToContainer(new SlotAutoSieveBook(tileEntity, 21, 8, 62));

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 5; j++) {
                addSlotToContainer(new SlotOutput(tileEntity, 1 + (i * 5) + j, 57 + (j * 18), 8 + (i * 18)));
            }
        }

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                addSlotToContainer(new Slot(inventoryPlayer, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (int i = 0; i < 9; i++) {
            addSlotToContainer(new Slot(inventoryPlayer, i, 8 + i * 18, 142));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer entityPlayer) {
        return tileEntity.isUseableByPlayer(entityPlayer);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer entityPlayer, int slotNumber) {
        ItemStack itemStack = null;
        Slot slot = getSlot(slotNumber);
        if (slot != null && slot.getHasStack()) {
            ItemStack slotStack = slot.getStack();
            itemStack = slotStack.copy();
            if (slotNumber <= 21) {
                if (!mergeItemStack(slotStack, 21, 57, true)) {
                    return null;
                }
            } else if (tileEntity.isItemValidForSlot(0, slotStack)) {
                if (!mergeItemStack(slotStack, 0, 1, false)) {
                    return null;
                }
            } else if (tileEntity.isItemValidForSlot(21, slotStack)) {
                if (!mergeItemStack(slotStack, 21, 22, false)) {
                    return null;
                }
            }
            if (slotStack.stackSize == 0) {
                slot.putStack(null);
            } else {
                slot.onSlotChanged();
            }
            if (slotStack.stackSize == itemStack.stackSize) {
                return null;
            }
            slot.onPickupFromSlot(entityPlayer, slotStack);
        }
        return itemStack;
    }
}
