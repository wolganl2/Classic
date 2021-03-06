package npc.model.residences;

import studio.lineage2.commons.collections.MultiValueSet;
import studio.lineage2.commons.util.Rnd;
import studio.lineage2.gameserver.Config;
import studio.lineage2.gameserver.model.AggroList;
import studio.lineage2.gameserver.model.Creature;
import studio.lineage2.gameserver.model.Playable;
import studio.lineage2.gameserver.model.Player;
import studio.lineage2.gameserver.model.quest.Quest;
import studio.lineage2.gameserver.model.quest.QuestEventType;
import studio.lineage2.gameserver.model.quest.QuestState;
import studio.lineage2.gameserver.templates.npc.NpcTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author VISTALL
 * @date 19:28/23.06.2011
 */
public class QuestSiegeGuardInstance extends SiegeGuardInstance
{
	private static final long serialVersionUID = 1L;

	public QuestSiegeGuardInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	@Override
	public void onDeath(Creature lastAttacker)
	{
		super.onDeath(lastAttacker);

		Player killer = lastAttacker.getPlayer();
		if(killer == null)
		{
			return;
		}

		Map<Playable, AggroList.HateInfo> aggroMap = getAggroList().getPlayableMap();

		Set<Quest> quests = getTemplate().getEventQuests(QuestEventType.MOB_KILLED_WITH_QUEST);
		if(quests != null && quests.size() > 0)
		{
			List<Player> players = null; // массив с игроками, которые могут быть заинтересованы в квестах
			if(isRaid() && Config.ALT_NO_LASTHIT) // Для альта на ластхит берем всех игроков вокруг
			{
				players = new ArrayList<Player>();
				for(Playable pl : aggroMap.keySet())
				{
					if(!pl.isDead() && (isInRangeZ(pl, Config.ALT_PARTY_DISTRIBUTION_RANGE) || killer.isInRangeZ(pl, Config.ALT_PARTY_DISTRIBUTION_RANGE)))
					{
						players.add(pl.getPlayer());
					}
				}
			}
			else if(killer.getParty() != null) // если пати то собираем всех кто подходит
			{
				players = new ArrayList<Player>(killer.getParty().getMemberCount());
				for(Player pl : killer.getParty().getPartyMembers())
				{
					if(!pl.isDead() && (isInRangeZ(pl, Config.ALT_PARTY_DISTRIBUTION_RANGE) || killer.isInRangeZ(pl, Config.ALT_PARTY_DISTRIBUTION_RANGE)))
					{
						players.add(pl);
					}
				}
			}

			for(Quest quest : quests)
			{
				Player toReward = killer;
				if(quest.getPartyType() != Quest.PARTY_NONE && players != null)
				{
					if(isRaid() || quest.getPartyType() == Quest.PARTY_ALL) // если цель рейд или квест для всей пати награждаем всех участников
					{
						for(Player pl : players)
						{
							QuestState qs = pl.getQuestState(quest.getId());
							if(qs != null && !qs.isCompleted())
							{
								quest.notifyKill(this, qs);
							}
						}
						toReward = null;
					}
					else
					{ // иначе выбираем одного
						List<Player> interested = new ArrayList<Player>(players.size());
						for(Player pl : players)
						{
							QuestState qs = pl.getQuestState(quest.getId());
							if(qs != null && !qs.isCompleted()) // из тех, у кого взят квест
							{
								interested.add(pl);
							}
						}

						if(interested.isEmpty())
						{
							continue;
						}

						toReward = interested.get(Rnd.get(interested.size()));
						if(toReward == null)
						{
							toReward = killer;
						}
					}
				}

				if(toReward != null)
				{
					QuestState qs = toReward.getQuestState(quest.getId());
					if(qs != null && !qs.isCompleted())
					{
						quest.notifyKill(this, qs);
					}
				}
			}
		}
	}
}
