package services;

import studio.lineage2.gameserver.handler.bypass.Bypass;
import studio.lineage2.gameserver.model.Player;
import studio.lineage2.gameserver.model.instances.NpcInstance;
import studio.lineage2.gameserver.network.l2.components.SystemMsg;
import studio.lineage2.gameserver.network.l2.s2c.SystemMessage;
import studio.lineage2.gameserver.utils.ItemFunctions;

public class ManaRegen
{
	private static final int ADENA = 57;
	private static final long PRICE = 5; //5 аден за 1 МП

	@Bypass("services.ManaRegen:DoManaRegen")
	public void DoManaRegen(Player player, NpcInstance npc, String[] param)
	{
		long mp = (long) Math.floor(player.getMaxMp() - player.getCurrentMp());
		long fullCost = mp * PRICE;
		if(fullCost <= 0)
		{
			player.sendPacket(SystemMsg.NOTHING_HAPPENED);
			return;
		}
		if(ItemFunctions.getItemCount(player, ADENA) >= fullCost)
		{
			ItemFunctions.deleteItem(player, ADENA, fullCost);
			player.sendPacket(new SystemMessage(SystemMessage.S1_MPS_HAVE_BEEN_RESTORED).addNumber(mp));
			player.setCurrentMp(player.getMaxMp());
		}
		else
		{
			player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
		}
	}
}