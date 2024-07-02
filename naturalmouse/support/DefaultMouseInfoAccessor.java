package net.runelite.client.plugins.BarbFlyFishing.naturalmouse.support;

import net.runelite.client.plugins.BarbFlyFishing.naturalmouse.api.MouseInfoAccessor;

import java.awt.*;

public class DefaultMouseInfoAccessor implements MouseInfoAccessor {

  @Override
  public Point getMousePosition() {
    return MouseInfo.getPointerInfo().getLocation();
  }
}
