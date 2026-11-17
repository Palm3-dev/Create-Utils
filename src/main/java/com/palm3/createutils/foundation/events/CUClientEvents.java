package com.palm3.createutils.foundation.events;

import com.palm3.createutils.content.items.screwdriver.ScrewdriverEvents;
import com.palm3.createutils.content.items.screwdriver.ScrewdriverItem;
import com.palm3.createutils.register.CUItems;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RegisterItemDecorationsEvent;

@EventBusSubscriber(Dist.CLIENT)
public class CUClientEvents {

    @SubscribeEvent
    public static void onMouseScrollInWorld(InputEvent.MouseScrollingEvent event) {
        ScrewdriverEvents.onMouseScrollInWorld(event);
    }

    @SubscribeEvent
    public static void onLeftClickBlock(InputEvent.InteractionKeyMappingTriggered event) {
        ScrewdriverEvents.onLeftClickBlock(event);
    }
}
