
package net.runelite.client.plugins.BarbFlyFishing;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.plugins.menuentryswapper.MenuEntrySwapperConfig;

@ConfigGroup("BarbFlyFishing")
public interface BarbFlyFishingConfig extends Config {

	enum BarbFishLocation
	{
		NONE,
		BARBARIAN_VILLAGE,
		BARBARIAN_OUTPOST,
		LUMBRIDGE_SWAMP
	}

	@ConfigItem(
			position = 1,
			keyName = "Location",
			name = "Select",
			description = "Fishing Location"
	)
	default BarbFishLocation barblocation()
	{
		return BarbFishLocation.NONE;
	}

	@ConfigItem(
			position = 2,
			keyName = "When to Drop?",
			name = "When to Drop?",
			description = "When to Drop?"
	)
	default int dropnum() {
		return 25;
	}

	@ConfigSection(
			name = "Debug",
			description = "Developer options for debugging",
			position = 3,
			closedByDefault = true
	)
	String DebugSection = "Debug";

	@ConfigItem(
			position = 4,
			keyName = "Debug GUI",
			name = "Debug GUI",
			description = "Debug GUI",
			section = DebugSection
	)
	default boolean debug() {
		return false;
	}

	@ConfigItem(
			position = 5,
			keyName = "Boundx",
			name = "Boundx",
			description = "Boundx",
			section = DebugSection
	)
	default int boundx() {
		return 4;
	}

	@ConfigItem(
			position = 6,
			keyName = "Boundy",
			name = "Boundy",
			description = "Boundy",
			section = DebugSection
	)
	default int boundy() {
		return 4;
	}

	@ConfigItem(
			position = 7,
			keyName = "offsetx",
			name = "offsetx",
			description = "offsetx",
			section = DebugSection
	)
	default int offsetx() {
		return 0;
	}

	@ConfigItem(
			position = 8,
			keyName = "offsety",
			name = "offsety",
			description = "offsety",
			section = DebugSection
	)
	default int offsety() {
		return 0;
	}

}

