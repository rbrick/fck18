package io.rcw.fck18;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.Team;

public final class Hooks {

    public static boolean canRenderName(EntityLivingBase entity) {
        EntityPlayerSP entityplayersp = Minecraft.getMinecraft().thePlayer;
        boolean flag = !entity.isInvisibleToPlayer(entityplayersp);

        if (entity instanceof EntityPlayer && entity != entityplayersp) {
            Team team = entity.getTeam();
            Team team1 = entityplayersp.getTeam();

            if (team != null) {
                Team.EnumVisible team$enumvisible = team.getNameTagVisibility();

                switch (team$enumvisible) {
                    case ALWAYS:
                        return flag;
                    case NEVER:
                        return false;
                    case HIDE_FOR_OTHER_TEAMS:
                        return team1 == null ? flag : team.isSameTeam(team1) && (team.getSeeFriendlyInvisiblesEnabled() || flag);
                    case HIDE_FOR_OWN_TEAM:
                        return team1 == null ? flag : !team.isSameTeam(team1) && flag;
                    default:
                        return true;
                }
            }
        }
        return Minecraft.isGuiEnabled() && entity != Minecraft.getMinecraft().getRenderManager().livingPlayer && flag && entity.riddenByEntity == null;
    }


}
