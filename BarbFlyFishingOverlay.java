package net.runelite.client.plugins.BarbFlyFishing;

import com.sun.jna.platform.WindowUtils;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.api.annotations.Component;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.game.NPCManager;
import net.runelite.client.plugins.BarbFlyFishing.api.MouseMotionFactory;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import static net.runelite.api.MenuAction.RUNELITE_OVERLAY_CONFIG;
import static net.runelite.client.ui.overlay.OverlayManager.OPTION_CONFIGURE;

class BarbFlyFishingOverlay extends OverlayPanel {
	private static final int INVENTORY_SIZE = 28;
	private static final int NEAREST_DEFAULT = 10000;
	private static final int TIMER_LIMIT = 5;
	private static final int IDLE_TIMER_LIMIT = 3000;
	private static final int BARBOUTPOST = 1542;
	private static final int LEAPINGTROUT = 11328;
	private static final int LEAPINGSALMON = 11330;
	private static final int LEAPINGSTURGEON = 11332;
	private static final int BARBVILLAGE = 1526;
	private static final int TROUT = 335;
	private static final int SALMON = 331;
	private static final int LUMBRIDGESWAMP = 1530;
	private static final int SHRIMP = 317;
	private static final int ANCHOVY = 321;

	private final Client client;
	private final BarbFlyFishingConfig config;
	private final BarbFlyFishingPlugin plugin;
	private final ThreadPoolExecutor executorService;
	private final List<Integer> coordx = new ArrayList<>();
	private final List<Integer> coordy = new ArrayList<>();
	private final Robot bot = new Robot();

	private int timer = TIMER_LIMIT;
	private int nearest = NEAREST_DEFAULT;
	private int itemsininv = 0;
	private int idletimer = 0;
	private boolean invon = true;
	private boolean MouseisMoving = false;
	private boolean AdjustMouse = true;
	private int adjustx = -1;
	private int adjusty = -1;
	private int mousetargx = 0;
	private int mousetargy = 0;
	private int targetID = 0;
	private int[] inventorytargets = {0, 0, 0};

	@Inject
	BarbFlyFishingOverlay(Client client, BarbFlyFishingConfig config, BarbFlyFishingPlugin plugin) throws AWTException {
		setPosition(OverlayPosition.DYNAMIC);
		setPriority(OverlayPriority.MED);
		executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
		this.client = client;
		this.config = config;
		this.plugin = plugin;
	}

