package me.sirgregg.osubot.cmdsystem.commands;

import me.sirgregg.osubot.OsuBot;
import me.sirgregg.osubot.cmdsystem.Command;
import me.sirgregg.osubot.util.config.Configuration;
import me.sirgregg.osubot.util.helpers.EmbedUtil;
import me.sirgregg.osubot.util.objects.Beatmap;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.awt.Color;
import java.util.List;

public class BeatmapCommand extends Command {
	private Configuration configuration = OsuBot.getConfiguration();
	public BeatmapCommand() {
		super(new String[] { "beatmap", "bm", "map" }, "map <id> <mode> [mods] [more] ", "Displays information about a beatmap.");
	}

	@Override
	public void execute(MessageReceivedEvent e, Color color, String[] args) {
		// 0 -> beatmap id
		// 1 -> mode
		if (args.length < 2) {
			e.getMessage().editMessage(EmbedUtil.createEmbed(color, "**Correct Usage:**\n" + configuration.getLead() + getUsage())).queue();
			return;
		}

		boolean more = false;

		if (args.length >= 4 && args[3].equalsIgnoreCase("more")) { // If it's more than the base args and the 3rd arg (args[2]) is "more"
			more = true;
		}

		String rawId = args[0];
		String rawMode = args[1];

		int id = -1;
		try {
			id = Integer.parseInt(rawId);
		} catch (NumberFormatException ex) {
			e.getMessage().editMessage(EmbedUtil.createEmbed(color, "**Correct Usage:**\n" + getUsage())).queue();
			return;
		}

		String mode = OsuBot.getOsu().formatMode(rawMode);

		if (mode == null) {
			e.getMessage().editMessage(EmbedUtil.createEmbed(color,
					"**Valid Modes: **\n" +
							"std, standard -> osu!standard\n" +
							"tak, taiko -> Taiko" + "\n" +
							"ctb, catch -> Catch the Beat" + "\n" +
							"man, mania -> Mania\n")).queue();
			return;
		}

		String mods = null;
		if (args.length >= 3) {
			mods = args[2];
		}

		Beatmap beatmap = OsuBot.getOsu().getBeatmap(rawId, rawMode);
		if (beatmap == null) { // Mode is invalid or User does not have stats
			e.getMessage().editMessage(EmbedUtil.createEmbed(color,
					"**Failed to load statistics: **\n" +
							"That beatmap does not have statistics for that mode.")).queue();
			return;
		}

		if (!more) {
			e.getMessage().editMessage(EmbedUtil.createEmbed(color,
					"**CREATOR: **" + beatmap.getCreator() + "\n" +
							"**VERSION: **" + beatmap.getVersion() + "\n" +
							"**COMPOSER: **" + beatmap.getArtist() + "\n" +
							"**SONG: **" + beatmap.getTitle() + "\n\n" +

							"**DIFFICULTY: **" + beatmap.getDifficultyRating() +
							"*, CS" + beatmap.getDiffCircleSize() +
							", AR" + beatmap.getDiffApproach() +
							", OD" + beatmap.getDifOverall() +
							", HP" + beatmap.getDiffDrain() +
							", BPM" + beatmap.getBpm() + "\n" +
							"**DRAIN LENGTH: **" + beatmap.getTotalLength() + " (" + beatmap.getMaxCombo() + "x combo)\n" +
							"**MAPSET: **https://osu.ppy.sh/s/" + beatmap.getBeatmapsetId()
			)).queue();
		} else {
			List<String> info = OsuBot.getOsu().getInfo(rawId, rawMode, null, mods, null, null, null);
			String pp = info.get(28);

			e.getMessage().editMessage(EmbedUtil.createEmbed(color,
					"**CREATOR: **" + beatmap.getCreator() + "\n" +
							"**VERSION: **" + beatmap.getVersion() + "\n" +
							"**STATUS: **" + OsuBot.getOsu().parseApproved(beatmap.getApproved()) + "\n" +
							"**SUBMITTED: **" + beatmap.getApprovedDate() + "\n" +
							"**UPDATED: **" + beatmap.getLastUpdate() + "\n\n" +

							"**COMPOSER: **" +  beatmap.getArtist() + "\n" +
							"**SONG: **" + beatmap.getTitle() + "\n\n" +

							"**STARS: **" + beatmap.getDifficultyRating() + "*\n" +
							"**CS: **" + beatmap.getDiffCircleSize() + "\n" +
							"**AR: **" + beatmap.getDiffApproach() + "\n" +
							"**OD: **" + beatmap.getDifOverall() + "\n" +
							"**HP: **" + beatmap.getDiffDrain() + "\n" +
							"**BPM: **" + beatmap.getBpm() + "\n" +
							"**DRAIN LENGTH: **" + beatmap.getHitLength() + "/" + beatmap.getTotalLength() + " (" + beatmap.getMaxCombo() + "x combo)\n\n" +

							"**MAX PP: **" + pp + "\n" + // NOTE: Doing just one \n is just a sneaky way of negating the effects of the added \n from the api. Yes, it's lazy, no, I don't plan on changing it.

							"**MAPSET: **https://osu.ppy.sh/s/" + beatmap.getBeatmapsetId()
			)).queue();
		}
	}
}
