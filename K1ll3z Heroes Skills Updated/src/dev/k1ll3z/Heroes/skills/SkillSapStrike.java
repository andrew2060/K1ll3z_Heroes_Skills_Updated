package dev.k1ll3z.Heroes.skills;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.SkillResult;
import com.herocraftonline.heroes.api.events.HeroRegainHealthEvent;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.skill.SkillConfigManager;
import com.herocraftonline.heroes.characters.skill.SkillType;
import com.herocraftonline.heroes.characters.skill.TargettedSkill;
import com.herocraftonline.heroes.util.Messaging;
import com.herocraftonline.heroes.characters.skill.SkillSetting;
import org.bukkit.Server;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.plugin.PluginManager;

public class SkillSapStrike extends TargettedSkill
{
  public SkillSapStrike(Heroes plugin)
  {
    super(plugin, "SapStrike");
    setDescription("You impale your target with your blade and steal health from them!");
    setUsage("/skill sapstrike <target>");
    setArgumentRange(0, 1);
    setIdentifiers(new String[] { "skill sapstrike" });
    setTypes(new SkillType[] { SkillType.LIGHT, SkillType.HEAL, SkillType.SILENCABLE, SkillType.HARMFUL });
  }

  public ConfigurationSection getDefaultConfig()
  {
    ConfigurationSection node = super.getDefaultConfig();
    node.set(SkillSetting.HEALTH.node(), Integer.valueOf(10));
    node.set(SkillSetting.DAMAGE.node(), Integer.valueOf(10));
    return node;
  }

  public SkillResult use(Hero hero, LivingEntity target, String[] args)
  {
    Player player = hero.getPlayer();

    int hpPlus = SkillConfigManager.getUseSetting(hero, this, SkillSetting.HEALTH, 10, false);
    HeroRegainHealthEvent hrhEvent = new HeroRegainHealthEvent(hero, hpPlus, this);
    this.plugin.getServer().getPluginManager().callEvent(hrhEvent);
    if (hrhEvent.isCancelled()) {
      Messaging.send(player, "Unable to regain health at the time!", new Object[0]);
      return SkillResult.CANCELLED;
    }
    hero.getEntity().setHealth(hero.getEntity().getHealth() + hrhEvent.getAmount());
    addSpellTarget(target, hero);
    int damage = SkillConfigManager.getUseSetting(hero, this, SkillSetting.DAMAGE, 15, false);
    damageEntity(target, player, damage, DamageCause.MAGIC);

    broadcastExecuteText(hero, target);
    return SkillResult.NORMAL;
  }

  public String getDescription(Hero hero)
  {
    return getDescription();
  }
}

/* Location:           C:\Users\Andrew\Desktop\K1ll3z\bin\decomp.jar
 * Qualified Name:     dev.k1ll3z.Heroes.skills.SkillSapStrike
 * JD-Core Version:    0.6.2
 */