	@Override
	public Dimension render(Graphics2D graphics) {

		if(config.debug())
			drawdebug(graphics);

		setlocation();

		Point CanvasMousePosition = client.getMouseCanvasPosition();
		java.awt.Point ScreenMouseCoord = MouseInfo.getPointerInfo().getLocation();

		if(AdjustMouse) {
			adjustx=-1;
			adjusty=-1;
		}

		if(CanvasMousePosition.getX() != -1 && CanvasMousePosition.getY() != -1 && AdjustMouse) {
			adjustx = (int) Math.round(ScreenMouseCoord.getX() - CanvasMousePosition.getX());
			adjusty = (int) Math.round(ScreenMouseCoord.getY() - CanvasMousePosition.getY());
			AdjustMouse = false;
		}

		if(adjustx == -1 || adjusty == -1)
			return null;

		if (client.getLocalPlayer().getAnimation() != -1) {
			idletimer = 0;
			return null;
		}

		idletimer++;

		Widget inventoryWidget = client.getWidget(149 << 16 | 0);

		if (inventoryWidget == null || inventoryWidget.isHidden()) {
			invon = false;
		} else {
			invon = true;
		}

		timer++;
		itemsininv = 0;

		if (idletimer > (IDLE_TIMER_LIMIT + Math.random() * 500)) {
			idletimer = 0;
			MouseisMoving = false;
		}

		List<Rectangle> rects = new ArrayList<>();

		for (int i = 0; i < INVENTORY_SIZE; ++i) {
			WidgetItem item = getWidgetItem(inventoryWidget, i);
			if (item.getId() == inventorytargets[0] || item.getId() == inventorytargets[1] || item.getId() == inventorytargets[2]) {
				Rectangle rect = item.getCanvasBounds();
				rects.add(rect);
				itemsininv += 1;
			}
		}

		if (client.getLocalPlayer().getAnimation() == -1 && timer > 5) {
			nearest = 10000;
			timer = 0;

			NPC target = null;
			Point targetpoint = null;
			LocalPoint localLocation = client.getLocalPlayer().getLocalLocation();

			for (NPC npc : client.getNpcs())
				if (npc.getId() == targetID) {
					if (localLocation.distanceTo(npc.getLocalLocation()) <= nearest) {
						target = npc;
						nearest = localLocation.distanceTo(npc.getLocalLocation());
					}
				}

			if (target != null) {
				targetpoint = target.getCanvasTextLocation(graphics, "0", target.getLogicalHeight());
			}

			if (targetpoint != null) {

				NPC finalTarget = target;

				if (MouseisMoving) {
					executorService.getQueue().clear();
					return null;
				}
				executorService.submit(() -> {
					MouseisMoving = true;
					if (itemsininv > config.dropnum()) {
						thread(200 + (int) (Math.random() * 300));
						for (Rectangle rect : rects) {
							if (!invon) { //switch to inventory if it isn't on
								bot.keyPress(KeyEvent.VK_F1);
								thread(200 + (int) (Math.random() * 300));
								bot.keyRelease(KeyEvent.VK_F1);
								thread(200 + (int) (Math.random() * 300));
							}
							bot.keyPress(KeyEvent.VK_SHIFT);
							int[] xy = getrandompoints(rect.getX() + rect.getWidth() / 2 - config.offsetx(),
									rect.getY() + rect.getHeight() / 2 - config.offsety(), rect.getWidth() * 0.6,
									rect.getHeight() * 0.6);

							coordx.add(xy[0]);
							coordy.add(xy[1]);
							simMouseClick(xy[0] + adjustx, xy[1] + adjusty);
						}
						thread(200 + (int) (Math.random() * 300));
						bot.keyRelease(KeyEvent.VK_SHIFT);
					}

					net.runelite.api.Point finals = Perspective
							.getCanvasTextLocation(client, graphics, finalTarget.getLocalLocation(), "0",
									0);

					int[] fin = getrandompoints(finals.getX(), finals.getY(), config.boundx(), config.boundy());

					mousetargx = finals.getX();
					mousetargy = finals.getY();

					coordx.add(fin[0]);
					coordy.add(fin[1]);

					simMouseClick(fin[0] + adjustx, fin[1] + adjusty);
					antiban();
					MouseisMoving = false;
				});
			}
		}

		return null;
	}

	private static WidgetItem getWidgetItem(Widget parentWidget, int idx) {
		assert parentWidget.isIf3();
		Widget wi = parentWidget.getChild(idx);
		return new WidgetItem(wi.getItemId(), wi.getItemQuantity(), wi.getBounds(), parentWidget, wi.getBounds());
	}

	private void setlocation()
	{
		if(config.barblocation() == BarbFlyFishingConfig.BarbFishLocation.NONE)
		{
			targetID = 0;
			Arrays.fill(inventorytargets, 0);
		}
		if(config.barblocation() == BarbFlyFishingConfig.BarbFishLocation.BARBARIAN_VILLAGE)
		{
			targetID = BARBVILLAGE;
			inventorytargets[0] = SALMON;
			inventorytargets[1] = TROUT;
		}
		if(config.barblocation() == BarbFlyFishingConfig.BarbFishLocation.BARBARIAN_OUTPOST)
		{
			targetID = BARBOUTPOST;
			inventorytargets[0] = LEAPINGTROUT;
			inventorytargets[1] = LEAPINGSALMON;
			inventorytargets[2] = LEAPINGSTURGEON;
		}
		if(config.barblocation() == BarbFlyFishingConfig.BarbFishLocation.LUMBRIDGE_SWAMP)
		{
			targetID = LUMBRIDGESWAMP;
			inventorytargets[0] = SHRIMP;
			inventorytargets[1] = ANCHOVY;
		}
	}

