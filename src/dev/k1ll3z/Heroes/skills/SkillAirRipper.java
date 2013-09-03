package dev.k1ll3z.Heroes.skills;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.SkillResult;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.skill.ActiveSkill;
import com.herocraftonline.heroes.characters.skill.SkillConfigManager;
import com.herocraftonline.heroes.characters.skill.SkillType;
import com.herocraftonline.heroes.characters.skill.SkillSetting;
import java.util.Iterator;
import java.util.List;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class SkillAirRipper extends ActiveSkill
{
  public SkillAirRipper(Heroes plugin)
  {
    super(plugin, "AirRipper");
    setDescription("Cuts the air and damages enemies for %1 damage around you");
    setUsage("/skill AirRipper");
    setArgumentRange(0, 0);
    setIdentifiers(new String[] { "skill airripper" });
    setTypes(new SkillType[] { SkillType.HARMFUL, SkillType.SILENCABLE });
  }

  public ConfigurationSection getDefaultConfig()
  {
    ConfigurationSection node = super.getDefaultConfig();
    node.set(SkillSetting.RADIUS.node(), Integer.valueOf(10));
    node.set(SkillSetting.DAMAGE.node(), Integer.valueOf(10));
    return node;
  }

  public SkillResult use(Hero hero, String[] args)
  {
    Player player = hero.getPlayer();
    int radius = SkillConfigManager.getUseSetting(hero, this, SkillSetting.RADIUS, 10, false);
    List entities = hero.getPlayer().getNearbyEntities(radius, radius, radius);
    Iterator i$ = entities.iterator();

    while (i$.hasNext())
    {
      Entity entity = (Entity)i$.next();
      if ((entity instanceof LivingEntity));
      LivingEntity target = (LivingEntity)entity;
      if (!target.equals(player))
      {
        int damage = SkillConfigManager.getUseSetting(hero, this, SkillSetting.DAMAGE, 10, false);
        addSpellTarget(target, hero);
        damageEntity(target, player, damage, DamageCause.MAGIC);
      }
    }

    broadcastExecuteText(hero);
    return SkillResult.NORMAL;
  }

  public String getDescription(Hero hero)
  {
    int radius = SkillConfigManager.getUseSetting(hero, this, SkillSetting.RADIUS, 20, false);
    int damage = SkillConfigManager.getUseSetting(hero, this, SkillSetting.DAMAGE, 10, false);
    return getDescription().replace("$1", damage + "");
  }
}

/* Location:           C:\Users\Andrew\Desktop\K1ll3z\bin\decomp.jar
 * Qualified Name:     dev.k1ll3z.Heroes.skills.SkillAirRipper
 * JD-Core Version:    0.6.2
 */