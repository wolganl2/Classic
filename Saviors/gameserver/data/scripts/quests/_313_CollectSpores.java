package quests;

import studio.lineage2.commons.util.Rnd;
import studio.lineage2.gameserver.model.instances.NpcInstance;
import studio.lineage2.gameserver.model.quest.Quest;
import studio.lineage2.gameserver.model.quest.QuestState;

public final class _313_CollectSpores extends Quest
{
	//NPC
	public final int Herbiel = 30150;
	//Mobs
	public final int SporeFungus = 20509;
	//Quest Items
	public final int SporeSac = 1118;

	public _313_CollectSpores()
	{
		super(PARTY_NONE, REPEATABLE);

		addStartNpc(Herbiel);
		addTalkId(Herbiel);
		addKillId(SporeFungus);
		addQuestItem(SporeSac);

		addLevelCheck(8, 13);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		if(event.equalsIgnoreCase("green_q0313_05.htm"))
		{
			st.setCond(1);
		}
		return event;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int cond = st.getCond();

		switch(npcId)
		{
			case Herbiel:
				if(cond == 0)
				{
					if(checkStartCondition(st.getPlayer()))
						htmltext = "green_q0313_03.htm";
					else
						htmltext = "green_q0313_02.htm";
				}
				else if(cond == 1)
					htmltext = "green_q0313_06.htm";
				else if(cond == 2)
				{
					st.takeItems(SporeSac, -1);
					st.giveItems(ADENA_ID, 500);
					htmltext = "green_q0313_07.htm";
					st.finishQuest();
				}
				break;
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int cond = st.getCond();
		if(cond == 1)
		{
			st.rollAndGive(SporeSac, 1, 1, 10, 70);
			if(st.getQuestItemsCount(SporeSac) >= 10)
				st.setCond(2);
		}
		return null;
	}
}