	private void drawdebug(Graphics2D graphics)
	{
		graphics.setStroke(new BasicStroke(2));

		for (int i = 0; i < coordx.size(); i++) {
			final Rectangle poly = new Rectangle(1, 1);
			Color color = Color.RED;
			graphics.setColor(color);
			poly.translate(coordx.get(i), coordy.get(i));
			graphics.draw(poly);
			graphics.fill(poly);
		}

		final Rectangle tpoly = new Rectangle(config.boundx(), config.boundy());
		Color color = Color.RED;
		graphics.setColor(color);
		tpoly.translate(mousetargx + config.offsetx() - config.boundx() / 2, mousetargy + config.offsety() - config.boundy() / 2);
		graphics.draw(tpoly);

		final Rectangle mid = new Rectangle(1, 1);
		graphics.setColor(color);
		mid.translate(mousetargx, mousetargy);
		graphics.draw(mid);
		graphics.fill(mid);

		Point mouselp = client.getMouseCanvasPosition();
		java.awt.Point realm = MouseInfo.getPointerInfo().getLocation();
		graphics.setColor(color.WHITE);
		graphics.drawString("DEBUG", 10, 100);
		graphics.drawString("AdjustX: " + String.valueOf(adjustx), 10, 120);
		graphics.drawString("AdjustY: " + String.valueOf(adjusty), 10, 130);
		graphics.drawString("MouseX: " + String.valueOf(realm.getX()), 10, 140);
		graphics.drawString("MouseY: " + String.valueOf(realm.getY()), 10, 150);
		graphics.drawString("CanvasX: " + String.valueOf(mouselp.getX()), 10, 160);
		graphics.drawString("CanvasY: " + String.valueOf(mouselp.getY()), 10, 170);

	}

	private void simMouseClick(int x, int y)
	{
		try {
			MouseMotionFactory.getDefault().move(x, y);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		thread(80 + (int) (Math.random() * 100));
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		thread(140 + (int) (Math.random() * 60));
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		thread(100 + (int) (Math.random() * 120));
	}

	private int[] getrandompoints(double midx, double midy, double boundx, double boundy) {
		Random r = new Random();
		if (boundx == 0)
			boundx = config.boundx();
		if (boundy == 0)
			boundx = config.boundy();

		midx = midx + config.offsetx();
		midy = midy + config.offsety();

		double x = 0;
		double y = 0;
		int[] randpoints = {0, 0};
		int i = 0;

		while (x < (midx - boundx / 2) || x > (midx + boundx / 2)) {
			x = midx + r.nextGaussian() * boundx / (3 + r.nextInt(3));
			i++;
			if (i > 10) {
				x = midx;
			}
		}

		i = 0;

		while (y < (midy - boundy / 2) || y > (midy + boundy / 2)) {
			y = midy + r.nextGaussian() * boundy / (3 + r.nextInt(3));
			i++;
			if (i > 10) {
				y = midy;
			}
		}
		randpoints[0] = (int) Math.round(x);
		randpoints[1] = (int) Math.round(y);

		return randpoints;
	}

	private void antiban() {
		int finalx = (int) (Math.random() * 150);
		int finaly = (int) (Math.random() * 1080);
		int finalx2 = (int) (Math.random() * 150);
		int finaly2 = (int) (Math.random() * 1080);

		try {
			MouseMotionFactory.getDefault().move(finalx, finaly);
			thread((int) (Math.random() * 1000));
			MouseMotionFactory.getDefault().move(finalx2, finaly2);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		thread(5000 + (int) (Math.random() * 3000));
	}

	private void thread(int time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
