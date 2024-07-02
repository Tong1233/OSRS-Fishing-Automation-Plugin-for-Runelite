
package net.runelite.client.plugins.BarbFlyFishing;

//import com.github.joonasvali.naturalmouse.api.MouseMotionFactory;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Provides;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.inject.Inject;

//import io.github.anseki.drgriffin.DrGriffin;
import lombok.Getter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.runelite.api.*;

import static net.runelite.api.ObjectID.CANNON_BASE;


import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.client.Notifier;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.BarbFlyFishing.naturalmouse.api.MouseMotionFactory;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;

@PluginDescriptor(
	name = "BarbFlyFishing",
	description = "Fishing!!!",
	tags = {"combat", "notifications", "ranged", "overlay"}
)
public class BarbFlyFishingPlugin extends Plugin
{
	private static final Pattern NUMBER_PATTERN = Pattern.compile("([0-9]+)");

	private ExecutorService executorService;

	@Getter
	private List<WorldPoint> spotPoints = new ArrayList<>();

	@Inject
	private ItemManager itemManager;

	@Inject
	private InfoBoxManager infoBoxManager;

	@Inject
	private Notifier notifier;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private BarbFlyFishingOverlay FishOverlay;

	@Inject
	private BarbFlyFishingConfig config;

	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private MouseMotionFactory factory;

	private static final Set<String> EVENT_OPTIONS = ImmutableSet.of(
			"Talk-to",
			"Dismiss"
	);

	private static final Set<Integer> EVENT_NPCS = ImmutableSet.of(
			NpcID.BEE_KEEPER_6747,
			NpcID.CAPT_ARNAV,
			NpcID.DR_JEKYLL, NpcID.DR_JEKYLL_314,
			NpcID.DRUNKEN_DWARF,
			NpcID.DUNCE_6749,
			NpcID.EVIL_BOB, NpcID.EVIL_BOB_6754,
			NpcID.FLIPPA_6744,
			NpcID.FREAKY_FORESTER_6748,
			NpcID.FROG_5429,
			NpcID.GENIE, NpcID.GENIE_327,
			NpcID.GILES, NpcID.GILES_5441,
			NpcID.LEO_6746,
			NpcID.MILES, NpcID.MILES_5440,
			NpcID.MYSTERIOUS_OLD_MAN_6750, NpcID.MYSTERIOUS_OLD_MAN_6751,
			NpcID.MYSTERIOUS_OLD_MAN_6752, NpcID.MYSTERIOUS_OLD_MAN_6753,
			NpcID.NILES, NpcID.NILES_5439,
			NpcID.PILLORY_GUARD,
			NpcID.POSTIE_PETE_6738,
			NpcID.QUIZ_MASTER_6755,
			NpcID.RICK_TURPENTINE, NpcID.RICK_TURPENTINE_376,
			NpcID.SANDWICH_LADY,
			NpcID.SERGEANT_DAMIEN_6743
	);

	@Provides
    BarbFlyFishingConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(BarbFlyFishingConfig.class);
	}

	@Override
	protected void startUp() throws Exception
	{
		overlayManager.add(FishOverlay);
		executorService = Executors.newSingleThreadExecutor();
	}

	@Override
	protected void shutDown() throws Exception
	{
		overlayManager.remove(FishOverlay);
		executorService.shutdownNow();
		spotPoints.clear();
	}

	@Subscribe
	public void onItemContainerChanged(ItemContainerChanged event)
	{
		if (event.getItemContainer() != client.getItemContainer(InventoryID.INVENTORY))
		{
			return;
		}
	}


	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		return;
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		if (gameStateChanged.getGameState() != GameState.LOGGED_IN)
		{
			return;
		}

	}

	@Subscribe
	public void onGameObjectSpawned(GameObjectSpawned event)
	{
		GameObject gameObject = event.getGameObject();
		Player localPlayer = client.getLocalPlayer();
	}


	@Subscribe
	public void onChatMessage(ChatMessage event)
	{
		if (event.getType() != ChatMessageType.SPAM && event.getType() != ChatMessageType.GAMEMESSAGE)
		{
			return;
		}


	}

	@Subscribe
	public void onGameTick(GameTick event) throws AWTException {
		return;
	}

	@Subscribe
	public void onInteractingChanged(InteractingChanged event) throws AWTException {
		Actor source = event.getSource();
		Actor target = event.getTarget();
		Player player = client.getLocalPlayer();

		// Check that the npc is interacting with the player and the player isn't interacting with the npc, so
		// that the notification doesn't fire from talking to other user's randoms
		if (player == null
				|| target != player
				|| player.getInteracting() == source
				|| !(source instanceof NPC)
				|| !EVENT_NPCS.contains(((NPC) source).getId()))
		{
			return;
		}

	}
}
